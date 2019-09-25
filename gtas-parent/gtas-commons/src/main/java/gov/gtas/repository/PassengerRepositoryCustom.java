/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.Passenger;
import gov.gtas.services.dto.PassengersRequestDto;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public interface PassengerRepositoryCustom {
	/**
	 * 
	 * @param flightId
	 *            optional. When specified, only return the passengers for the given
	 *            flight. When null, return passengers on all flights.
	 * @param request
	 *            query criteria.
	 */
	public Pair<Long, List<Object[]>> findByCriteria(Long flightId, PassengersRequestDto request);

	public List<Object[]> findAllDispositions();

	/**
	 * Find Passengers by attributes.
	 *
	 * @param passengerId
	 *            retrieve the passenger and then extract its attributes.
	 * @param docNum
	 *            the document number
	 * @param docIssuCountry
	 *            the doc issu country
	 * @param docExpDate
	 *            the doc exp date
	 * @return the list
	 */
	public List<Passenger> findByAttributes(Long passengerId, String docNum, String docIssuCountry, Date docExpDate);

	/**
	 * Find existing passenger by attributes.
	 *
	 * @param firstName
	 *            the first name
	 * @param lastName
	 *            the last name
	 * @param middleName
	 *            the middle name
	 * @param gender
	 *            the gender
	 * @param dob
	 *            the dob
	 * @param passengerType
	 *            the passenger type
	 * @return true, if successful
	 */
	public boolean findExistingPassengerByAttributes(String firstName, String lastName, String middleName,
			String gender, Date dob, String passengerType);

	public Passenger findExistingPassengerWithAttributes(String firstName, String lastName, String middleName,
			String gender, Date dob, String passengerType);

	public List<Passenger> getPassengersByFlightIdAndName(Long flightId, String firstName, String lastName);

	public List<Passenger> getPassengersByFlightId(Long flightId);

}
