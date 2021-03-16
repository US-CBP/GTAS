/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.POELane;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.POEService;
import gov.gtas.services.dto.LookoutStatusDTO;
import gov.gtas.services.dto.POETileServiceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;


@RestController
public class POEController {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(POETileServiceRequest.DATE_FORMAT);
    private final ObjectMapper objectMapper = new ObjectMapper().setDateFormat(simpleDateFormat);

    private static final Logger logger = LoggerFactory.getLogger(POEController.class);
    private final POEService poeService;

    @Autowired
    public POEController(POEService poeService) {
        this.poeService = poeService;
    }

    // GET ALL LANES
    @RequestMapping(method = RequestMethod.GET, value = "/api/POE/lanes")
    public List<POELane> getAllLanes() {
        return poeService.getAllLanes();
    }

    // GET ALL TILES
    @RequestMapping(method = RequestMethod.GET, value = "/api/POE/tiles", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Set<LookoutStatusDTO> getAllTiles(@RequestParam(value = "requestDto", required = false) String requestDto) throws IOException {
        final POETileServiceRequest req = objectMapper.readValue(requestDto, POETileServiceRequest.class); //thread safe
        return poeService.getAllTiles(GtasSecurityUtils.fetchLoggedInUserId(),req);
    }

    // UPDATE POE STATUS FOR A GIVEN PASSENGER
    @RequestMapping(method = RequestMethod.PUT, value = "/api/POE/tiles",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonServiceResponse updateStatus(@RequestBody LookoutStatusDTO poeTile) {
        return poeService.updateStatus(poeTile);
    }

}
