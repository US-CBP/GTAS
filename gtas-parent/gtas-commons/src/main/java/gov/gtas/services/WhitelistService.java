/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.Whitelist;

import java.util.List;

/**
 * The Interface WhitelistService.
 */
public interface WhitelistService {

	/*
	 * Find and return the list of all Whitelist that have the "delete flag" set
	 * to * "N".
	 * 
	 * @return list of all whitelist.
	 */
	public List<Whitelist> getAllWhitelists();

	/**
	 * Deletes a whitelist. The object is not physically deleted, but a
	 * "delete flag" is set to indicate it is no longer in use.
	 * 
	 * @param id
	 *            the Id of the whitelist to delete.
	 * @param userId
	 *            the user Id of the person deleting the whitelist (usually also
	 *            the whitelist editor.).
	 * @return the deleted whitelist.
	 */
	public Whitelist delete(Long id, String userId);

	/**
	 * Creates a Whitelist.
	 * 
	 * @param whitelist
	 *            the Whitelist object to persist in the DB.
	 * @param userId
	 *            the user Id of the person persisting the whitelist (usually
	 *            also the whitelist editor.)
	 * @return the persisted whitelist.
	 */
	public Whitelist create(Whitelist rule, String userId);

	/**
	 * Updates a whitelist.
	 * 
	 * @param whitelist
	 *            the Whitelist to update.
	 * @param userId
	 *            the editor Id of the person updating the whitelist (usually
	 *            also the whitelist editor).
	 * @return the updated Whitelist.
	 */
	public Whitelist update(Whitelist whitelist, String userId);

}
