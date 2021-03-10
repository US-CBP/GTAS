/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */
package gov.gtas.services;

import gov.gtas.enumtype.POEStatusEnum;
import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.Document;
import gov.gtas.model.HitViewStatus;
import gov.gtas.model.POELane;
import gov.gtas.model.Passenger;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.repository.HitViewStatusRepository;
import gov.gtas.repository.POELaneRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.services.dto.POETileDTO;
import gov.gtas.services.dto.POETileServiceRequest;
import gov.gtas.services.security.UserService;
import gov.gtas.vo.passenger.DocumentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class POEServiceImpl implements POEService {

    @Resource
    private POELaneRepository poeLaneRepository;

    @Resource
    private HitViewStatusRepository hitViewStatusRepository;

    @Resource
    private PassengerRepository passengerRepository;

    @Autowired
    private UserService userService;

    @Override
    public Set<POETileDTO> getAllTiles(String userId, POETileServiceRequest request) {
        Set<POETileDTO> tiles = new HashSet<POETileDTO>();
       Set<HitViewStatus> hvs = hitViewStatusRepository.findAllWithNotClosedAndWithinRange(userService.fetchUserGroups(userId),request.getEtaStart(), request.getEtaEnd());
       for(HitViewStatus hv : hvs ){
          tiles.add(createPOETileDTO(hv)); //TODO: distinct passenger on query with a lot more configuration in the service layer for duplicates handling
       }
        return tiles;
    }

    @Override
    public List<POELane> getAllLanes() {
        List<POELane> list = StreamSupport
                .stream(poeLaneRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        return list;
    }

    @Override
    @Transactional
    public JsonServiceResponse updateStatus(POETileDTO poeTileDTO) {
        Passenger p = passengerRepository.findById(poeTileDTO.getPaxId()).orElseThrow(RuntimeException::new);
        Set<HitViewStatus> hvs = hitViewStatusRepository.findAllByPassenger(p);
        for(HitViewStatus hv : hvs){
            hv.setPoeStatusEnum(POEStatusEnum.valueOf(poeTileDTO.getPoeStatus()));
        }
        hitViewStatusRepository.saveAll(hvs);
        return new JsonServiceResponse(Status.SUCCESS, "success"); //TODO: More robust
    }

    private POETileDTO createPOETileDTO(HitViewStatus hvs){
        Long paxId = hvs.getPassenger().getId();
        String paxFirstName = hvs.getPassenger().getPassengerDetails().getFirstName();
        String paxLastName = hvs.getPassenger().getPassengerDetails().getLastName();
        Set<Document> documents = hvs.getPassenger().getDocuments();
        DocumentVo docVo = new DocumentVo();
        docVo.fromDocument(documents.iterator().next()); //TODO: first doc only for now
        HitCategory hitCategory = hvs.getHitDetail().getHitMaker().getHitCategory();
        Date flightCountdownTime = hvs.getPassenger().getFlight().getFlightCountDownView().getCountDownTimer();
        POEStatusEnum status = hvs.getPoeStatusEnum();

        POETileDTO tile = new POETileDTO(paxId, paxFirstName, paxLastName, docVo,
                hitCategory.getName(), flightCountdownTime, status.name());

        return tile;
    }
}
