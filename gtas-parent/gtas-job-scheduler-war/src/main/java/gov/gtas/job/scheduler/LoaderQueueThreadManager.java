/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import gov.gtas.job.config.JobSchedulerConfig;
import gov.gtas.parsers.paxlst.segment.unedifact.DTM;
import gov.gtas.parsers.paxlst.segment.unedifact.LOC;
import gov.gtas.parsers.paxlst.segment.unedifact.TDT;
import gov.gtas.parsers.pnrgov.segment.TVL_L0;
import gov.gtas.parsers.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import gov.gtas.parsers.edifact.EdifactLexer;
import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.exception.ParseException;

import static gov.gtas.services.GtasLoaderImpl.*;

@Component
public class LoaderQueueThreadManager {

	private final ApplicationContext ctx;

	private static final int DEFAULT_THREADS_ON_LOADER = 5;

	private ExecutorService exec;

	private static final int DEFAULT_PERMITS = 5000;

	private static ConcurrentMap<String, LoaderWorkerThread> bucketBucket = new ConcurrentHashMap<>();

	private static final Logger logger = LoggerFactory.getLogger(LoaderQueueThreadManager.class);

	private final Semaphore semaphore;

	@Autowired
	public LoaderQueueThreadManager(ApplicationContext ctx,
									@Value("${loader.permits}") Integer loaderPermits, JobSchedulerConfig jobSchedulerConfig) {

		this.ctx = ctx;
		if (loaderPermits == null || loaderPermits.equals(0)) {
			logger.warn("no permits set up, using default of " + DEFAULT_PERMITS);
			this.semaphore = new Semaphore(DEFAULT_PERMITS);
		} else {
			this.semaphore = new Semaphore(loaderPermits);
		}
		/*
		 * Fail safe and fall back to 5 number of threads when the database
		 * configuration is set incorrectly
		 */
		int maxNumOfThreads = DEFAULT_THREADS_ON_LOADER;

		try {

			maxNumOfThreads = jobSchedulerConfig.getThreadsOnLoader();

		} catch (Exception e) {
			logger.error(String.format(
					"Failed to load application configuration: THREADS_ON_LOADER from application.properties... Number of threads set to use %1$s",
					DEFAULT_THREADS_ON_LOADER));
		}

		this.exec = Executors.newFixedThreadPool(maxNumOfThreads);
	}

	void receiveMessages(Message<?> message) throws ParseException, InterruptedException {
		String[] primeFlightKeyArray = generatePrimeFlightKey(message);

		// Construct label for individual buckets out of concatenated array values from
		// prime flight key generation
		String primeFlightKey = primeFlightKeyArray[PRIME_FLIGHT_ORIGIN] + primeFlightKeyArray[PRIME_FLIGHT_DESTINATION]
				+ primeFlightKeyArray[PRIME_FLIGHT_CARRIER] + primeFlightKeyArray[PRIME_FLIGHT_NUMBER_STRING]
				+ primeFlightKeyArray[ETD_DATE_NO_TIMESTAMP_AS_LONG];
		// bucketBucket is a bucket of buckets. It holds a series of queues that are
		// processed sequentially.
		// This solves the problem where-in which we cannot run the risk of trying to
		// save/update the same flight at the same time. This is done
		// by shuffling all identical flights into the same queue in order to be
		// processed sequentially. However, by processing multiple
		// sequential queues at the same time, we in essence multi-thread the process
		// for all non-identical prime flights
		AtomicBoolean firstMessage = new AtomicBoolean(false);
		logger.debug("Available permits: " + semaphore.availablePermits());

		// Here we will acquire a lock as a new message has come in. The Loader Worker
		// will release the lock when it is done processing the message.
		semaphore.acquire();
		LoaderWorkerThread primeFlightWorkerThread = bucketBucket.computeIfAbsent(primeFlightKey, m -> {
			LoaderWorkerThread worker = ctx.getBean(LoaderWorkerThread.class);
			logger.info("New Queue Created For Prime Flight: " + primeFlightKey);
			worker.setQueue(new ArrayBlockingQueue<>(1024));
			worker.setMap(bucketBucket); // give map reference and key in order to kill queue later
			worker.setPrimeFlightKeyArray(primeFlightKeyArray);
			worker.setPrimeFlightKey(primeFlightKey);
			worker.setSemaphore(semaphore);
			firstMessage.set(true);
			return worker;
		});
		// There solves the race condition in which the queue is being torn
		// down/destroyed and then the message is
		// added to the queue.
		// addMessageToQueue returns false when the thread has is being destroyed.
		// If the worker has been destroyed then re-run receiveMessage, which will
		// create a new worker thread
		// and process correctly.
		boolean addedToQueue = primeFlightWorkerThread.addMessageToQueue(message);
		if (!addedToQueue) {
			logger.error("MESSAGE NOT PROCESSED-REPROCESSING");

			receiveMessages(message);
			return;
		}
		if (firstMessage.get()) {
			exec.execute(primeFlightWorkerThread);
		}
	}

	/*
	 * Crafts prime flight key out of TVL0 line of a PNR message or DTM LOC and TDT
	 * of an APIS message. Key is the following primeFlightKeyArray[0] = PRIME
	 * FLIGHT ORIGIN primeFlightKeyArray[1] = PRIME FLIGHT DESTINATION
	 * primeFlightKeyArray[2] = PRIME FLIGHT CARRIER primeFlightKeyArray[3] = PRIME
	 * FLIGHT NUMBER primeFlightKeyArray[4] = PRIME FLIGHT ETD DATE AS A STRING LONG
	 * VALUE primeFlightKeyArray[5] = PRIME FLIGHT ETD TIMESTAMP AS A STRING LONG
	 * VALUE
	 */
	private String[] generatePrimeFlightKey(Message<?> message) throws ParseException {
		String[] primeFlightKeyArray = new String[6];
		List<Segment> messageSegments = getMessageSegments(message);
		boolean apisMessage = true;
		// Arbitrarily attempt to read prime flight from PNR first.
		for (Segment segment : messageSegments) {
			// Extract the prime flight information from a PNR message.
			// This will mirror prime flight array result of an APIS message.
			// PNR and APIS messages relating to the same prime flight
			// will always generate the same label.
			if (segment.getName().equalsIgnoreCase("TVL")) {
				apisMessage = false;
				TVL_L0 tvl = new TVL_L0(segment.getComposites());
				primeFlightKeyArray[PRIME_FLIGHT_ORIGIN] = tvl.getOrigin().trim();
				primeFlightKeyArray[PRIME_FLIGHT_DESTINATION] = tvl.getDestination().trim();
				primeFlightKeyArray[PRIME_FLIGHT_CARRIER] = tvl.getCarrier().trim();
				String primeFlightNumber = tvl.getFlightNumber().trim();
				primeFlightNumber = addZerosToPrimeFlightIfNeeded(primeFlightNumber);
				primeFlightKeyArray[PRIME_FLIGHT_NUMBER_STRING] = primeFlightNumber;
				primeFlightKeyArray[ETD_DATE_NO_TIMESTAMP_AS_LONG] = Long.toString(flightDate(tvl));
				primeFlightKeyArray[ETD_DATE_WITH_TIMESTAMP] = Long.toString(tvl.getEtd().getTime());
				break;
			}
		}

		// If the attempt to parse a PNR doesn't result in a prime flight key attempt to
		// read segments as an APIS message.
		if (apisMessage) {
			boolean primeFlightArrivalFound = false;
			boolean primeFlightDepartFound = false;
			boolean primeFlightDepartDateFound = false;
			for (Segment seg : messageSegments) {
				// Extract the prime flight information from an APIS message.
				// This will mirror prime flight array result of a PNR message.
				// PNR and APIS messages relating to the same prime flight
				// will always generate the same label.
				switch (seg.getName()) {
				case "TDT":
					// TDT is the parent of LOC and DTM. We are basing processing the loop off the
					// messages in the
					// messages below. This means the information relating the TDT can be
					// overwritten several times
					// before finding a prime flight.
					TDT tdt = new TDT(seg.getComposites());
					primeFlightKeyArray[PRIME_FLIGHT_CARRIER] = tdt.getC_carrierIdentifier();
					String primeFlightNumber = tdt.getFlightNumber().trim();
					primeFlightNumber = addZerosToPrimeFlightIfNeeded(primeFlightNumber);
					primeFlightKeyArray[PRIME_FLIGHT_NUMBER_STRING] = primeFlightNumber;
					break;
				case "LOC":
					LOC loc = new LOC(seg.getComposites());
					// The arrival airport corresponds to the prime flight's arrival airport.
					if (loc.getFunctionCode() == LOC.LocCode.ARRIVAL_AIRPORT) {
						primeFlightKeyArray[PRIME_FLIGHT_DESTINATION] = loc.getLocationNameCode();
						primeFlightArrivalFound = true;
						// The departure airport corresponds with the prime flight's departure airport.
					} else if (loc.getFunctionCode() == LOC.LocCode.DEPARTURE_AIRPORT) {
						primeFlightKeyArray[PRIME_FLIGHT_ORIGIN] = loc.getLocationNameCode();
						primeFlightDepartFound = true;
					}
					break;
				case "DTM":
					DTM dtm = new DTM(seg.getComposites());
					// Take advantage that the next DTM after the prime flight departure airport
					// will hold the prime flight ETD.
					if (dtm.getDtmCode() == DTM.DtmCode.DEPARTURE && primeFlightDepartFound) {
						primeFlightKeyArray[ETD_DATE_NO_TIMESTAMP_AS_LONG] = Long
								.toString(DateUtils.stripTime(dtm.getDtmValue()).getTime());
						primeFlightKeyArray[ETD_DATE_WITH_TIMESTAMP] = Long.toString(dtm.getDtmValue().getTime());
						primeFlightDepartDateFound = true;
					}
					break;
				default:
					break;
				}
				if (primeFlightArrivalFound && primeFlightDepartFound && primeFlightDepartDateFound) {
					// Prime flight generated - stop processing message!
					break;
				}
			}
		}
		return primeFlightKeyArray;
	}

	private long flightDate(TVL_L0 tvl) {
		return DateUtils.stripTime(tvl.getEtd()).getTime();
	}

	private List<Segment> getMessageSegments(Message<?> message) {
		List<Segment> segments = new ArrayList<>();
		EdifactLexer lexer = new EdifactLexer((String) message.getPayload());
		try {
			segments = lexer.tokenize();
		} catch (ParseException e) {
			logger.error("error tokenizing segments", e);
		}
		return segments;
	}

	private String addZerosToPrimeFlightIfNeeded(String primeFlightNumber) {
		StringBuilder primeFlightNumberBuilder = new StringBuilder(primeFlightNumber);
		while (primeFlightNumberBuilder.length() < 4) {
			primeFlightNumberBuilder.insert(0, "0");
		}
		return primeFlightNumberBuilder.toString();
	}
}
