/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import gov.gtas.model.*;
import gov.gtas.repository.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.enumtype.AuditActionType;
import gov.gtas.enumtype.Status;
import gov.gtas.json.AuditActionData;
import gov.gtas.json.AuditActionTarget;
import gov.gtas.model.lookup.DispositionStatus;
import gov.gtas.services.dto.PassengersPageDto;
import gov.gtas.services.dto.PassengersRequestDto;
import gov.gtas.vo.passenger.CaseVo;
import gov.gtas.vo.passenger.DocumentVo;
import gov.gtas.vo.passenger.PassengerVo;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;

import static java.util.stream.Collectors.toSet;

/**
 * The Class PassengerServiceImpl.
 */
@Service
public class PassengerServiceImpl implements PassengerService {

    private static final Logger logger = LoggerFactory.getLogger(PassengerServiceImpl.class);

    @Resource
    private PassengerRepository passengerRespository;

    @Resource
    private HitsSummaryRepository hitsSummaryRepository;

    @Resource
    private DispositionStatusRepository dispositionStatusRepo;

    @Resource
    private DispositionRepository dispositionRepo;

    @Resource
    private SeatRepository seatRepository;

    @Autowired
    private AuditRecordRepository auditLogRepository;

    @Autowired
    private FlightRepository flightRespository;
    
    @Autowired
    private PnrRepository pnrRepository;

    @Autowired
    private BagRepository bagRespository;

    @Autowired
    private BookingDetailRepository bookingDetailRepository;
    
	@PersistenceContext
	private EntityManager em;

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    FlightPaxRepository flightPaxRepository;

    @Autowired
    AppConfigurationService appConfigurationService;

    @Override
    @Transactional
    public Passenger create(Passenger passenger) {
        return passengerRespository.save(passenger);
    }

    @Override
    @Transactional
    public PassengersPageDto getPassengersByCriteria(Long flightId, PassengersRequestDto request) {
        List<PassengerVo> rv = new ArrayList<>();
        Pair<Long, List<Object[]>> tuple = passengerRespository.findByCriteria(flightId, request);
        int count = 0;
        for (Object[] objs : tuple.getRight()) {
            if (count == request.getPageSize()) {
                break;
            }

            Passenger p = (Passenger) objs[0];
            Flight f = (Flight) objs[1];
            HitsSummary hit = (HitsSummary) objs[2];
        //    PaxWatchlistLink link = (PaxWatchlistLink) objs[3];

            if (hit != null && !f.getId().equals(hit.getFlightId())) {
                continue;
            }

            PassengerVo vo = new PassengerVo();
            BeanUtils.copyProperties(p, vo);
            BeanUtils.copyProperties(p.getPassengerDetails(), vo);
            BeanUtils.copyProperties(p.getPassengerTripDetails(), vo);
            vo.setId(p.getId());


            
            Iterator<Document> docIter = p.getDocuments().iterator();
    		while (docIter.hasNext()) {
    			Document d = docIter.next();
    			DocumentVo docVo = new DocumentVo();
    			docVo.setDocumentNumber(d.getDocumentNumber());
    			docVo.setDocumentType(d.getDocumentType());
    			docVo.setIssuanceCountry(d.getIssuanceCountry());
    			docVo.setExpirationDate(d.getExpirationDate());
    			docVo.setIssuanceDate(d.getIssuanceDate());
    			vo.addDocument(docVo);
    		}
            
            List<Seat> seatList = seatRepository.findByFlightIdAndPassengerId(f.getId(), p.getId());
            if (CollectionUtils.isNotEmpty(seatList)) {
                List<String> seats = seatList.stream().map(seat -> seat.getNumber()).distinct()
                        .collect(Collectors.toList());
                if (seats.size() == 1) {
                    vo.setSeat(seats.get(0));
                }
            }

            if (hit != null) {
                for (HitDetail hd : hit.getHitdetails()) {

                    if ("GH".equalsIgnoreCase(hd.getHitType())) {
                        vo.setOnRuleHitList(true);
                    }
                    if ("R".equalsIgnoreCase(hd.getHitType())) {
                        vo.setOnRuleHitList(true);
                    }
                    if ("P".equalsIgnoreCase(hd.getHitType())) {
                        vo.setOnWatchList(true);
                    }
                    if ("D".equalsIgnoreCase(hd.getHitType())) {
                        vo.setOnWatchListDoc(true);
                    }
                }
            }
            if (p.getPassengerWLTimestamp() != null
                    && p.getPassengerWLTimestamp().getHitCount() != null
                    && p.getPassengerWLTimestamp().getHitCount() > 0) {
                    vo.setOnWatchListLink(true);
            }

            // grab flight info
            vo.setFlightId(f.getId().toString());
            vo.setFlightNumber(f.getFlightNumber());
            vo.setFullFlightNumber(f.getFullFlightNumber());
            vo.setCarrier(f.getCarrier());
            vo.setEtd(f.getMutableFlightDetails().getEtd());
            vo.setEta(f.getMutableFlightDetails().getEta());
            rv.add(vo);
            count++;
        }

        return new PassengersPageDto(rv, tuple.getLeft());
       
    }

    @Override
    @Transactional
    public List<CaseVo> getAllDispositions() {
        List<CaseVo> rv = new ArrayList<>();
        List<Object[]> cases = passengerRespository.findAllDispositions();
        for (Object[] objs : cases) {
            CaseVo vo = new CaseVo();
            rv.add(vo);
            Long passengerId = ((BigInteger) objs[0]).longValue();
            vo.setPassengerId(passengerId);
            Long flightId = ((BigInteger) objs[1]).longValue();
            vo.setFlightId(flightId);
            vo.setFirstName((String) objs[2]);
            vo.setLastName((String) objs[3]);
            vo.setMiddleName((String) objs[4]);
            vo.setFlightNumber((String) objs[5]);

            Flight f = flightRespository.findById(flightId).orElse(null);
            vo.setFlightETADate(f.getMutableFlightDetails().getEta());
            vo.setFlightETDDate(f.getMutableFlightDetails().getEtd());
            vo.setFlightDirection(f.getDirection());

            Timestamp ts = (Timestamp) objs[6];
            String datetime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(ts);
            vo.setCreateDate(datetime);
            vo.setStatus((String) objs[7]);
            for (HitsSummary h : hitsSummaryRepository.findByFlightIdAndPassengerId(flightId, passengerId)) {
                if (vo.getHitType() != null) {
                    vo.setHitType(vo.getHitType() + h.getHitType());
                } else {
                    vo.setHitType(h.getHitType());
                }
            }
        }
        return rv;
    }

    @Override
    @Transactional
    public Passenger update(Passenger passenger) {
        Passenger passengerToUpdate = this.findById(passenger.getId());
        if (passengerToUpdate != null) {
            passengerToUpdate.getPassengerDetails().setAge(passenger.getPassengerDetails().getAge());
            passengerToUpdate.getPassengerDetails().setNationality(passenger.getPassengerDetails().getNationality());
            passengerToUpdate.getPassengerTripDetails().setDebarkation(passenger.getPassengerTripDetails().getDebarkation());
            passengerToUpdate.getPassengerTripDetails().setDebarkCountry(passenger.getPassengerTripDetails().getDebarkCountry());
            passengerToUpdate.getPassengerDetails().setDob(passenger.getPassengerDetails().getDob());
            passengerToUpdate.getPassengerTripDetails().setEmbarkation(passenger.getPassengerTripDetails().getEmbarkation());
            passengerToUpdate.getPassengerTripDetails().setEmbarkCountry(passenger.getPassengerTripDetails().getEmbarkCountry());
            passengerToUpdate.getPassengerDetails().setFirstName(passenger.getPassengerDetails().getFirstName());
            //passengerToUpdate.setFlights(passenger.getFlights()); TODO: UNCALLED METHOD, CONSIDER REMOVAL
            passengerToUpdate.getPassengerDetails().setGender(passenger.getPassengerDetails().getGender());
            passengerToUpdate.getPassengerDetails().setLastName(passenger.getPassengerDetails().getLastName());
            passengerToUpdate.getPassengerDetails().setMiddleName(passenger.getPassengerDetails().getMiddleName());
            passengerToUpdate.getPassengerDetails().setResidencyCountry(passenger.getPassengerDetails().getResidencyCountry());
            passengerToUpdate.setDocuments(passenger.getDocuments());
            passengerToUpdate.getPassengerDetails().setSuffix(passenger.getPassengerDetails().getSuffix());
            passengerToUpdate.getPassengerDetails().setTitle(passenger.getPassengerDetails().getTitle());
        }
        return passengerToUpdate;
    }

    @Override
    @Transactional
    public List<Disposition> getPassengerDispositionHistory(Long passengerId, Long flightId) {
        return passengerRespository.getPassengerDispositionHistory(passengerId, flightId);
    }

    @Override
    public List<DispositionStatus> getDispositionStatuses() {
        Iterable<DispositionStatus> i = dispositionStatusRepo.findAll();
        if (i != null) {
            return IteratorUtils.toList(i.iterator());
        }
        return new ArrayList<>();
    }

    @Transactional
    @Override
    public void createOrEditDispositionStatus(DispositionStatus ds) {
        dispositionStatusRepo.save(ds);
    }

    @Transactional
    @Override
    public void deleteDispositionStatus(DispositionStatus ds) {
        dispositionStatusRepo.delete(ds);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gov.gtas.services.PassengerService#createDisposition(gov.gtas.services
     * .DispositionData)
     */
    @Transactional
    @Override
    public void createDisposition(DispositionData disposition, User loggedinUser) {
        Disposition d = new Disposition();
        d.setCreatedAt(new Date());
        d.setCreatedBy(disposition.getUser());
        Flight f = new Flight();
        f.setId(disposition.getFlightId());
        d.setFlightId(f.getId());
        Passenger p = new Passenger();
        p.setId(disposition.getPassengerId());
        d.setPassenger(p);
        d.setComments(disposition.getComments());
        DispositionStatus status = new DispositionStatus();
        status.setId(disposition.getStatusId());
        d.setStatus(status);

        dispositionRepo.save(d);
        writeAuditLogForDisposition(disposition.getPassengerId(), loggedinUser);
    }

    /**
     * Write audit log for disposition.
     */
    private void writeAuditLogForDisposition(Long pId, User loggedinUser) {
        Passenger passenger = findById(pId);
        try {
            AuditActionTarget target = new AuditActionTarget(passenger);
            AuditActionData actionData = new AuditActionData();

            actionData.addProperty("Nationality", passenger.getPassengerDetails().getNationality());
            actionData.addProperty("PassengerType", passenger.getPassengerDetails().getPassengerType());
            //
            String message = "Disposition Status Change run on " + passenger.getCreatedAt();
            auditLogRepository.save(new AuditRecord(AuditActionType.DISPOSITION_STATUS_CHANGE, target.toString(),
                    Status.SUCCESS, message, actionData.toString(), loggedinUser, new Date()));

        } catch (Exception ex) {
            logger.warn(ex.getMessage());
        }
    }

    @Override
    public void createDisposition(List<HitsSummary> hitsList) {

        List<Disposition> dispositionsList = new ArrayList<>();
        Set<Long> hitsIds = hitsList.stream().map(HitsSummary::getPaxId).collect(toSet());
        if (!hitsIds.isEmpty()) {
            Set<Long> passengerIdsWithDisposition = dispositionRepo.getExisitngPaxIds(hitsIds);
            for (HitsSummary hit : hitsList) {
                if (!passengerIdsWithDisposition.contains(hit.getPaxId())) {
                    Disposition d = createDispositionFromHitsSummary(hit);
                    dispositionsList.add(d);
                }
            }
            if (!dispositionsList.isEmpty()) {
                dispositionRepo.saveAll(dispositionsList);
            }
        }
    }



    @Override
    public void createDisposition(HitsSummary hit) {
        Disposition d = createDispositionFromHitsSummary(hit);
        dispositionRepo.save(d);
    }

    private Disposition createDispositionFromHitsSummary(HitsSummary hit) {
        Disposition d = new Disposition();
        Date date = new Date();
        d.setCreatedAt(date);
        d.setCreatedBy("SYSTEM");
        d.setComments("A new disposition has been created on " + date);
        d.setPaxId(hit.getPaxId());
        DispositionStatus status = new DispositionStatus();
        status.setId(1L);
        d.setStatus(status);
        return d;
    }

    @Override
    @Transactional
    public Passenger findById(Long id) {
        return passengerRespository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Passenger findByIdWithFlightPaxAndDocuments(Long paxId){
        return passengerRepository.findByIdWithFlightPaxAndDocuments(paxId);
    }

 /*   @Override
    @Transactional
    public List<Passenger> getPassengersByLastName(String lastName) {
        return passengerRespository.getPassengersByLastName(lastName);
    }
*/
    @Override
    public void fillWithHitsInfo(PassengerVo vo, Long flightId, Long passengerId) {
        List<HitsSummary> hitsSummary = hitsSummaryRepository.findByFlightIdAndPassengerId(flightId, passengerId);
        if (!CollectionUtils.isEmpty(hitsSummary)) {
            boolean isRuleHit = false;
            boolean isWatchlistHit = false;
            for (HitsSummary hs : hitsSummary) {
                isRuleHit = hs.getRuleHitCount() != null && hs.getRuleHitCount() > 0;
                isWatchlistHit =  hs.getWatchListHitCount() != null && hs.getWatchListHitCount() > 0;
            }
            vo.setOnRuleHitList(isRuleHit);
            vo.setOnWatchList(isWatchlistHit);
        }
    }

    @Override
    @Transactional
    public List<Flight> getTravelHistory(Long pId, String docNum, String docIssuCountry, Date docExpDate) {
       /* List<Passenger> paxL = passengerRespository.findByAttributes(pId, docNum, docIssuCountry, docExpDate);
        return paxL.stream().map(pax -> pax.getFlight()).flatMap(Set::stream).collect(Collectors.toList());*/
    	return null;
    }
    
    @Override
    @Transactional
    public List<Flight> getTravelHistoryByItinerary(Long pnrId, String pnrRef) {
    	return flightRespository.getTravelHistoryByItinerary(pnrId, pnrRef);
    }
    @Override
    @Transactional
    public List<Flight> getTravelHistoryNotByItinerary(Long paxId, Long pnrId, String pnrRef) {
    	return flightRespository.getTravelHistoryNotByItinerary(paxId, pnrId, pnrRef);
    }

    @Override
    @Transactional
    public List<Passenger> getBookingDetailHistoryByPaxID(Long pId) {
        return bookingDetailRepository.getBookingDetailsByPassengerIdTag(pId);
    }

    @SuppressWarnings("unchecked")
	@Override
	public Set<Flight> getAllFlights(Long id) {
		String sqlStr = "SELECT f.* FROM flight_passenger fp JOIN flight f ON (fp.flight_id = f.id) WHERE fp.passenger_id="+id+"";
		List<Flight> resultList = em.createNativeQuery(sqlStr, Flight.class).getResultList();
		Set<Flight> flightSet = null;
		if(resultList != null){
			flightSet = new HashSet<Flight>(resultList);
		}
		return flightSet;
	}


        @Override
        public Set<FlightPax> findFlightPaxFromPassengerIds(List<Long> passengerIdList)
        {
            return flightPaxRepository.findFlightFromPassIdList(passengerIdList);
        }


        @Override
        public List<Passenger> getPaxByPaxIdList(List<Long> passengerIdList)
        {
            return passengerRepository.getPassengersById(passengerIdList);
        }
        
                
        @Override
        public List<Flight> getFlightsByIdList(List<Long> flightIdList)
        {
            String sqlStr = "SELECT f FROM Flight f WHERE f.id IN :fidList";
            Query query = em.createQuery(sqlStr);
            query.setParameter("fidList", flightIdList);
            List<Flight> flightList = query.getResultList();
            return flightList;            
        }

	@Override
	public void setAllFlights(Set<Flight> flights, Long id) {
		String sqlStr = "";
		for(Flight f: flights){
			sqlStr += "INSERT INTO flight_passenger(flight_id, passenger_id) VALUES("+f.getId()+","+id+");";
		}
		em.createNativeQuery(sqlStr).executeUpdate();
	}

	@Override
	public void SetSingleFlight(Flight f, Long id) {
		String sqlStr = "INSERT INTO flight_passenger(flight_id, passenger_id) VALUES("+f.getId()+","+id+");";
		em.createNativeQuery(sqlStr).executeUpdate();
	}
}
