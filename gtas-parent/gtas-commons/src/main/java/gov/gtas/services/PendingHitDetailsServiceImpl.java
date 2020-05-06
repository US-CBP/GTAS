package gov.gtas.services;

import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.HitMaker;
import gov.gtas.model.ManualHit;
import gov.gtas.model.PendingHitDetails;
import gov.gtas.model.User;
import gov.gtas.repository.HitMakerRepository;
import gov.gtas.repository.PendingHitDetailRepository;
import gov.gtas.services.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;

@Service
public class PendingHitDetailsServiceImpl implements PendingHitDetailsService {

    @Autowired
    PendingHitDetailRepository phr;

    @Autowired
    HitMakerRepository hmr;

    @Autowired
    UserService userService;

    @Autowired
    HitCategoryService hitCategoryService;

    //For generation of manual hits.
    @Override
    @Transactional
    public void createManualPendingHitDetail(Long paxId, Long flightId, String userId, Long hitCategoryId, String desc){
        PendingHitDetails phd = new PendingHitDetails();
        User user = userService.fetchUser(userId);

        //Manual hit hit maker must be present
        ManualHit mh = new ManualHit();
        mh.setDescription("Manual Hit Generated On Passenger Detail Page");
        mh.setAuthor(user);
        mh.setHitCategory(hitCategoryService.findById(hitCategoryId));
        hmr.save(mh);

        phd.setTitle("Manual PVL Generation");
        phd.setDescription(desc);

        phd.setHitEnum(HitTypeEnum.MANUAL_HIT);
        phd.setHitType(HitTypeEnum.MANUAL_HIT.toString());
        phd.setPercentage(1);
        //Manual hit generation, no rule conditions.
        phd.setRuleConditions("N/A");
        phd.setPassengerId(paxId);
        phd.setFlightId(flightId);

        phd.setCreatedDate(new Date());
        phd.setCreatedBy(userId);

        phd.setHitMaker(mh);
        phd.setHitMakerId(mh.getId());

        phr.save(phd);
    };

    @Override
    public void saveAllPendingHitDetails(Set<PendingHitDetails> phdSet){
        phr.saveAll(phdSet);
    };

}
