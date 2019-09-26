/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGE_ADMIN;
import gov.gtas.model.Whitelist;
import gov.gtas.vo.WhitelistVo;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * The Interface WhitelistService.
 */
@PreAuthorize(PRIVILEGE_ADMIN)
public interface WhitelistService {

	/**
	 * Find and return the list of all Whitelist that have the "delete flag" set to
	 * * "N".
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
	 *            the user Id of the person deleting the whitelist (usually also the
	 *            whitelist editor.).
	 */
	public void delete(Long id, String userId);

	/**
	 * Creates a new Whitelist.
	 * 
	 * @param wlv
	 *            the WhitelistVo to create a new whitelist.
	 * @param userId
	 *            the user Id of the person persisting the whitelist (usually also
	 *            the whitelist editor.)
	 */
	public Whitelist create(WhitelistVo wlv, String userId);

	/**
	 * Updates a existing whitelist.
	 * 
	 * @param wlv
	 *            the WhitelistVo to update the existing whitelist.
	 * @param userId
	 *            the editor Id of the person updating the whitelist (usually also
	 *            the whitelist editor).
	 */
	public void update(WhitelistVo wlv, String userId);

}
