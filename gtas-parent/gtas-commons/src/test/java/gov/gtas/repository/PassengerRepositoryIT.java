package gov.gtas.repository;

import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.model.Passenger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonServicesConfig.class,CachingConfig.class })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class PassengerRepositoryIT {

    @Autowired
    private PassengerRepository passengerDao;
    private static final Logger logger = LoggerFactory
            .getLogger(PassengerRepositoryIT.class);

    @Test
    @Transactional
    public void testRetrieveNotNullIdTagPax() {

        List<Passenger> paxList = passengerDao.getNotNullIdTaggedPassenger("","","","",new Date());
        int count = passengerDao.getNullIdTagPassengers().size();
        List<Passenger> paxListNotNull = passengerDao.getNotNullIdTagPassengers();
        List<Passenger> paxListWithNullIdTags = passengerDao.getNullIdTagPassengers();

        final java.util.Random rand = new java.util.Random();

        for(Passenger _tempPaxWithIdTag : paxListNotNull){
            boolean paxFoundFlag = false;
            Passenger _tempPaxWithIdTagThatMatched = new Passenger();
            for(Passenger _tempPaxWithNoIdTag : paxListWithNullIdTags){
                if((_tempPaxWithIdTag.getFirstName().equalsIgnoreCase(_tempPaxWithNoIdTag.getFirstName()))
                        &&(_tempPaxWithIdTag.getLastName().equalsIgnoreCase(_tempPaxWithNoIdTag.getLastName()))
                        &&(_tempPaxWithIdTag.getDob().equals(_tempPaxWithNoIdTag.getDob()))
                        &&(_tempPaxWithIdTag.getCitizenshipCountry().equalsIgnoreCase(_tempPaxWithNoIdTag.getCitizenshipCountry()))
                        &&(_tempPaxWithIdTag.getGender().equalsIgnoreCase(_tempPaxWithNoIdTag.getGender()))
                        ){
                        // match found
                        paxFoundFlag = true;
                        _tempPaxWithNoIdTag.setIdTag(_tempPaxWithIdTag.getIdTag());
                        break;
                }

            }

            if(paxFoundFlag){ // Passenger found, apply IdTag to the new record

            }else{ //generate IdTag and update Passenger Record

            }
        } // end of Passenger with No IdTag loop
        assertTrue(passengerDao.getNullIdTagPassengers()!=null);
        assertTrue(!paxList.isEmpty());

    }

    private String generateRandomIDTag(int strLength){
        final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
        final java.util.Random rand = new java.util.Random();
        StringBuilder builder = new StringBuilder();
        while(builder.toString().length() == 0) {
            int length = (strLength>0)?(strLength):(9);
            for(int i = 0; i < strLength; i++) {
                builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
            }
        }
        return builder.toString();
    }

}
