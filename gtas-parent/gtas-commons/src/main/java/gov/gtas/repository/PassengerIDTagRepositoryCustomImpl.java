package gov.gtas.repository;

import gov.gtas.model.Passenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class PassengerIDTagRepositoryCustomImpl implements PassengerIDTagRepositoryCustom {

	private static final Logger logger = LoggerFactory.getLogger(PassengerIDTagRepositoryCustomImpl.class);

	@PersistenceContext
	private EntityManager em;

	// @SuppressWarnings("unchecked")
	// @Override
	// public List<Passenger> getPassengersByFlightIdAndName(Long flightId, String
	// firstName, String lastName) {
	// String nativeQuery = "";
	// return (List<Passenger>) em.createNativeQuery(nativeQuery,
	// Passenger.class).getResultList();
	// }
	//
	// @SuppressWarnings("unchecked")
	// @Override
	// public List<Passenger> getPassengersByFlightId(Long flightId) {
	// String nativeQuery = "";
	// return (List<Passenger>) em.createNativeQuery(nativeQuery,
	// Passenger.class).getResultList();
	// }

}
