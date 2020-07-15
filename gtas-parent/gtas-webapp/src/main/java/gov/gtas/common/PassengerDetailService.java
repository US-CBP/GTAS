package gov.gtas.common;

import gov.gtas.aop.annotations.PassengerAuditFirstArgPaxIdAsString;
import gov.gtas.enumtype.MessageType;
import gov.gtas.model.ApisMessage;
import gov.gtas.model.Bag;
import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerDetails;
import gov.gtas.model.Pnr;
import gov.gtas.model.Seat;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.repository.AuditRecordRepository;
import gov.gtas.repository.BagRepository;
import gov.gtas.services.ApisService;
import gov.gtas.services.FlightService;
import gov.gtas.services.PassengerService;
import gov.gtas.services.PnrService;
import gov.gtas.services.SeatService;
import gov.gtas.services.search.FlightPassengerVo;
import gov.gtas.services.security.UserService;
import gov.gtas.util.PaxDetailVoUtil;
import gov.gtas.vo.passenger.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Controller
public class PassengerDetailService {

    @Autowired
    private ApisService apisService;

    @Autowired
    private PassengerService pService;

    @Autowired
    private FlightService fService;

    @Autowired
    private PnrService pnrService;

    @Resource
    private BagRepository bagRepository;

    @Resource
    private ApisMessageRepository apisMessageRepository;

    @Autowired
    private SeatService seatService;

    @Autowired
    UserService userService;

    @Autowired
    AuditRecordRepository auditRecordRepository;

    @PassengerAuditFirstArgPaxIdAsString
    public PassengerVo generatePassengerVO(String paxId, String flightId) {
        Passenger passenger = pService.findByIdWithFlightAndDocumentsAndMessageDetails(Long.valueOf(paxId));
        Flight flight = fService.findById(Long.parseLong(flightId));

        return populatePassangerVo(passenger, flight);
    }


    private PassengerVo populatePassangerVo(Passenger passenger, Flight flight) {
        PassengerVo vo = new PassengerVo();
        vo.setFlightNumber(flight.getFlightNumber());
        vo.setCarrier(flight.getCarrier());
        vo.setFlightOrigin(flight.getOrigin());
        vo.setFlightDestination(flight.getDestination());
        vo.setEta(flight.getMutableFlightDetails().getEta());
        vo.setEtd(flight.getMutableFlightDetails().getEtd());
        vo.setFlightOrigin(flight.getOrigin());
        vo.setFlightDestination(flight.getDestination());
        vo.setFlightId(flight.getId().toString());
        vo.setFlightIdTag(flight.getIdTag());

        String seatNumber = seatService.findSeatNumberByFlightIdAndPassengerId(flight.getId(), passenger.getId());
        vo.setSeat(seatNumber);

        vo.setPaxId(String.valueOf(passenger.getId()));
        if (passenger.getPassengerIDTag() != null) {
            vo.setPaxIdTag(passenger.getPassengerIDTag().getIdTag());
        }

        PassengerDetails passengerDetails = PaxDetailVoUtil.filterOutMaskedAPISOrPnr(passenger);
        PaxDetailVoUtil.populatePassengerVoWithPassengerDetails(vo, passengerDetails, passenger);

        for (Document d : passenger.getDocuments()) {
            DocumentVo docVo = new DocumentVo();
            docVo.setDocumentNumber(d.getDocumentNumber());
            docVo.setDocumentType(d.getDocumentType());
            docVo.setIssuanceCountry(d.getIssuanceCountry());
            docVo.setExpirationDate(d.getExpirationDate());
            docVo.setIssuanceDate(d.getIssuanceDate());
            docVo.setMessageType(d.getMessageType() == null ? "" : d.getMessageType().toString());
            if (passenger.getDataRetentionStatus().requiresDeletedAPIS() && d.getMessageType() == MessageType.APIS) {
                docVo.deletePII();
            } else if (passenger.getDataRetentionStatus().requiresDeletedPNR() && d.getMessageType() == MessageType.PNR) {
                docVo.deletePII();
            } else if (passenger.getDataRetentionStatus().requiresMaskedAPIS() && d.getMessageType() == MessageType.APIS) {
                docVo.maskPII();
            } else if (passenger.getDataRetentionStatus().requiresMaskedPNR() && d.getMessageType() == MessageType.PNR) {
                docVo.maskPII();
            }
            vo.addDocument(docVo);
        }

        // Gather PNR Details
        List<Pnr> pnrList = pnrService.findPnrByPassengerIdAndFlightId(passenger.getId(), flight.getId());
        List<Bag> bagList = bagRepository.findFromFlightAndPassenger(flight.getId(), passenger.getId());

        if (!pnrList.isEmpty()) {
            Pnr source = getLatestPnrFromList(pnrList);
            vo.setPnrVo(PaxDetailVoUtil.mapPnrToPnrVo(source));
            List<Long> passengerIds = source.getPassengers().stream().map(Passenger::getId).collect(toList());
            Set<Bag> pnrBag = bagRepository.getBagsByPassengerIds(passengerIds);
            Set<BagVo> bagVos = BagVo.fromBags(pnrBag);
            BagSummaryVo bagSummaryVo = BagSummaryVo.createFromBagVos(bagVos);
            PnrVo tempVo = vo.getPnrVo();
            tempVo.setBagSummaryVo(bagSummaryVo);
            // Assign seat for every passenger on pnr
            for (Passenger p : source.getPassengers()) {
                for (Seat s : p.getSeatAssignments()) {
                    // exclude APIS seat data
                    if (!s.getApis()) {
                        SeatVo seatVo = new SeatVo();
                        seatVo.setFirstName(p.getPassengerDetails().getFirstName());
                        seatVo.setLastName(p.getPassengerDetails().getLastName());
                        seatVo.setNumber(s.getNumber());
                        seatVo.setApis(s.getApis());
                        seatVo.setFlightNumber(flight.getFullFlightNumber());
                        if (p.getDataRetentionStatus().requiresDeletedPNR()) {
                            seatVo.deletePII();
                        } else if (p.getDataRetentionStatus().requiresMaskedPNR()) {
                            seatVo.maskPII();
                        }
                        tempVo.addSeat(seatVo);
                    }
                }
            }
            BagStatisticCalculator bagStatisticCalculator = new BagStatisticCalculator(new HashSet<>(bagList)).invoke("PNR");
            tempVo.setBagCount(bagStatisticCalculator.getBagCount());
            tempVo.setBaggageWeight(bagStatisticCalculator.getBagWeight());
            tempVo.setTotal_bag_count(bagStatisticCalculator.getBagCount());
            PaxDetailVoUtil.parseRawMessageToSegmentList(tempVo);
            vo.setPnrVo(tempVo);
        }

        List<ApisMessage> apisList = apisMessageRepository.findByFlightIdAndPassengerId(flight.getId(),
                passenger.getId());
        if (!apisList.isEmpty()) {
            ApisMessage apis = apisList.get(0);
            ApisMessageVo apisVo = new ApisMessageVo();
            apisVo.setApisRecordExists(true);
            apisVo.setTransmissionDate(apis.getEdifactMessage().getTransmissionDate());

            Passenger loadedPassenger = apisMessageRepository
                    .findPaxByFlightIdandPassengerId(flight.getId(), passenger.getId());
            String refNumber = loadedPassenger.getPassengerTripDetails().getReservationReferenceNumber();
            BagStatisticCalculator bagStatisticCalculator = new BagStatisticCalculator(loadedPassenger).invoke("APIS");
            int bagCount = bagStatisticCalculator.getBagCount();
            double bagWeight = bagStatisticCalculator.getBagWeight();
            apisVo.setBagCount(bagCount);
            apisVo.setBagWeight(bagWeight);

            if (refNumber != null) {
                List<FlightPassengerVo> fpList = apisService.generateFlightPassengerList(refNumber,
                        flight.getId());
                apisVo.getFlightpaxs().addAll(fpList);
            }

            for (Bag b : bagList) {
                if (b.getData_source().equalsIgnoreCase("apis")) {
                    BagVo bagVo = new BagVo();
                    bagVo.setBagId(b.getBagId());
                    bagVo.setData_source(b.getData_source());
                    bagVo.setDestination(b.getDestinationAirport());
                    apisVo.addBag(bagVo);
                }
            }
//			ToDo: Add APIS phones to rule engine and loader.
//			Iterator<Phone> phoneIter = apis.getPhones().iterator();
//			while (phoneIter.hasNext()) {
//				Phone p = phoneIter.next();
//				PhoneVo pVo = new PhoneVo();
//				pVo.setNumber(p.getNumber());
//				apisVo.addPhoneNumber(pVo);
//			}
            vo.setApisMessageVo(apisVo);
        }

        if (isMasked(passenger) || isDeleted(passenger)) {
            vo.setDisableLinks(true);
        }


        return vo;
    }

    private boolean isMasked(Passenger t) {
        return !((t.getDataRetentionStatus().isHasPnrMessage() && !t.getDataRetentionStatus().requiresMaskedPNR()) ||
                (t.getDataRetentionStatus().isHasApisMessage() && !t.getDataRetentionStatus().requiresMaskedAPIS()));
    }

    private boolean isDeleted(Passenger t) {
        return !((t.getDataRetentionStatus().isHasPnrMessage() && !t.getDataRetentionStatus().requiresDeletedPNR()) ||
                (t.getDataRetentionStatus().isHasApisMessage() && !t.getDataRetentionStatus().requiresDeletedAPIS()));
    }

    private Pnr getLatestPnrFromList(List<Pnr> pnrList) {
        Pnr latest = pnrList.get(0);
        for (Pnr p : pnrList) {
            if (p.getId() > latest.getId()) {
                latest = p;
            }
        }
        return latest;
    }


//	private boolean isRelatedToTifPassenger(String tifSegment, BagVo b) {
//		return b.getData_source().equalsIgnoreCase("PNR") && b.getPassFirstName() != null
//				&& tifSegment.contains(b.getPassFirstName()) && b.getPassLastName() != null
//				&& tifSegment.contains(b.getPassLastName());
//	}
}
