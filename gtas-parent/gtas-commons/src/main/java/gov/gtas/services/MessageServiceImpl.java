/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.Message;
import gov.gtas.repository.ApisMessageRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    ApisMessageRepository apisMessageRepo;

    public List<Message> getAPIsByDates (Date startDate, Date endDate){

        //return apisMessageRepo.getAPIsByDates(startDate, endDate);
        return apisMessageRepo.getAPIsByDates();

    }

}
