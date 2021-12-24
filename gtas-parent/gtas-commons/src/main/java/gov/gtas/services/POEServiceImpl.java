/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */
package gov.gtas.services;

import gov.gtas.enumtype.LookoutStatusEnum;
import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.Document;
import gov.gtas.model.HitViewStatus;
import gov.gtas.model.LookoutLane;
import gov.gtas.model.Passenger;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.repository.HitViewStatusRepository;
import gov.gtas.repository.POELaneRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.services.dto.LookoutLaneDTO;
import gov.gtas.services.dto.LookoutStatusDTO;
import gov.gtas.services.dto.POETileServiceRequest;
import gov.gtas.services.security.UserService;
import gov.gtas.vo.passenger.DocumentVo;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class POEServiceImpl implements POEService {
	
	private static final Logger logger = LoggerFactory.getLogger(POEServiceImpl.class);

    @Resource
    private POELaneRepository poeLaneRepository;

    @Resource
    private HitViewStatusRepository hitViewStatusRepository;

    @Resource
    private PassengerRepository passengerRepository;

    @Autowired
    private UserService userService;

    @Override
    public Set<LookoutStatusDTO> getAllTiles(String userId, POETileServiceRequest request) {
	   long start = System.nanoTime();

		Set<LookoutStatusDTO> tiles = new HashSet<>();
		Set<HitViewStatus> hvs = new HashSet<>();
		if ("All".equalsIgnoreCase(request.getPoeAirport()) 
				|| StringUtils.isBlank(request.getPoeAirport())) {
			hvs = hitViewStatusRepository.findAllWithNotClosedAndWithinRange(
					userService.fetchUserGroups(userId), request.getEtaStart(), request.getEtaEnd());
			logger.info("Tile in... {} m/s.", (System.nanoTime() - start) / 1000000);		
		} else {
			hvs = hitViewStatusRepository.findAllWithNotClosedAndWithinRangeWithAirport(
					userService.fetchUserGroups(userId), request.getEtaStart(), request.getEtaEnd(), request.getPoeAirport());
			logger.info("Tile with airport filter in... {} m/s.", (System.nanoTime() - start) / 1000000);
		}

	   start = System.nanoTime();
       Set<HitViewStatus> hvsToBeUpdated = new HashSet<HitViewStatus>();
       for(HitViewStatus hv : hvs ){
           if(lookoutIsMissedOrInactiveAndUpdate(hv)){
               hvsToBeUpdated.add(hv);
           }
           tiles.add(createLookoutTileDTO(hv)); //TODO: distinct passenger on query with a lot more configuration in the service layer for duplicates handling
       }
       if(!hvsToBeUpdated.isEmpty()){
           hitViewStatusRepository.saveAll(hvsToBeUpdated);
       }
	   logger.info("Rest in... {} m/s.", (System.nanoTime() - start) / 1000000);

        return tiles;
    }

    @Override
    public List<LookoutLane> getAllLanes() {
        List<LookoutLane> list = StreamSupport
                .stream(poeLaneRepository.getNonArchivedLanes().spliterator(), false)
                .collect(Collectors.toList());
        return list;
    }

    @Override
    @Transactional
    public JsonServiceResponse updateStatus(LookoutStatusDTO poeTileDTO) {
        Passenger p = passengerRepository.findById(poeTileDTO.getPaxId()).orElseThrow(RuntimeException::new);
        Set<HitViewStatus> hvs = hitViewStatusRepository.findAllByPassenger(p);
        for(HitViewStatus hv : hvs){
            hv.setLookoutStatusEnum(LookoutStatusEnum.valueOf(poeTileDTO.getStatus()));
        }
        hitViewStatusRepository.saveAll(hvs);
        return new JsonServiceResponse(Status.SUCCESS, "success"); //TODO: More robust
    }

    @Override
    public JsonServiceResponse createNewLane(LookoutLaneDTO laneDTO) {
        try {
            poeLaneRepository.save(createLookoutLane(laneDTO));
        } catch (Exception e){
            return new JsonServiceResponse(Status.FAILURE, "failure"); //TODO: More robust
        }
        return new JsonServiceResponse(Status.SUCCESS, "success");
    }

    @Override
    public JsonServiceResponse updateLane(LookoutLaneDTO laneDTO) {
        LookoutLane lane = poeLaneRepository.findById(laneDTO.getId()).orElse(null);
        if(lane == null){
            return new JsonServiceResponse(Status.FAILURE, "failure");
        }
        lane.setOrd(laneDTO.getOrd());
        lane.setStatus(laneDTO.getStatus());  //Dangerous to change status on the fly
        lane.setDisplayName(laneDTO.getDisplayName());

        poeLaneRepository.save(lane);

        return new JsonServiceResponse(Status.SUCCESS, "success");
    }

    @Override
    public JsonServiceResponse deleteLane(String laneId) {
        LookoutLane lane = poeLaneRepository.findById(Long.parseLong(laneId)).orElse(null);
        if(lane == null){
            return new JsonServiceResponse(Status.FAILURE, "failure");
        }
        lane.setArchived(true); //Lanes auto set to archived for now until further instructions on what to do with existing tiles in those lanes
        poeLaneRepository.save(lane);

        return new JsonServiceResponse(Status.SUCCESS, "success");
    }


    private LookoutLane createLookoutLane(LookoutLaneDTO lookoutLaneDTO){
        LookoutLane lane = new LookoutLane();
        lane.setStatus(lookoutLaneDTO.getStatus());
        lane.setDisplayName(lookoutLaneDTO.getDisplayName());
        lane.setOrd(lookoutLaneDTO.getOrd());
        lane.setArchived(false);

        return lane;
    }

    private LookoutStatusDTO createLookoutTileDTO(HitViewStatus hvs){
        Long paxId = hvs.getPassenger().getId();
        Long flightId = hvs.getPassenger().getFlight().getId();
        String flightNumber = hvs.getPassenger().getFlight().getFullFlightNumber();
        String paxFirstName = hvs.getPassenger().getPassengerDetails().getFirstName();
        String paxLastName = hvs.getPassenger().getPassengerDetails().getLastName();
        Set<Document> documents = hvs.getPassenger().getDocuments();
        DocumentVo docVo = new DocumentVo();
        docVo = docVo.fromDocument(documents.iterator().next()); //TODO: first doc only for now
        HitCategory hitCategory = hvs.getHitDetail().getHitMaker().getHitCategory();
        Date flightCountdownTime = hvs.getPassenger().getFlight().getFlightCountDownView().getCountDownTimer();
        String direction = hvs.getPassenger().getFlight().getDirection();
        boolean isApis = false;
        if(hvs.getPassenger().getPnrs().isEmpty()) { //if no PNRs, must be APIS information only
            isApis = true;
        }
        LookoutStatusEnum status = hvs.getLookoutStatusEnum();

        LookoutStatusDTO tile = new LookoutStatusDTO(paxId, flightId, flightNumber, paxFirstName, paxLastName, docVo,
                hitCategory.getName(), flightCountdownTime, status.name(), direction, isApis);

        return tile;
    }

    public boolean lookoutIsMissedOrInactiveAndUpdate(HitViewStatus hvs){

        DateTime inactiveWindowVariable = new DateTime().minusDays(1); //24 hour timer
        DateTime fullyDemoteVariable = new DateTime().minusWeeks(1); //1 week timer
        DateTime missedFlightBufferVariable = new DateTime().minusHours(12); //12 hour buffer on missed flights

        if(!hvs.getLookoutStatusEnum().name().equals(LookoutStatusEnum.INACTIVE.name())
                && !hvs.getLookoutStatusEnum().name().equals(LookoutStatusEnum.MISSED.name())
                && !hvs.getLookoutStatusEnum().name().equals(LookoutStatusEnum.NOTPROMOTED.name())
                && !hvs.getLookoutStatusEnum().name().equals(LookoutStatusEnum.DEMOTED.name())
                && !hvs.getLookoutStatusEnum().name().equals(LookoutStatusEnum.POSITIVE.name())
                && !hvs.getLookoutStatusEnum().name().equals(LookoutStatusEnum.NEGATIVE.name())
                && !hvs.getLookoutStatusEnum().name().equals(LookoutStatusEnum.DIDNOTBOARD.name())){
            Date pastDue = inactiveWindowVariable.toDate();
            Date alreadyLandedOrLeft = missedFlightBufferVariable.toDate();

            if (hvs.getUpdatedAt() != null && hvs.getUpdatedAt().before(pastDue)) {
                hvs.setLookoutStatusEnum(LookoutStatusEnum.INACTIVE);
                return true;
            } else if (hvs.getUpdatedAt() == null && hvs.getCreatedAt().before(pastDue)){ //Stagnant creation
                hvs.setLookoutStatusEnum(LookoutStatusEnum.INACTIVE);
                return true;
            }else if (hvs.getPassenger().getFlight().getFlightCountDownView().getCountDownTimer().before(alreadyLandedOrLeft)
                    && !updatedAfterDepartureOrArrivalTime(hvs)) {
                hvs.setLookoutStatusEnum(LookoutStatusEnum.MISSED);
                return true;
            }
        } else if(hvs.getLookoutStatusEnum().name().equals(LookoutStatusEnum.INACTIVE.name())){ //If after a time inactive, demote
            Date demoteDate = fullyDemoteVariable.toDate();
            if(hvs.getUpdatedAt() != null && hvs.getUpdatedAt().before(demoteDate)){
                hvs.setLookoutStatusEnum(LookoutStatusEnum.DEMOTED);
            }
            return true;
        }
        return false;
    }

    //This will allow us to not reset anything recently updated erroneously to Missed
    private boolean updatedAfterDepartureOrArrivalTime(HitViewStatus hvs){
        if(hvs.getUpdatedAt() != null ) {
            if (hvs.getPassenger().getFlight().getDirection().equals("I")) {
                if (hvs.getPassenger().getFlight().getMutableFlightDetails().getEta().before(hvs.getUpdatedAt())) {
                    return true;
                }
            } else if (hvs.getPassenger().getFlight().getDirection().equals("O")) {
                if (hvs.getPassenger().getFlight().getMutableFlightDetails().getEtd().before(hvs.getUpdatedAt())) {
                    return true;
                }
            }
        }
        return false;
    }
}