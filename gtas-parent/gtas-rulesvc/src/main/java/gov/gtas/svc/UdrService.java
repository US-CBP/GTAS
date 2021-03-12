/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGES_ADMIN_AND_MANAGE_RULES;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.udr.KnowledgeBase;
import gov.gtas.model.udr.json.JsonUdrListElement;
import gov.gtas.model.udr.json.UdrSpecification;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * The service interface for managing User Defined Rules (UDR).<br>
 * 1. CRUD on UDR.<br>
 * 2. Generation of Drools Rules and creation of versioned Knowledge Base.
 */
public interface UdrService {
	/**
	 * Retrieves the UDR domain object from the DB and converts it to the
	 * corresponding JSON object.
	 * 
	 * @param userId
	 *            the userId of the author.
	 * @param title
	 *            the title of the UDR.
	 * @return the JSON UDR object.
	 */
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_RULES)
	UdrSpecification fetchUdr(String userId, String title);

	/**
	 * Retrieves the UDR domain object from the DB and converts it to the
	 * corresponding JSON object.
	 * 
	 * @param id
	 *            the id of the UDR record in the DB.
	 * @return the JSON UDR object.
	 */
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_RULES)
	UdrSpecification fetchUdr(Long id);

	/**
	 * Retrieves a list of UDR summary JSON objects authored by the specified user.
	 * 
	 * @param userId
	 *            the userId of the author.
	 * @return the list of JSON UDR summary objects.
	 */
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_RULES)
	List<JsonUdrListElement> fetchUdrSummaryList(String userId);

	/**
	 * Retrieves a list of UDR summary JSON objects.
	 * 
	 * @return the list of JSON UDR summary objects.
	 */
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_RULES)
	List<JsonUdrListElement> fetchUdrSummaryList();

	/**
	 * Creates a new UDR object in the database and returns it in JSON object
	 * format.
	 * 
	 * @param userId
	 *            the userId of the author.
	 * @param udrToCreate
	 *            the JSON UDR object to be inserted into tte DB.
	 * @return the service response JSON format.
	 */
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_RULES)
	JsonServiceResponse createUdr(String userId, UdrSpecification udrToCreate);

	/**
	 * Creates a new UDR object by copying a UDR specified by ID.
	 * 
	 * @param userId
	 *            the userId of the author.
	 * @param udrId
	 *            the id of the UDR object to be copied.
	 * @return the service response JSON format.
	 */
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_RULES)
	JsonServiceResponse copyUdr(String userId, Long udrId);

	/**
	 * Updates the UDR by replacing the UDR object in the DB with the same ID.
	 * 
	 * @param userId
	 *            the userId of the author.
	 * @param udrToUpdate
	 *            the updated object image to use for replacing the DB object.
	 * @return the updated object.
	 */
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_RULES)
	JsonServiceResponse updateUdr(String userId, UdrSpecification udrToUpdate);

	/**
	 * Deletes a UDR object.
	 * 
	 * @param userId
	 *            the userId of the author.
	 * @param id
	 *            the id of the UDR record in the DB to be deleted.
	 * @return the service response JSON format.
	 */
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_RULES)
	JsonServiceResponse deleteUdr(String userId, Long id);

	public KnowledgeBase recompileRules(final String kbName, String userId);
}
