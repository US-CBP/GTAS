/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.config.CommonServicesConfig;
import gov.gtas.model.Case;
import gov.gtas.repository.CaseDispositionRepository;
import gov.gtas.services.dto.CasePageDto;
import gov.gtas.services.dto.CaseRequestDto;
import gov.gtas.vo.passenger.CaseVo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.assertTrue;

//import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonServicesConfig.class })
public class CaseDispositionServiceImplTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Autowired
    private CaseDispositionService caseDispService;
    
    @Autowired
    private CaseDispositionRepository caseRepo;

    private static final String TEST_PAX_NAME="John Smith";
    private static final String TEST_PAX_TYPE="Passenger";
    private static final String TEST_PAX_DOC="123456789";
    private static final String TEST_PAX_CTZ="GBR";
    private static final String TEST_PAX_DOB="John Smith";

    @Test
    public void testCreateCase() throws Exception {
    	Random _rand = new Random();
        List<Long> _tempHitList = new ArrayList<>();
        _tempHitList.add(new Long(_rand.nextInt(1000)));
        _tempHitList.add(new Long(_rand.nextInt(1000)));
        _tempHitList.add(new Long(_rand.nextInt(1000)));

        assertTrue((caseDispService.create((new Long(_rand.nextInt(1000))), (new Long(_rand.nextInt(1000))),
                TEST_PAX_NAME,TEST_PAX_TYPE, TEST_PAX_CTZ,
                new Date(), TEST_PAX_DOC, "", _tempHitList))
                .getId()!=null);
    }
    

    //@Test
    public void testUpdateCaseComments(){
        CaseRequestDto inboundDto = new CaseRequestDto();
        List<CaseVo> _tempCases = new ArrayList<CaseVo>();
        CasePageDto outboundDto = new CasePageDto(_tempCases, 10);
        Case aCase = null;
        inboundDto.setPageNumber(1);
        inboundDto.setPageSize(10);
        inboundDto.setFlightId((long) 499);
    	inboundDto.setPaxId((long) 727);
        Long _tempHitId = new Long(720);
        try{

            aCase = caseDispService.addCaseComments(inboundDto.getFlightId(), inboundDto.getPaxId(), _tempHitId, "follow-up", "PENDING","true");

        }catch (Exception ex){
            ex.printStackTrace();
        }

    }


}