/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import com.sun.jna.platform.win32.WinUser.MSG;

@Component
public class LoaderWorkerThread implements Runnable {
	@Autowired
	private LoaderScheduler loader;
/*	@PersistenceUnit
	EntityManagerFactory emf;*/
	
	private String text;
	private String fileName;
	private BlockingQueue<Message<?>> queue;
	
	static final Logger logger = LoggerFactory.getLogger(LoaderWorkerThread.class);
	
	public LoaderWorkerThread(){
	}
	
	public LoaderWorkerThread(BlockingQueue<Message<?>> queue){
        this.queue=queue;
    }

	@Override
    public void run() {
    	while(true){
	    	Message<?> msg = null;
	    	try {
	    		msg = queue.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	    	
	    	if(msg != null){
	    		MessageHeaders headers =  msg.getHeaders();
	    		this.fileName = headers.get("Filename").toString();
	    		this.text = msg.getPayload().toString();
	    		logger.info(Thread.currentThread().getName()+" Test FileName = "+fileName);
	        	processCommand();
	    	}
	    }
    }
   
    private void processCommand() {
      loader.receiveMessage(text, fileName);
    }

    @Override
    public String toString(){
        return Thread.currentThread().getName()+" Filename: "+fileName;
    }

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public BlockingQueue<Message<?>> getQueue() {
		return queue;
	}

	public void setQueue(BlockingQueue<Message<?>> queue) {
		this.queue = queue;
	}
}
