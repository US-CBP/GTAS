/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.vo.WhitelistVo;

import java.util.List;

/**
 * The Interface WhitelistService.
 */
public interface WhitelistService {

	/**
	 * Find and return the list of all Whitelist that have the "delete flag" set
	 * to * "N".
	 *
	 * @return list of all whitelists.
	 */
	public List<WhitelistVo> getAllWhitelists();

	/**
	 * Deletes a existing whitelist. The object is not physically deleted, but a
	 * "delete flag" is set to indicate it is no longer in use.
	 * 
	 * @param id
	 *            the Id of the whitelist to delete.
	 * @param userId
	 *            the user Id of the person deleting the whitelist (usually also
	 *            the whitelist editor.).
	 */
	public void delete(Long id, String userId);

	/**
	 * Creates a new Whitelist.
	 * 
	 * @param wlv
	 *            the WhitelistVo to create a new whitelist.
	 * @param userId
	 *            the user Id of the person persisting the whitelist (usually
	 *            also the whitelist editor.)
	 */
	public void create(WhitelistVo wlv, String userId);

	/**
	 * Updates a existing whitelist.
	 * 
	 * @param wlv
	 *            the WhitelistVo to update the existing whitelist.
	 * @param userId
	 *            the editor Id of the person updating the whitelist (usually
	 *            also the whitelist editor).
	 */
	public void update(WhitelistVo wlv, String userId);

}
