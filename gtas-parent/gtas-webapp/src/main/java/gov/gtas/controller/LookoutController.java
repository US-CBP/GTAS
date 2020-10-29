package gov.gtas.controller;

import gov.gtas.model.LookoutRequest;
import gov.gtas.repository.LookoutRequestRepository;
import gov.gtas.services.dto.LookoutSendRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LookoutController {

    @Autowired
    LookoutRequestRepository lookoutRequestRepository;

    @RequestMapping(value = "/lookout/send", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void updateMessages(@RequestBody LookoutSendRequest lookoutSendRequest, HttpServletRequest hsr) {
        LookoutRequest lr = LookoutRequest.from(lookoutSendRequest);
        if (lookoutSendRequest.getPassengerId() == null || lookoutSendRequest.getCountryGroupName() == null) {
            throw new RuntimeException("Passenger ID or Country group name is null!");
        } else {
            lookoutRequestRepository.save(lr);
        }
    }
}
