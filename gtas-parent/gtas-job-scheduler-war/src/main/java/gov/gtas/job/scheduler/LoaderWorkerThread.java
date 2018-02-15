package gov.gtas.job.scheduler;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoaderWorkerThread implements Runnable {
	@Autowired
	private LoaderScheduler loader;
	@PersistenceUnit
	EntityManagerFactory emf;
	
	private String text;
	private String fileName;
	static final Logger logger = LoggerFactory.getLogger(LoaderWorkerThread.class);
	
	public LoaderWorkerThread(){
	}
	
	public LoaderWorkerThread(String text, String fileName){
        this.text=text;
        this.fileName=fileName;
    }

    @Override
    public void run() {
    	EntityManager em = emf.createEntityManager();
        logger.info(Thread.currentThread().getName()+" Test FileName = "+fileName);
        em.getTransaction().begin();
        processCommand();
        em.getTransaction().commit();
        em.flush();
        em.close();
        logger.info(Thread.currentThread().getName()+" End.");
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
}
