package gov.gtas.repository;

import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.FlightPax;
import gov.gtas.model.lookup.Carrier;


public interface FlightPaxRepository extends CrudRepository<FlightPax, Long>{
	
    default FlightPax findOne(Long flightPaxId)
    {
    	return findById(flightPaxId).orElse(null);
    }

}
