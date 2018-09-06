package gov.gtas.services;

import java.util.List;

public interface PassengerResolverService {
	
	List<Long> resolve(Long pax_id);
}
