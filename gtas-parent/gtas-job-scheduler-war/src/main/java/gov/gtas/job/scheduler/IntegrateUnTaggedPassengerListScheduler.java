/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerIDTag;
import gov.gtas.repository.PassengerIDTagRepository;
import gov.gtas.repository.PassengerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class IntegrateUnTaggedPassengerListScheduler {


    private static final Logger logger = LoggerFactory
            .getLogger(IntegrateUnTaggedPassengerListScheduler.class);

    @Autowired
    private PassengerRepository passengerDao;

    @Autowired
    private PassengerIDTagRepository passengerIDTagRepository;

    //@Scheduled(fixedDelayString = "${cleanup.fixedDelay.in.milliseconds}", initialDelayString = "${cleanup.initialDelay.in.milliseconds}")
    public void jobScheduling() throws IOException {

        logger.info("PassengerIDTag matchup service START ");
        Iterable<Passenger> paxList = passengerDao.findAll();
        //int count = passengerDao.getNullIdTagPassengers().size();

        List<Passenger> paxListNotNull = new ArrayList<Passenger>();
        List<Passenger> paxListWithNullIdTags = new ArrayList<Passenger>();
//        passengerDao.getNotNullIdTagPassengers();
//        Iterable<Passenger> paxList2  = passengerDao.getNullIdTagPassengers();


        for(Passenger _tempPax : paxList){
/*            if(_tempPax.getPaxIdTag() == null){
                paxListWithNullIdTags.add(_tempPax);
            }else{
                paxListNotNull.add(_tempPax);
            }*/
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
                        //_tempPaxWithNoIdTag.setPaxIdTag(_tempPaxWithIdTag.getPaxIdTag());
                    }
                }

            } // end of Passenger with No IdTag loop

        }catch (Exception ex){
            logger.error("error in job scheduling", ex);
        }

        // Now persist this checked set of passengers
        for(Passenger _checkedListOfNullIdTagPassenger : paxListWithNullIdTags){

            //create new PassengerIDTag entity
            ArrayList<PassengerIDTag> _tempPaxIdTagList = new ArrayList<>();
            PassengerIDTag _tempPaxIDTag = new PassengerIDTag();


/*            if(_checkedListOfNullIdTagPassenger.getPaxIdTag()==null){ // Passenger found, apply IdTag to the new record
                _tempPaxIDTag.setIdTag(generateRandomIDTag(9));
                _tempPaxIdTagList.add(_tempPaxIDTag);
                _checkedListOfNullIdTagPassenger.setPaxIdTag(_tempPaxIDTag);
                //_tempPaxIDTag.getPassengers().add(_checkedListOfNullIdTagPassenger);
            }*/
            passengerIDTagRepository.saveAll(_tempPaxIdTagList);
            passengerDao.save(_checkedListOfNullIdTagPassenger);
        }

        logger.info("PassengerIDTag matchup service END ");

    }

    /**
     * Util method to generate a unique id tag each time
     * @param strLength
     * @return
     */
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



//    /**
//     * Util method takes top 5 attributes for a Passenger and returns a hash
//     * @param firstName
//     * @param lastName
//     * @param gender
//     * @param DOB
//     * @param ctz_country
//     * @return
//     * @throws NoSuchAlgorithmException
//     * @throws UnsupportedEncodingException
//     */
//    private String getHashForPassenger(String firstName, String lastName, String gender, String DOB, String ctz_country) throws NoSuchAlgorithmException, UnsupportedEncodingException{
//        return makeSHA1Hash(String.join("", Arrays.asList(firstName.toUpperCase(), lastName.toUpperCase(), gender.toUpperCase(), DOB, ctz_country.toUpperCase())));
//    }
//
//    /**
//     * Util method takes a Passenger object and return a hash for the top 5 attributes
//     * @param pax
//     * @return
//     * @throws NoSuchAlgorithmException
//     * @throws UnsupportedEncodingException
//     */
//    private String getHashForPassenger(Passenger pax) throws NoSuchAlgorithmException, UnsupportedEncodingException{
//        return makeSHA1Hash(String.join("", Arrays.asList(pax.getFirstName().toUpperCase(), pax.getLastName().toUpperCase(),
//                pax.getGender().toUpperCase(), pax.getDob().toString(), pax.getCitizenshipCountry().toUpperCase())));
//    }

    /**
     * Util method to hash passenger attributes
     * @param input
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
//    private String makeSHA1Hash(String input)
//            throws NoSuchAlgorithmException, UnsupportedEncodingException
//    {
//        MessageDigest md = MessageDigest.getInstance("SHA1");
//        md.reset();
//        byte[] buffer = input.getBytes("UTF-8");
//        md.update(buffer);
//        byte[] digest = md.digest();
//
//        String hexStr = "";
//        for (int i = 0; i < digest.length; i++) {
//            hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
//        }
//        return hexStr;
//    }


}
