/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.RuleCat;
import gov.gtas.repository.RuleCatRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RuleCatServiceImpl implements RuleCatService {


    @Resource
    private RuleCatRepository ruleCatRepository;

    @Override
    public RuleCat findRuleCatByID(Long id) {
        return ruleCatRepository.findOne(id);
    }

    @Override
    public Iterable<RuleCat> findAll() {
        return (List<RuleCat>) ruleCatRepository.findAll();
    }
}
