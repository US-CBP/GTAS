/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import gov.gtas.parsers.edifact.EdifactLexer;
import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.edifact.segment.UNA;
import gov.gtas.parsers.exception.ParseException;

@Component
public class LoaderQueueThreadManager {
	@Autowired
	private ApplicationContext ctx;

	private int maxNumOfThreads = 5;

	private ExecutorService exec = Executors.newFixedThreadPool(maxNumOfThreads);

	private static ConcurrentMap<String, BlockingQueue<Message<?>>> bucketBucket = new ConcurrentHashMap<String, BlockingQueue<Message<?>>>();

	static final Logger logger = LoggerFactory.getLogger(LoaderQueueThreadManager.class);

	public void receiveMessages(Message<?> message) {
		String[] primeFlightKeyArray = getPrimeFlightKey(message);
		//Construct label for individual buckets out of concatenated array values from prime flight key generation
		String primeFlightKey = primeFlightKeyArray[0]+primeFlightKeyArray[1]+primeFlightKeyArray[2]+primeFlightKeyArray[3];
		// bucketBucket is a bucket of buckets. It holds a series of queues that are processed sequentially.
		// This solves the problem where-in which we cannot run the risk of trying to save/update the same flight at the same time. This is done
		// by shuffling all identical flights into the same queue in order to be processed sequentially. However, by processing multiple
		// sequential queues at the same time, we in essence multi-thread the process for all non-identical prime flights
		BlockingQueue<Message<?>> potentialBucket = bucketBucket.get(primeFlightKey);
		if (potentialBucket == null) {
			// Is not existing bucket, make bucket, stuff in bucketBucket,
			logger.info("New Queue Created For Prime Flight: " + primeFlightKey);
			BlockingQueue<Message<?>> queue = new ArrayBlockingQueue<Message<?>>(1024);
			queue.offer(message); // TODO: offer returns false if can't enter the queue, need to make sure we don'tlose messages and have it wait for re-attempt when there is space.
			bucketBucket.putIfAbsent(primeFlightKey, queue);
			// Only generate workers on a per queue basis
			LoaderWorkerThread worker = ctx.getBean(LoaderWorkerThread.class);
			worker.setQueue(queue);
			worker.setMap(bucketBucket); // give map reference and key in order to kill queue later
			worker.setPrimeFlightKeyArray(primeFlightKeyArray);
			worker.setPrimeFlightKey(primeFlightKey);
			exec.execute(worker);
		} else {
			// Is existing bucket, place same prime flight message into bucket
			logger.info("Existing Queue Found! Placing message inside...");
			potentialBucket.offer(message);
			// No need to execute worker here, if queue exists then worker is already on it.
		}
	}
	
	//Crafts prime flight key out of TVL0 line in the message
	private String[] getPrimeFlightKey(Message<?> message) {
		List<Segment> segments = new ArrayList<>();
		String[] primeFlightKeyArray = new String[4];
		String tvlLineText = "";
		EdifactLexer lexer = new EdifactLexer((String) message.getPayload());
		try {
			segments = lexer.tokenize();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		for (Segment seg : segments) {
			if (seg.getName().equalsIgnoreCase("TVL")) {
				tvlLineText = seg.getText();
				primeFlightKeyArray[0] = seg.getComposite(1).getElement(0);
				primeFlightKeyArray[1] = seg.getComposite(2).getElement(0);
				primeFlightKeyArray[2] = seg.getComposite(3).getElement(0)+seg.getComposite(4).getElement(0);
				break;
			}
		}
		String delimiterChar = "";
		if (tvlLineText == ""){
			//Is APIS, need to convert to primeflightkey programmatically from various APIS segments
			tvlLineText += "TVL"+delimiterChar; //baseline
			int locCount = 0;
			String[] orderArray = new String[5];
			String regex = "((?<=[a-zA-Z])(?=[0-9]))|((?<=[0-9])(?=[a-zA-Z]))";
			for (Segment seg : segments){
				if(seg.getName().equals("DTM")){
					orderArray[0] = seg.getText().split(delimiterChar+"")[1].replace("'", "");
				}else if(seg.getName().equals("LOC")){
					orderArray[locCount+1] = seg.getText().split(delimiterChar+"")[2].replace("'","");
					primeFlightKeyArray[locCount] = seg.getComposite(1).getElement(0);
					locCount++;
				}else if(seg.getName().equals("TDT")){
					//String[] tmpArry = seg.getComposite(1).getElement(0).split(regex);
						primeFlightKeyArray[2] = seg.getComposite(1).getElement(0);
				}
				if(locCount == 2){
					break;
				}
			}//End for
			
			//Assemble primeflight tvl
			for(int i=0;i<orderArray.length;i++){
				if(i == orderArray.length - 1){
					tvlLineText +=orderArray[i]+"'";
				}else{
					tvlLineText +=orderArray[i]+delimiterChar;
				}
			}
		}		
		return primeFlightKeyArray;
	}
}
