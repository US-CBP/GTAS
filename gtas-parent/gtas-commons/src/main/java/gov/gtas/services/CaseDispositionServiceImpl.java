/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.Case;

import javax.annotation.Resource;

import gov.gtas.model.HitsDisposition;
import gov.gtas.repository.CaseDispositionRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.HashSet;


@Service
public class CaseDispositionServiceImpl implements CaseDispositionService  {
    @Resource
    private CaseDispositionRepository caseDispositionRepository;

    @Override
    public Case create(Long flight_id, Long pax_id, Long hit_id) {
        Case aCase = new Case();

        aCase.setFlight_id(flight_id);
        aCase.setPax_id(pax_id);
        aCase.setStatus("New");
        //aCase.setHits_disp(new HashSet<>().add(new HitsDisposition(hit_id)));
        caseDispositionRepository.save(aCase);
        return aCase;
    }

    //    @Override
//    @Transactional
//    public Case create(Carrier carrier) {
//        return carrierRespository.save(carrier);
//    }
//
//    @Override
//    @Transactional
//    public Carrier delete(Long id) {
//        Carrier carrier = this.findById(id);
//        if(carrier != null){
//            carrierRespository.delete(carrier);
//        }
//        return carrier;
//    }
//
//    @Override
//    @Transactional
//    public List<Carrier> findAll() {
//
//        return (List<Carrier>)carrierRespository.findAll();
//    }
//
//    @Override
//    @Transactional
//    public Carrier update(Carrier carrier) {
//        // NO IMPLEMENTATION
//        return null;
//    }
//
//    @Override
//    @Transactional
//    public Carrier findById(Long id) {
//
//        return carrierRespository.findOne(id);
//    }
//

}