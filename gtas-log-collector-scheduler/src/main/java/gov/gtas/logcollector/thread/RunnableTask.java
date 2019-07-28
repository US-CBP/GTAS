/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.logcollector.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RunnableTask implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(RunnableTask.class);


	
	@Autowired
	private ExecutableTask executableTask;
	
	@Autowired
	private ExecutableCommand executableCommand;



	@Override
	public void run() {

		if (!ExecutableTask.isProcessing()) {
			log.info(" COMMAND LINE: " + executableCommand.getCommand());
			executableTask.executeOnCommandLine(executableCommand.getCommand());
		}
	}



}
