/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
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

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class WhitelistServiceImpl.
 */
@Service
public class WhitelistServiceImpl implements WhitelistService {

	private static final Logger logger = LoggerFactory
			.getLogger(WhitelistServiceImpl.class);

	@Resource
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
		List<Whitelist> whitelists = whitelistRepository
				.findAllbydeleted(YesNoEnum.N);
		List<WhitelistVo> wlvList = new ArrayList<>();
		whitelists.forEach(wl -> {
			WhitelistVo wlv = new WhitelistVo();
			BeanUtils.copyProperties(wlv, wl);
			wlvList.add(wlv);
		});
		return wlvList;
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
		Whitelist wl = whitelistRepository.findOne(id);
		if (wl != null) {
			wl.setDeleted(YesNoEnum.Y);
		} else {
			logger.warn("The id = " + id
					+ " of whitelist object does not exist.");
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
	public void create(WhitelistVo wlv, String userId) {
		Whitelist newWl = new Whitelist();
		BeanUtils.copyProperties(newWl, wlv);
		newWl.setId(null);
		User userEntity = userRepository.findOne(userId);
		newWl.setDeleted(YesNoEnum.N);
		newWl.setWhiteListEditor(userEntity);
		whitelistRepository.save(newWl);
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
			existing = whitelistRepository.findOne(wlv.getId());
		else {
			throw new RuntimeException(
					"the id of a existing whitelist object is null.");
		}
		if (existing != null) {
			if (wlv.getCitizenshipCountry() != null)
				existing.setCitizenshipCountry(wlv.getCitizenshipCountry());
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
			if (wlv.getCitizenshipCountry() != null)
				existing.setIssuanceDate(wlv.getIssuanceDate());
			if (wlv.getIssuanceDate() != null)
				existing.setLastName(wlv.getLastName());
			if (wlv.getMiddleName() != null)
				existing.setMiddleName(wlv.getMiddleName());
			if (wlv.getResidencyCountry() != null)
				existing.setResidencyCountry(wlv.getResidencyCountry());
			User userEntity = userRepository.findOne(userId);
			existing.setWhiteListEditor(userEntity);
		}
	}
}
