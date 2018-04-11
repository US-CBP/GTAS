/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import gov.gtas.model.Passenger;
import gov.gtas.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class IntegrateUnTaggedPassengerListScheduler {


    @Autowired
    private PassengerRepository passengerDao;

    @Scheduled(fixedDelayString = "${loader.fixedDelay.in.milliseconds}", initialDelayString = "${loader.initialDelay.in.milliseconds}")
    public void jobScheduling() throws IOException {

        Iterable<Passenger> paxList = passengerDao.findAll();
        //int count = passengerDao.getNullIdTagPassengers().size();

        List<Passenger> paxListNotNull = new ArrayList<Passenger>();
        List<Passenger> paxListWithNullIdTags = new ArrayList<Passenger>();
                //passengerDao.getNotNullIdTagPassengers();
                //passengerDao.getNullIdTagPassengers();

        for(Passenger _tempPax : paxList){
            if(_tempPax.getIdTag() == null){
                paxListWithNullIdTags.add(_tempPax);
            }else{
                paxListNotNull.add(_tempPax);
            }
        }

        final java.util.Random rand = new java.util.Random();

        try {
            for (Passenger _tempPaxWithIdTag : paxListNotNull) {
                boolean paxFoundFlag = false;
                Passenger _tempPaxWithIdTagThatMatched = new Passenger();
                for (Passenger _tempPaxWithNoIdTag : paxListWithNullIdTags) {
                    if ((_tempPaxWithIdTag.getFirstName().equalsIgnoreCase(_tempPaxWithNoIdTag.getFirstName()))
                            && (_tempPaxWithIdTag.getLastName().equalsIgnoreCase(_tempPaxWithNoIdTag.getLastName()))
                            && (_tempPaxWithIdTag.getDob().equals(_tempPaxWithNoIdTag.getDob()))
//                            && (_tempPaxWithIdTag.getCitizenshipCountry().equalsIgnoreCase(_tempPaxWithNoIdTag.getCitizenshipCountry()))
                            && (_tempPaxWithIdTag.getGender().equalsIgnoreCase(_tempPaxWithNoIdTag.getGender()))
                            ) {
                        // match found
                        _tempPaxWithNoIdTag.setIdTag(_tempPaxWithIdTag.getIdTag());
                    }
                }

            } // end of Passenger with No IdTag loop

        }catch (Exception ex){
            ex.printStackTrace();
        }

        // Now persist this checked set of passengers
        for(Passenger _checkedListOfNullIdTagPassenger : paxListWithNullIdTags){

            if(_checkedListOfNullIdTagPassenger.getIdTag()==null){ // Passenger found, apply IdTag to the new record
                _checkedListOfNullIdTagPassenger.setIdTag(generateRandomIDTag(9));
            }
            passengerDao.save(_checkedListOfNullIdTagPassenger);
        }

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
