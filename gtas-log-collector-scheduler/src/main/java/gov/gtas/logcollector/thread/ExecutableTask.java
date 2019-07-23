package gov.gtas.logcollector.thread;

import static gov.gtas.logcollector.Constants.LINUX_CMD_CONST;
import static gov.gtas.logcollector.Constants.WINDOWS;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import gov.gtas.logcollector.AppConfig;
import gov.gtas.logcollector.Constants;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ExecutableTask {
	
	@Autowired
	private AppConfig appConfig;
	
	private static final Logger log = LoggerFactory.getLogger(ExecutableTask.class);
	private static boolean isProcessing = false;
	
	
	public synchronized void executeOnCommandLine(String command) {
		int exitValue = -1;
		try {
			log.info(" *** LAUNCHING SCHEDULER FOR GTAS LOG-COLLECTOR-ETL **** ");

			Runtime runtime = Runtime.getRuntime();
			Process process = null;
			if (appConfig.getOpSystem() != null && appConfig.getOpSystem().trim().equalsIgnoreCase(Constants.LINUX)) {
				process = runtime.exec(new String[] { LINUX_CMD_CONST, "-c", command });
			} else if (appConfig.getOpSystem() != null && appConfig.getOpSystem().trim().equalsIgnoreCase(WINDOWS)) {
				process = runtime.exec(command);
			}

			InputStream stderr = process.getErrorStream();
			InputStreamReader inputStreamReader = new InputStreamReader(stderr);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				log.info(line);
				isProcessing = true;
			}
			exitValue = process.waitFor();
			isProcessing = false;
			log.info("***  END OF GTAS-LOG-COLLECTOR SCHEDULER .....EXIT VALUE = " + exitValue);
			process.destroy();

		} catch (Exception e) {
			log.error("An error has occurred when launching the PDI ETL job from the gtas-neo4j-job-scheduler");
			e.printStackTrace();
		}

	}


	public static boolean isProcessing() {
		return isProcessing;
	}
	

	
	
	
	


}
