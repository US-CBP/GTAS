package gov.gtas.repository;

import gov.gtas.config.CommonServicesConfig;
import gov.gtas.model.Case;
import gov.gtas.model.HitsDisposition;
import gov.gtas.model.HitsDispositionComments;
import gov.gtas.model.lookup.DispositionStatusCode;
import gov.gtas.services.CaseDispositionService;
import gov.gtas.repository.CaseDispositionRepository;
import gov.gtas.services.dto.CasePageDto;
import gov.gtas.services.dto.CaseRequestDto;
import gov.gtas.vo.passenger.CaseVo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonServicesConfig.class })
public class CaseDispositionRepositoryIT {

    @Before
    public void setUp() throws Exception {

       /* Random _rand = new Random();
        List<Long> _tempHitList = new ArrayList<>();
        _tempHitList.add(new Long(_rand.nextInt(1000)));
        _tempHitList.add(new Long(_rand.nextInt(1000)));
        _tempHitList.add(new Long(_rand.nextInt(1000)));

        Case aCase = new Case();
        HitsDisposition hitDisp = new HitsDisposition();
        HitsDispositionComments hitsDispositionComments = new HitsDispositionComments();
        Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
        Set<HitsDispositionComments> hitsDispCommentsSet = new HashSet<HitsDispositionComments>();

        aCase.setFlightId(new Long(_rand.nextInt(1000)));
        aCase.setPaxId(new Long(_rand.nextInt(1000)));
        aCase.setStatus(DispositionStatusCode.NEW.toString());
        for (Long _tempHitId : _tempHitList) {
            hitDisp = new HitsDisposition();
            hitsDispCommentsSet = new HashSet<>();
            hitDisp.setHitId(_tempHitId);
            hitDisp.setStatus(DispositionStatusCode.NEW.toString());
            hitsDispositionComments = new HitsDispositionComments();
            hitsDispositionComments.setHitId(_tempHitId);
            hitsDispositionComments.setComments("Initial Comment");
            hitsDispCommentsSet.add(hitsDispositionComments);
            hitDisp.setDispComments(hitsDispCommentsSet);
            hitsDispSet.add(hitDisp);
        }
        aCase.setHitsDispositions(hitsDispSet);

        caseDispositionRepository.save(aCase);*/
    }

    @After
    public void tearDown() throws Exception {
    }

    @Autowired
    private CaseDispositionRepository caseDispositionRepository;

    @Autowired
    private CaseDispositionService caseDispositionService;

    @Test
    public void testFindAllPageable() throws Exception {

        Pageable pageable = new Pageable() {
            @Override
            public int getPageNumber() {
                return 1;
            }

            @Override
            public int getPageSize() {
                return 10;
            }

            @Override
            public int getOffset() {
                return 0;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public Pageable next() {
                return null;
            }

            @Override
            public Pageable previousOrFirst() {
                return null;
            }

            @Override
            public Pageable first() {
                return null;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }
        };

        CaseRequestDto _tempDto = new CaseRequestDto();
        _tempDto.setPageNumber(pageable.getPageNumber());
        _tempDto.setPageSize(pageable.getPageSize());
        //_tempDto.setFlightId(20L);

        CasePageDto _tempCaseDto = caseDispositionService.findAll(_tempDto);
        for(CaseVo aCase : _tempCaseDto.getcases()){
            Set<HitsDisposition> _tempSet = caseDispositionRepository.getHitsDispositionByCaseId(aCase.getId());
            for(HitsDisposition _hit : aCase.getHitsDispositions()){
                System.out.println(_hit);
            }
        }
        assertNotNull( caseDispositionRepository.findAll());
    }
}
