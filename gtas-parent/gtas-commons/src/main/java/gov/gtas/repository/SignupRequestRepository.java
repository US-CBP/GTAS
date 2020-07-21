package gov.gtas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import gov.gtas.enumtype.SignupRequestStatus;
import gov.gtas.model.SignupRequest;

public interface SignupRequestRepository extends CrudRepository<SignupRequest, Long> {

	/*
	 * This is using Spring's derived query method feature to formulate the query.
	 * 
	 * it must follow certain rules, see
	 * https://docs.spring.io/spring-data/jpa/docs/2.1.3.RELEASE/reference/html/#jpa
	 * .modifying-queries.derived-delete
	 * 
	 */
	public Boolean existsSignupRequestByEmailOrUsername(String email, String username);

	/**
	 * Returns all new sign up requests
	 * 
	 * @param status
	 * @return
	 */
	@Query("select u from SignupRequest u where u.status = :status")
	public List<SignupRequest> findNewSignupRequests(@Param("status") SignupRequestStatus status);
}
