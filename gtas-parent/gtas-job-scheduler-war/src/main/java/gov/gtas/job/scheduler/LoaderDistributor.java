package gov.gtas.job.scheduler;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.commons.collections4.map.LinkedMap;
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

	private LinkedMap<String, String> loaderNames;

	private int indexNumber = 0;

	private ReentrantLock lock = new ReentrantLock();
	
	private final LoaderWorkerRepository loaderWorkerRepository;

	private final ManualMover manualMover;

	public LoaderDistributor(JmsTemplate jmsTemplateFile, FlightLoaderRepository flightLoaderRepository,
			EventIdentifierFactory eventIdentifierFactory, @Value("${message.dir.error}") String errorstr,
			LoaderWorkerRepository loaderWorkerRepository, 	@Value("${activemq.broker.url}")String brokerUrl, 	
			@Value("${inbound.loader.jms.queue}")String distributorQueue) {
		this.jmsTemplateFile = jmsTemplateFile;
		this.flightLoaderRepository = flightLoaderRepository;
		this.eventIdentifierFactory = eventIdentifierFactory;
		this.errorstr = errorstr;
		this.loaderNames = new LinkedMap<>();
		this.loaderWorkerRepository = loaderWorkerRepository;
		//We do not want spring to manage this as it creates and destroys connections to the queue.
		this.manualMover = new ManualMover(brokerUrl, distributorQueue);
	}

	@Scheduled(fixedDelayString = "${loader.purge.check}", initialDelayString = "30000")
	public void checkAllQueues() {
		logger.info("Starting queue check job");
		// Check all queues to retrieve stale messages
		List<LoaderWorker> workers = loaderWorkerRepository.loadActiveWorkers();
		for (LoaderWorker lw : workers) {
			try {
				if (!loaderNames.containsKey(lw.getWorkerName())) {
					logger.info("Queue " + lw.getWorkerName() + " not active - Moving any old messages from " + lw.getWorkerName() + " to GTAS distributor.");
					manualMover.purgeQueue(lw.getWorkerName());
				} else {
					logger.info("Active loader: " + lw.getWorkerName() + ". No action taken.");
				}
			} catch (JMSException jme) {
				logger.error("Failed queue purge for queue: " + lw.getWorkerName(), jme);
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
		for (LoaderWorker lw : loaderWorkers) {
			String lwName = lw.getWorkerName();
			if (!this.loaderNames.containsKey(lwName)) {
				// Condition 1, new loaderworker:
				this.loaderNames.put(lw.getWorkerName(), lwName);
				logger.info("Registered new loader worker " + lwName);
			} else if (lw.getUpdatedAt().before(healthCheckCutOff)) {
				// Condition 2, old loaderworker
				if (this.loaderNames.containsKey(lwName)) {
					removeQueue(lwName);
				}
				loaderWorkerRepository.delete(lw);
				logger.info("Removed old loader worker " + lwName);
			} else {
				// Condition 3, loader in use.
				logger.info("Queue in use: " + lwName + " processing "
						+ lw.getBucketCount() + " files.");
			}
		}
		logger.info("Register/De-register job completed");
	}

	public void distributeToQueue(MessageWrapper mw) throws InterruptedException {
		EventIdentifier ei = null;
		lock.lock();
		try {
		while (loaderNames.isEmpty()) {
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
				if (indexNumber == loaderNames.size()) {
					indexNumber = 0;
				}
				loaderDestination = loaderNames.get(indexNumber);
				indexNumber++;
				fl.setLoaderName(loaderDestination);
				flightLoaderRepository.save(fl);
			} else if (loaderNames.containsKey(fl.getLoaderName())) {
				logger.info("Association found, forwarding message");
				loaderDestination = fl.getLoaderName();
			} else {
				logger.info("Loader assocation for flight " + ei.getIdentifier()
						+ " is stale. Creating new loader association!");
				if (indexNumber >= loaderNames.size()) {
					indexNumber = 0;
				}
				loaderDestination = loaderNames.get(indexNumber);
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

	public String assignQueue() {
		lock.lock();
		String openQueue = "";
		try {
			// Always give the first open queue
			boolean thereIsAnOpenQueue = false;
			for (LoaderWorker worker : loaderWorkerRepository.loadActiveWorkers()) {
				String queueName = worker.getWorkerName();
				if (!loaderNames.containsKey(queueName)) {
					thereIsAnOpenQueue = true;
					openQueue = queueName;
					break;
				}
			}
			if (thereIsAnOpenQueue) {
				loaderNames.put(openQueue, openQueue);
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
