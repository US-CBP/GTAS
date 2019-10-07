/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import gov.gtas.model.ApiAccess;
import gov.gtas.repository.ApiAccessRepository;

@Service
public class ApiAccessServiceImpl implements ApiAccessService {

	BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Resource
	private ApiAccessRepository apiAccessRepository;

	@Override
	@Transactional
	public ApiAccess create(ApiAccess apiAccess) {
		apiAccess.setPassword(passwordEncoder.encode(apiAccess.getPassword()));
		return apiAccessRepository.save(apiAccess);
	}

	@Override
	@Transactional
	public ApiAccess delete(Long id) {
		ApiAccess apiAccess = this.findById(id);
		if (apiAccess != null) {
			apiAccessRepository.delete(apiAccess);
		}
		return apiAccess;
	}

	@Override
	@Transactional
	public List<ApiAccess> findAll() {
		return (List<ApiAccess>) apiAccessRepository.findAll();
	}

	@Override
	@Transactional
	public ApiAccess update(ApiAccess apiAccess) {
		// If the password changed we need to encrypt it
		if (!passwordEncoder.matches(findById(apiAccess.getId()).getPassword(), apiAccess.getPassword())) {
			apiAccess.setPassword(passwordEncoder.encode(apiAccess.getPassword()));
		}
		return apiAccessRepository.save(apiAccess);
	}

	@Override
	@Transactional
	public ApiAccess findById(Long id) {
		return apiAccessRepository.findById(id).orElse(null);
	}

}
