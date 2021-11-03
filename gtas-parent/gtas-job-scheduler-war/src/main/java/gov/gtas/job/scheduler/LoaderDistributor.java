package gov.gtas.job.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import gov.gtas.job.wrapper.MessageWrapper;
import gov.gtas.model.FlightLoader;
import gov.gtas.model.LoaderWorker;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.repository.FlightLoaderRepository;
import gov.gtas.repository.LoaderWorkerRepository;
import gov.gtas.services.EventIdentifierFactory;
import gov.gtas.services.Utils;
import gov.gtas.summary.EventIdentifier;

@Component
public class LoaderDistributor {
	
	private static final Logger logger = LoggerFactory.getLogger(LoaderDistributor.class);

	private final JmsTemplate jmsTemplateFile;

	private final FlightLoaderRepository flightLoaderRepository;

	private final EventIdentifierFactory eventIdentifierFactory;

	private String errorstr;

	private LinkedMap<String, LoaderWorker> loaderNames;

	private int indexNumber = 0;

	private ReentrantLock lock = new ReentrantLock();
	
	private final LoaderWorkerRepository loaderWorkerRepository;

	private final ManualMover manualMover;
	
	private final List<String> loaderQueueNameOptions;

	public LoaderDistributor(JmsTemplate jmsTemplateFile, FlightLoaderRepository flightLoaderRepository,
			EventIdentifierFactory eventIdentifierFactory, 
			@Value("${message.dir.error}") String errorstr,
			LoaderWorkerRepository loaderWorkerRepository, 	
			@Value("${activemq.broker.url}")String brokerUrl, 	
			@Value("${inbound.loader.jms.queue}")String distributorQueue,
			@Value("${loader.queue.names}") Integer numberOfQueues) {
		this.jmsTemplateFile = jmsTemplateFile;
		this.flightLoaderRepository = flightLoaderRepository;
		this.eventIdentifierFactory = eventIdentifierFactory;
		this.errorstr = errorstr;
		this.loaderNames = new LinkedMap<>();
		this.loaderWorkerRepository = loaderWorkerRepository;
		//We do not want spring to manage this as it creates and destroys connections to the queue.
		this.manualMover = new ManualMover(brokerUrl, distributorQueue);
		List<String> loaderQueueNameOptions = new ArrayList<>();
		for (int i = 1; i <= numberOfQueues; i++) {
			loaderQueueNameOptions.add("GTAS_LOADER_" + i);
		}
		this.loaderQueueNameOptions = loaderQueueNameOptions;
		
	}

	@Scheduled(fixedDelayString = "${loader.purge.check}", initialDelayString = "30000")
	public void checkAllQueues() {
		logger.info("Starting queue check job");
		// Check all queues to retrieve stale messages
		Iterable<LoaderWorker> loaderWorkers = loaderWorkerRepository.findAll();
		Map<String, LoaderWorker> loadersInUse = new HashMap<>();
		for (LoaderWorker lw : loaderWorkers) {
			if (lw.getAssignedQueue() != null && StringUtils.isNotBlank(lw.getAssignedQueue())) {
				loadersInUse.put(lw.getAssignedQueue(), lw);
			} 
		}
		for (String queueName : loaderQueueNameOptions) {
			try {
				if (!loadersInUse.containsKey(queueName)) {
					logger.info("Queue " + queueName + " not active - Moving any old messages from " + queueName + " to GTAS distributor.");
					manualMover.purgeQueue(queueName);
				} else {
					LoaderWorker lw = loadersInUse.get(queueName);
					logger.info("Active loader: " + lw.getWorkerName() + " working on assigned queue " + lw.getAssignedQueue() + " .  No action taken.");
				}
			} catch (JMSException jme) {
				logger.error("Failed queue purge for queue: " + queueName, jme);
			}
		}
		logger.info("Queue check job completed");
	}

	@Scheduled(fixedDelayString = "${loader.register.check}", initialDelayString = "30000")
	public void loaderHealthChecks() {
		logger.info("Starting register/deregister job");
		Date now = new Date();
		Date healthCheckCutOff = DateUtils.addMinutes(now, -5);
		Iterable<LoaderWorker> loaderWorkers = loaderWorkerRepository.findAll();
		Map<String, LoaderWorker> loadersInUse = new HashMap<>();
		Set<LoaderWorker> duplicateHashLoaders = new HashSet<>();
		for (LoaderWorker lw : loaderWorkers) {
			if (lw.getAssignedQueue() != null && StringUtils.isNotBlank(lw.getAssignedQueue())) {
				if (loadersInUse.containsKey(lw.getAssignedQueue()) || "DUPLICATE".equals(lw.getAssignedQueue())) {
					logger.info("Duplicate key detected! Reassignment of loader worker needed!");
					lw.setAssignedQueue(null);
					duplicateHashLoaders.add(lw);
				} else {
					loadersInUse.put(lw.getAssignedQueue(), lw);
				}
			} 
		}
		Set<String> queuesInUse = new HashSet<>();

		for (LoaderWorker lw : loaderWorkers) {
			String lwName = lw.getAssignedQueue();
			
			//Assign and register new queues as needed.
			if (StringUtils.isBlank(lwName) || "DUPLICATE".equals(lwName)) {
				String assignedQueue = assignQueue(lw);
				if (StringUtils.isBlank(assignedQueue) || "DUPLICATE".equals(assignedQueue)) {
					logger.info("Unable to assign new queue for duplicate! Setting or keeping as DUPLICATE queue!");
					lw.setAssignedQueue("DUPLICATE");
					lwName = "DUPLICATE";
				} else {
					lw.setAssignedQueue(assignedQueue);
					lwName = lw.getAssignedQueue();
					logger.info("Registered new loader worker " + lw.getAssignedQueue());

				}
				loaderWorkerRepository.save(lw);
			} 
			
			if (lwName != null && !"DUPLICATE".equals(lwName)
					&& !this.loaderNames.containsKey(lwName)) {
				logger.info("Caught orphan queue - registered queue " + lwName);
				addQueue(lw);
			}
			
			if (lw.getUpdatedAt().before(healthCheckCutOff)) {
				// Condition 1, old loaderworker needs to be deregistered.
				if (this.loaderNames.containsKey(lwName)) {
					removeQueue(lwName);
				}
				loaderWorkerRepository.delete(lw);
				logger.info("Removed old loader worker " + lwName);
			} else if (this.loaderNames.containsKey(lwName)){
				// Condition 2, loader in use.
				queuesInUse.add(lwName);
				logger.info("Queue in use: " + lwName + " processing "
						+ lw.getBucketCount() + " flights with " + lw.getPermitsFree() + " permits free.");
			} else {
				// Condition 3, loader worker unable to register to a queue.
				logger.info("\n**************************** \n"
						+ "Unable to register worker to queue. \nEither increase number of allowed queues or decrease loader workers. \n"
						+ "This does not effect the system integrity but does waste system resources. \n"
						+ "Queue impacted: " + lw.getWorkerName() + ".\nIf not duplicate queue setting to DUPLICATE.\n"
						+ "**************************** \n");
				lw.setAssignedQueue("DUPLICATE");
				loaderWorkerRepository.save(lw);
			}
		}
		//Copy the key set - otherwise removing will impact the LinkedMap.
		Set<String> keys = new HashSet<>(this.loaderNames.keySet());
		keys.removeAll(queuesInUse);
		for(String orphanQueue : keys) {
			this.loaderNames.remove(orphanQueue);
			logger.info("removed orphan " + orphanQueue);
		}
		logger.info("Register/De-register job completed");
	}

	public void distributeToQueue(MessageWrapper mw) throws InterruptedException {
		EventIdentifier ei = null;
		lock.lock();
		try {
		while (this.loaderNames.isEmpty()) {
			logger.error("No Loaders!!!! Sleeping thread for 1 minute and trying again.");
			Thread.sleep(60000);
		}
		try {
			ei = eventIdentifierFactory.createEventIdentifier(mw);
		} catch (ParseException e) {
			logger.error("Unable to parse", e);
		}
		 }
		catch(Exception e) {
			logger.error("Unable to parse", e);
		} finally {
			lock.unlock();
		}
		if (ei == null) {
			writeToErrorFolder(mw);
			return;
		}
		/*
		 * 3 cases Case 1: There **is no** loader association Case 2: There **is** a
		 * loader association Case 3: There is a stale loader association
		 */
		lock.lock();
		String loaderDestination = "error";
		try {
			FlightLoader fl = flightLoaderRepository.findByidTag(ei.getIdentifier());
			if (fl == null) {
				logger.info(
						"First message for flight " + ei.getIdentifier() + " not found. Creating loader association!");
				fl = new FlightLoader();
				fl.setCreatedBy("LOADER");
				fl.setIdTag(ei.getIdentifier());
				if (indexNumber == this.loaderNames.size()) {
					indexNumber = 0;
				}
				loaderDestination = this.loaderNames.get(indexNumber);
				indexNumber++;
				fl.setLoaderName(loaderDestination);
				flightLoaderRepository.save(fl);
			} else if (this.loaderNames.containsKey(fl.getLoaderName())) {
				logger.info("Association found, forwarding message");
				loaderDestination = fl.getLoaderName();
			} else {
				logger.info("Loader assocation for flight " + ei.getIdentifier()
						+ " is stale. Creating new loader association!");
				if (indexNumber >= this.loaderNames.size()) {
					indexNumber = 0;
				}
				loaderDestination = this.loaderNames.get(indexNumber);
				indexNumber++;
				fl.setLoaderName(loaderDestination);
				fl.setUpdatedBy("LOADER");
				flightLoaderRepository.save(fl);
			}
			jmsTemplateFile.send(fl.getLoaderName(), session -> {
				Message fwd = session.createTextMessage(mw.getMessage().getPayload().toString());
				fwd.setStringProperty("filename", mw.getFileName());
				return fwd;
			});
		} catch (Exception e) {
			logger.error("Error!", e);
			logger.error("Writing to error folder!");
			String fileName = mw.getFileName() != null ? mw.getFileName() : UUID.randomUUID().toString();
			gov.gtas.services.Utils.writeToDisk(fileName, mw.getMessage().getPayload().toString(), errorstr);
		} finally {
			lock.unlock();
		}
	}

	private void writeToErrorFolder(MessageWrapper mw) {
		logger.error("Failed to extract event identifier! Writing to error folder!");
		String fileName = mw.getFileName() != null ? mw.getFileName() : UUID.randomUUID().toString();
		Utils.writeToDisk(fileName, mw.getMessage().getPayload().toString(), errorstr);
	}

	public void removeQueue(String queueName) {
		lock.lock();
		try {
			if (this.loaderNames.containsKey(queueName)) {
				this.loaderNames.remove(queueName);
				logger.info("Queue successfully removed: " + queueName);
			} else {
				logger.error("No queuename exist to be removed: " + queueName);
			}
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			lock.unlock();
		}
	}

	private void addQueue(LoaderWorker lw) {
		lock.lock();
		try {
			if (lw.getAssignedQueue() == null) {
				throw new RuntimeException("Can not register null queue!");
			}
			this.loaderNames.put(lw.getAssignedQueue(), lw);
		} catch(Exception e) {
			logger.error("", e);
		}
		finally {
			lock.unlock();
		}
	}
	
	private String assignQueue(LoaderWorker lw) {
		lock.lock();
		String openQueue = "";
		try {
			// Always give the first open queue
			boolean thereIsAnOpenQueue = false;
			for (String queueOption : loaderQueueNameOptions) {
				if (!this.loaderNames.containsKey(queueOption)) {
					thereIsAnOpenQueue = true;
					openQueue = queueOption;
					lw.setAssignedQueue(openQueue);
					break;
				}
			}
			if (thereIsAnOpenQueue) {
				this.loaderNames.put(openQueue, lw);
				logger.info("New queue assigned - " + openQueue);
			} else {
				logger.error("There are no open queues");
			}
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			lock.unlock();
		}
		return openQueue;
	}
}
