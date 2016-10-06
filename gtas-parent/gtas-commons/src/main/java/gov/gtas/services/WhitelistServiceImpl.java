/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.enumtype.YesNoEnum;
import gov.gtas.model.Whitelist;
import gov.gtas.repository.WhitelistRepository;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WhitelistServiceImpl implements WhitelistService {

	private static final Logger logger = LoggerFactory
			.getLogger(WhitelistServiceImpl.class);

	@Resource
	private WhitelistRepository whitelistRepository;

	@Override
	@Transactional
	public List<Whitelist> getAllWhitelists() {
		return whitelistRepository.findAllbydeleted(YesNoEnum.N);
	}

	@Override
	@Transactional
	public Whitelist delete(Long id, String userId) {
		Whitelist wl = whitelistRepository.findOne(id);
		if (wl != null) {
			whitelistRepository.delete(wl);
		} else {
			logger.warn("The id = " + id
					+ " of whitelist object does not exist.");
		}
		return wl;
	}

	@Override
	@Transactional
	public Whitelist create(Whitelist wl, String userId) {
		return whitelistRepository.save(wl);
	}

	@Override
	@Transactional
	public Whitelist update(Whitelist wl, String userId) {
		Whitelist updatedWl = null;
		Whitelist existing = whitelistRepository.findOne(wl.getId());
		if (existing != null) {
			if (wl.getCitizenshipCountry() != null)
				existing.setCitizenshipCountry(wl.getCitizenshipCountry());
			if (wl.getDeleted() != null)
				existing.setDeleted(wl.getDeleted());
			if (wl.getDeleted() != null)
				existing.setDob(wl.getDob());
			if (wl.getDocumentNumber() != null)
				existing.setDocumentNumber(wl.getDocumentNumber());
			if (wl.getDocumentType() != null)
				existing.setDocumentType(wl.getDocumentType());
			if (wl.getExpirationDate() != null)
				existing.setExpirationDate(wl.getExpirationDate());
			if (wl.getFirstName() != null)
				existing.setFirstName(wl.getFirstName());
			if (wl.getGender() != null)
				existing.setGender(wl.getGender());
			if (wl.getIssuanceCountry() != null)
				existing.setIssuanceCountry(wl.getIssuanceCountry());
			if (wl.getCitizenshipCountry() != null)
				existing.setIssuanceDate(wl.getIssuanceDate());
			if (wl.getIssuanceDate() != null)
				existing.setLastName(wl.getLastName());
			if (wl.getMiddleName() != null)
				existing.setMiddleName(wl.getMiddleName());
			if (wl.getResidencyCountry() != null)
				existing.setResidencyCountry(wl.getResidencyCountry());
			if (wl.getWhiteListEditor() != null)
				existing.setWhiteListEditor(wl.getWhiteListEditor());

			updatedWl = whitelistRepository.save(existing);
		}
		return updatedWl;
	}
}
