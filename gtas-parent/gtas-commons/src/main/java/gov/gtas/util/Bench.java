/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.gtas.util;

import static java.lang.System.out;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gbays This is a convenience utility for micro benchmarking code. Use
 *         unique values for the name param; it does not matter what you put for
 *         name, it just allows the retrieval of the TimeObject for the
 *         benchmark. If the name is not unique for a run, then a negative time
 *         (-99999) will be returned. The Logger level is set to Warn here to
 *         make the messages more visible in the output.
 */
public class Bench {
	private final static Map<String, TimeObject> timeObjectMap = new HashMap<>();
	private static int count = 0;
	private static final Logger logger = LoggerFactory.getLogger(Bench.class);
	private static boolean logResultsAsTheyHappen = false;

	public static void start(String name, String message) {
		TimeObject timeObj = new TimeObject(name, (System.nanoTime() / 1000000), message, false, count);
		if (logResultsAsTheyHappen) {
			timeObj.logStart();
		}
		timeObjectMap.put(name, timeObj);
		count++;
	}

	public static void end(String name, String message) {
		TimeObject endTimeObj = new TimeObject(name + message, (System.nanoTime() / 1000000), message, true, count);
		TimeObject startTimeObj = timeObjectMap.get(name);
		if (startTimeObj != null) {
			endTimeObj.setElapsedTime(endTimeObj.getCurrentTime() - startTimeObj.getCurrentTime());
		} else {
			endTimeObj.setElapsedTime(-999999L);
		}

		timeObjectMap.put(name + "end", endTimeObj);

		count++;
		if (logResultsAsTheyHappen) {
			endTimeObj.logEnd();
		}
		// timeObjectMap.remove(name);
	}

	public static void print() {
		List<TimeObject> reducedOrderedList = timeObjectMap.values().stream().filter(t -> t.isIsEndObject())
				.sorted((t1, t2) -> t2.getElapsedTime().compareTo(t1.getElapsedTime())).collect(Collectors.toList());

		// logger.warn("\n\n******BENCHMARKS******\n");
		System.out.println("\n\n******BENCHMARKS******\n");
		for (TimeObject tObj : reducedOrderedList) {
			tObj.logEndForPrint();
		}

		// logger.warn("\n\n******END BENCHMARKS******\n\n");
		System.out.println("\n\n******END BENCHMARKS******\n\n");
		timeObjectMap.clear();
	}

}

class TimeObject {
	private final Logger logger = LoggerFactory.getLogger(Bench.class);
	private long currentTime;
	private String name;
	private String message;
	private Long elapsedTime;
	private boolean isEndObject;
	private Integer count;
	private static final int ELAPSED_TIME_PADDING_LENGTH = 16;

	public TimeObject(String nameP, long startTimeP, String currentTimeP, boolean isEndObjectP, int countP) {
		this.name = nameP;
		this.currentTime = startTimeP;
		this.message = currentTimeP;
		this.isEndObject = isEndObjectP;
		this.count = countP;
	}

	public String getName() {
		return this.name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}

	public Long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(Long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public boolean isIsEndObject() {
		return isEndObject;
	}

	public void setIsEndObject(boolean isEndObject) {
		this.isEndObject = isEndObject;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public void logStart() {
		logger.warn("SSSSSSSSSSSSStart : " + this.getMessage());
	}

	public void logEnd() {
		logger.warn("EEEEEEEEEEEEEEnd : " + this.getMessage() + " " + this.getElapsedTime() + " ms");
	}

	public void logEndForPrint() {
		String timeStr = this.getElapsedTime() + " ms";
		int padLength = ELAPSED_TIME_PADDING_LENGTH - timeStr.length();
		String timeDisplayStr = StringUtils.rightPad(timeStr, padLength) + "\t";
		System.out.println(timeDisplayStr + this.getMessage());
	}

}
