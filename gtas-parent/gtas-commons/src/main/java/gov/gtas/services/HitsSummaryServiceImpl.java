/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.HitsSummary;
import gov.gtas.repository.HitsSummaryRepository;

import java.util.Arrays;
import java.util.List;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGES_ADMIN_AND_MANAGE_HITS;

@Service
@Transactional
public class HitsSummaryServiceImpl implements HitsSummaryService {

	@Autowired
	HitsSummaryRepository hitsSummaryRepository;

	@Override
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_HITS)
	public HitsSummary findByPassengerId(Long id) {
		return hitsSummaryRepository.findRuleHitsByPassengerId(id);
	}

	@Override
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_HITS)
	public Iterable<HitsSummary> findAll() {
		return hitsSummaryRepository.findAll();
	}

	@Override
	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_HITS)
	public List<HitsSummary> findHitsByFlightId(Long flightId) {
		return hitsSummaryRepository.findHitsByFlightId(flightId);
	}

	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_HITS)
	public List<HitsSummary> findByFlightIdAndPassengerIdAndUdrRule(Long fightId, Long passengerId) {
		List<String> listHitTypes = Arrays.asList("R", "RPD", "RP", "RD");
		return hitsSummaryRepository.findByFlightIdAndPassengerIdWithHitTypes(fightId, passengerId, listHitTypes);
	}

	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_HITS)
	public List<HitsSummary> findByFlightIdAndPassengerIdAndWL(Long fightId, Long passengerId) {
		List<String> listHitTypes = Arrays.asList("P", "D", "PD");
		return hitsSummaryRepository.findByFlightIdAndPassengerIdWithHitTypes(fightId, passengerId, listHitTypes);
	}

	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_HITS)
	public List<HitsSummary> findByFlightIdAndPassengerIdAndCombinedWithUdrRule(Long fightId, Long passengerId) {
		List<String> listHitTypes = Arrays.asList("RPD", "RP", "RD");
		return hitsSummaryRepository.findByFlightIdAndPassengerIdWithHitTypes(fightId, passengerId, listHitTypes);
	}

	@PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_HITS)
	public List<HitsSummary> findByFlightIdAndPassengerId(Long fightId, Long passengerId) {
		return hitsSummaryRepository.findByFlightIdAndPassengerId(fightId, passengerId);
	}

	@Override
	public HitsSummary getMostRecentHitsSummary() {
		return hitsSummaryRepository.findFirstByOrderByIdDesc();
	}

	@Override
	public List<HitsSummary> findByIds(List<Long> ids) {
		return (List<HitsSummary>) hitsSummaryRepository.findAllById(ids);
	}

}
