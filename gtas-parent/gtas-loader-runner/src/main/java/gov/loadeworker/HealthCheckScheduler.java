package gov.loadeworker;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import gov.gtas.model.LoaderWorker;
import gov.gtas.repository.LoaderWorkerRepository;
import gov.gtas.services.LoaderQueueThreadManager;

@Component
public class HealthCheckScheduler {
	
	private static final Logger logger = LoggerFactory.getLogger(HealthCheckScheduler.class);
	
	private final LoaderWorkerRepository loaderWorkerRepository;
	
	private final String name;
	
	public HealthCheckScheduler(LoaderWorkerRepository loaderWorkerRepository, 
			@Value("${loader.name}")String name) {
		this.loaderWorkerRepository = loaderWorkerRepository;
		this.name = name;
	}


	@Scheduled(fixedDelayString = "${loader.delay}", initialDelayString = "15000")
	public void jobScheduling() {
		int bucketCount = LoaderQueueThreadManager.getBucketBucket().size();
		LoaderWorker lw = loaderWorkerRepository.findByWorkerName(name);
		if (lw == null) {
			lw = new LoaderWorker();
			lw.setBucketCount(bucketCount);
			lw.setCreatedAt(new Date());
			lw.setCreatedBy("LoaderWorker");
			lw.setUpdatedBy("LoaderWorker");
			lw.setUpdatedAt(new Date());
			lw.setWorkerName(name);
		} else {
			lw.setUpdatedAt(new Date());
			lw.setBucketCount(bucketCount);
			lw.setUpdatedBy("LoaderWorker");
		}
		
		loaderWorkerRepository.save(lw);
		logger.info("Health check completed for " + name + " with bucket count of : " + bucketCount);
	}
}
