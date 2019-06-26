package gov.gtas.services;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.model.PassengerIDTag;
import gov.gtas.repository.PassengerIDTagRepository;

@Service
public class PassengerResolverServiceImpl implements PassengerResolverService {

	@Autowired
	private PassengerIDTagRepository passengerIDTagRepository;

	@Override
	public List<Long> resolve(Long pax_id) {
		//
		PassengerIDTag idTag = this.passengerIDTagRepository.findByPaxId(pax_id);

		if (idTag != null && idTag.getTamrId() != null) {
			//
			return this.passengerIDTagRepository.findPaxIdsByTamrId(idTag.getTamrId());
		} else {
			//Edge case - passenger failed to create pax id.
			if (idTag == null || idTag.getIdTag() == null) {
				return Collections.singletonList(pax_id);
			}
			return this.passengerIDTagRepository.findPaxIdsByTagId(idTag.getIdTag());
		}
	}

}
