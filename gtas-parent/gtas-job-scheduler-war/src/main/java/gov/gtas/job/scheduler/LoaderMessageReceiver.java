package gov.gtas.job.scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

@Component
public class LoaderMessageReceiver {
	//Uncomment along with executorService when threading is more capable in our persistence model
	@Autowired
	private LoaderWorkerThread worker;
	
	@Autowired
	private LoaderScheduler loader;
	
	private int maxNumOfThreads = 2;
	
	private ExecutorService exec = Executors.newFixedThreadPool(maxNumOfThreads);
	
	private static final String GTAS_LOADER_QUEUE = "GTAS_LOADER_Q";
	static final Logger logger = LoggerFactory.getLogger(LoaderMessageReceiver.class);
	
	@JmsListener(destination = GTAS_LOADER_QUEUE, concurrency = "2")
	public void receiveMessagesForLoader(Message<?> message){
		logger.info("+++++++++++++++++IN LOADER QUEUE++++++++++++++++++++++++++++++++++++");
		MessageHeaders headers =  message.getHeaders();
		logger.info("Application : headers received : {}", headers);
		logger.info("Filename: "+headers.get("Filename"));
		//logger.info("+++++++++++++FILE CONTENT ++++++++++++++++++++++"+message.getPayload());
		String fileName = headers.get("Filename").toString();
		//loader.receiveMessage(message.getPayload().toString(), fileName);;
		//Uncomment when threading is supported for our loader process
		worker.setFileName(fileName);
		worker.setText(message.getPayload().toString());
		exec.execute(worker);
	}
}