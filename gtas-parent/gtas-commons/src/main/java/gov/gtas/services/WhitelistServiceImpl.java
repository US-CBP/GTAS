/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.enumtype.YesNoEnum;
import gov.gtas.model.User;
import gov.gtas.model.Whitelist;
import gov.gtas.repository.UserRepository;
import gov.gtas.repository.WhitelistRepository;
import gov.gtas.vo.WhitelistVo;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class WhitelistServiceImpl.
 */
@Service
public class WhitelistServiceImpl implements WhitelistService {

	private static final Logger logger = LoggerFactory.getLogger(WhitelistServiceImpl.class);

	@Autowired
	private WhitelistRepository whitelistRepository;

	@Autowired
	private UserRepository userRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.services.WhitelistService#getAllWhitelists()
	 */
	@Override
	@Transactional
	public List<WhitelistVo> getAllWhitelists() {
		List<Whitelist> whitelists = whitelistRepository.findBydeleted(YesNoEnum.N);
		List<WhitelistVo> wlvList = new ArrayList<>();
		if (whitelists != null) {
			whitelists.forEach(wl -> {
				WhitelistVo wlv = mapWhitelistVoFromWhitelist(wl);
				wlvList.add(wlv);
			});
		}
		return wlvList;
	}

	private WhitelistVo mapWhitelistVoFromWhitelist(Whitelist wl) {
		WhitelistVo wlv = new WhitelistVo();
		wlv.setFirstName(wl.getFirstName());
		wlv.setMiddleName(wl.getMiddleName());
		wlv.setLastName(wl.getLastName());
		wlv.setGender(wl.getGender());
		wlv.setDob(wl.getDob());
		wlv.setNationality(wl.getNationality());
		wlv.setResidencyCountry(wl.getResidencyCountry());
		wlv.setDocumentType(wl.getDocumentType());
		wlv.setDocumentNumber(wl.getDocumentNumber());
		wlv.setExpirationDate(wl.getExpirationDate());
		wlv.setIssuanceDate(wl.getIssuanceDate());
		wlv.setIssuanceCountry(wl.getIssuanceCountry());
		return wlv;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.services.WhitelistService#delete(java.lang.Long,
	 * java.lang.String)
	 */
	@Override
	@Transactional
	public void delete(Long id, String userId) {
		Whitelist wl = whitelistRepository.findById(id).orElse(null);
		if (wl != null) {
			wl.setDeleted(YesNoEnum.Y);
		} else {
			logger.warn("The id = " + id + " of whitelist object does not exist.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.services.WhitelistService#create(gov.gtas.vo.WhitelistVo,
	 * java.lang.String)
	 */
	@Override
	@Transactional
	public Whitelist create(WhitelistVo wlv, String userId) {
		Whitelist newWl = mapWhitelistfromWhitelistVo(wlv);
		User userEntity = userRepository.findById(userId).orElse(null);
		newWl.setDeleted(YesNoEnum.N);
		newWl.setWhiteListEditor(userEntity);
		return whitelistRepository.save(newWl);
	}

	private Whitelist mapWhitelistfromWhitelistVo(WhitelistVo wlv) {
		Whitelist newWl = new Whitelist();
		newWl.setFirstName(wlv.getFirstName());
		newWl.setMiddleName(wlv.getMiddleName());
		newWl.setLastName(wlv.getLastName());
		newWl.setGender(wlv.getGender());
		newWl.setDob(wlv.getDob());
		newWl.setNationality(wlv.getNationality());
		newWl.setResidencyCountry(wlv.getResidencyCountry());
		newWl.setDocumentType(wlv.getDocumentType());
		newWl.setDocumentNumber(wlv.getDocumentNumber());
		newWl.setExpirationDate(wlv.getExpirationDate());
		newWl.setIssuanceDate(wlv.getIssuanceDate());
		newWl.setIssuanceCountry(wlv.getIssuanceCountry());
		return newWl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.services.WhitelistService#update(gov.gtas.vo.WhitelistVo,
	 * java.lang.String)
	 */
	@Override
	@Transactional
	public void update(WhitelistVo wlv, String userId) {
		Whitelist existing = null;
		if (wlv.getId() != null)
			existing = whitelistRepository.findById(wlv.getId()).orElse(null);
		else {
			throw new RuntimeException("the id of a existing whitelist object is null.");
		}
		if (existing != null) {
			if (wlv.getNationality() != null)
				existing.setNationality(wlv.getNationality());
			if (wlv.getDob() != null)
				existing.setDob(wlv.getDob());
			if (wlv.getDocumentNumber() != null)
				existing.setDocumentNumber(wlv.getDocumentNumber());
			if (wlv.getDocumentType() != null)
				existing.setDocumentType(wlv.getDocumentType());
			if (wlv.getExpirationDate() != null)
				existing.setExpirationDate(wlv.getExpirationDate());
			if (wlv.getFirstName() != null)
				existing.setFirstName(wlv.getFirstName());
			if (wlv.getGender() != null)
				existing.setGender(wlv.getGender());
			if (wlv.getIssuanceCountry() != null)
				existing.setIssuanceCountry(wlv.getIssuanceCountry());
			if (wlv.getNationality() != null)
				existing.setIssuanceDate(wlv.getIssuanceDate());
			if (wlv.getIssuanceDate() != null)
				existing.setLastName(wlv.getLastName());
			if (wlv.getMiddleName() != null)
				existing.setMiddleName(wlv.getMiddleName());
			if (wlv.getResidencyCountry() != null)
				existing.setResidencyCountry(wlv.getResidencyCountry());
			User userEntity = userRepository.findById(userId).orElse(null);
			existing.setWhiteListEditor(userEntity);
		}
	}
}
