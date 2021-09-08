package gov.gtas.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import gov.gtas.model.LoaderWorker;

public interface LoaderWorkerRepository extends CrudRepository<LoaderWorker, Long> {
	
	@Query("Select lw from LoaderWorker lw where lw.workerName = :name")
	public LoaderWorker findByWorkerName(@Param("name")String name);
	
}
