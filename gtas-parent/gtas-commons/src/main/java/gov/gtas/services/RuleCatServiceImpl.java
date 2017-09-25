/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.RuleCat;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.repository.RuleCatRepository;
import gov.gtas.repository.udr.UdrRuleRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class RuleCatServiceImpl implements RuleCatService {


    @Resource
    private RuleCatRepository ruleCatRepository;

    @Resource
    private UdrRuleRepository udrRuleRepository;

    @Override
    public RuleCat findRuleCatByID(Long id) {
        return ruleCatRepository.findOne(id);
    }

    @Override
    public Iterable<RuleCat> findAll() {
        return (List<RuleCat>) ruleCatRepository.findAll();
    }

    @Override
    public Long fetchRuleCatPriorityIdFromRuleId(Long ruleId) throws Exception{

        UdrRule _tempRule = udrRuleRepository.findOne(ruleId);
        RuleCat _tempRuleCat = null;
        if(_tempRule!=null){
            Set<RuleCat> _tempRuleCatSet = _tempRule.getMetaData().getRuleCategories();

            _tempRuleCat = _tempRuleCatSet.stream().sorted(Comparator.comparing(RuleCat::getPriority).reversed())
                    .findFirst()
                    .orElse(null);
            if(_tempRuleCat==null)return 1L; // bracket orphans to 'General' rule category
            else return _tempRuleCat.getPriority();

        }else {
            // bracket orphans to 'General' rule category
            return 1L;
        }

    }
}
