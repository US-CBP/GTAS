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
import javax.transaction.Transactional;

import gov.gtas.model.*;
import gov.gtas.repository.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.enumtype.AuditActionType;
import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.enumtype.Status;
import gov.gtas.json.AuditActionData;
import gov.gtas.json.AuditActionTarget;
import gov.gtas.model.lookup.DispositionStatus;
import gov.gtas.services.dto.PassengersPageDto;
import gov.gtas.services.dto.PassengersRequestDto;
import gov.gtas.vo.passenger.CaseVo;
import gov.gtas.vo.passenger.DocumentVo;
import gov.gtas.vo.passenger.PassengerVo;
import javax.persistence.Query;

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
            PaxWatchlistLink link = (PaxWatchlistLink) objs[3];

            if (hit != null && f.getId() != hit.getFlight().getId()) {
                continue;
            }

            PassengerVo vo = new PassengerVo();
            BeanUtils.copyProperties(p, vo);

            
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
                String hitType = hit.getHitType();
                if (hitType.contains(HitTypeEnum.R.toString())) {
                    vo.setOnRuleHitList(true);
                }
                if (hitType.contains(HitTypeEnum.P.toString())) {
                    vo.setOnWatchList(true);
                }
                if (hitType.contains(HitTypeEnum.D.toString())) {
                    vo.setOnWatchListDoc(true);
                }
            }

            if (link != null) {
                vo.setOnWatchListLink(true);
            }

            // grab flight info
            vo.setFlightId(f.getId().toString());
            vo.setFlightNumber(f.getFlightNumber());
            vo.setFullFlightNumber(f.getFullFlightNumber());
            vo.setCarrier(f.getCarrier());
            vo.setEtd(f.getEtd());
            vo.setEta(f.getEta());
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
            vo.setFlightETADate(f.getEta());
            vo.setFlightETDDate(f.getEtd());
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
            passengerToUpdate.setAge(passenger.getAge());
            passengerToUpdate.setCitizenshipCountry(passenger.getCitizenshipCountry());
            passengerToUpdate.setDebarkation(passenger.getDebarkation());
            passengerToUpdate.setDebarkCountry(passenger.getDebarkCountry());
            passengerToUpdate.setDob(passenger.getDob());
            passengerToUpdate.setEmbarkation(passenger.getEmbarkation());
            passengerToUpdate.setEmbarkCountry(passenger.getEmbarkCountry());
            passengerToUpdate.setFirstName(passenger.getFirstName());
            //passengerToUpdate.setFlights(passenger.getFlights()); TODO: UNCALLED METHOD, CONSIDER REMOVAL
            passengerToUpdate.setGender(passenger.getGender());
            passengerToUpdate.setLastName(passenger.getLastName());
            passengerToUpdate.setMiddleName(passenger.getMiddleName());
            passengerToUpdate.setResidencyCountry(passenger.getResidencyCountry());
            passengerToUpdate.setDocuments(passenger.getDocuments());
            passengerToUpdate.setSuffix(passenger.getSuffix());
            passengerToUpdate.setTitle(passenger.getTitle());
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
        d.setFlight(f);
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

            actionData.addProperty("CitizenshipCountry", passenger.getCitizenshipCountry());
            actionData.addProperty("PassengerType", passenger.getPassengerType());
            //
            String message = "Disposition Status Change run on " + passenger.getCreatedAt();
            auditLogRepository.save(new AuditRecord(AuditActionType.DISPOSITION_STATUS_CHANGE, target.toString(),
                    Status.SUCCESS, message, actionData.toString(), loggedinUser, new Date()));

        } catch (Exception ex) {
            logger.warn(ex.getMessage());
        }
    }

    @Override
    public void createDisposition(HitsSummary hit) {
        Disposition d = new Disposition();
        Date date = new Date();
        d.setCreatedAt(date);
        d.setCreatedBy("SYSTEM");
        d.setComments("A new disposition has been created on " + date);
        d.setPassenger(hit.getPassenger());
        d.setFlight(hit.getFlight());
        DispositionStatus status = new DispositionStatus();
        status.setId(1L);
        d.setStatus(status);

        dispositionRepo.save(d);
    }

    @Override
    @Transactional
    public Passenger findById(Long id) {
        return passengerRespository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public List<Passenger> getPassengersByLastName(String lastName) {
        return passengerRespository.getPassengersByLastName(lastName);
    }

    @Override
    public void fillWithHitsInfo(PassengerVo vo, Long flightId, Long passengerId) {
        List<HitsSummary> hitsSummary = hitsSummaryRepository.findByFlightIdAndPassengerId(flightId, passengerId);
        if (!CollectionUtils.isEmpty(hitsSummary)) {
            for (HitsSummary hs : hitsSummary) {
                String hitType = hs.getHitType();
                if (hitType.contains(HitTypeEnum.R.toString())) {
                    vo.setOnRuleHitList(true);
                }
                if (hitType.contains(HitTypeEnum.P.toString())) {
                    vo.setOnWatchList(true);
                }
                if (hitType.contains(HitTypeEnum.D.toString())) {
                    vo.setOnWatchListDoc(true);
                }
            }
        }
    }

    @Override
    @Transactional
    public List<Flight> getTravelHistory(Long pId, String docNum, String docIssuCountry, Date docExpDate) {
       /* List<Passenger> paxL = passengerRespository.findByAttributes(pId, docNum, docIssuCountry, docExpDate);
        return paxL.stream().map(pax -> pax.getFlights()).flatMap(Set::stream).collect(Collectors.toList());*/
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
        //return
        List<Passenger> _tempPaxList = bookingDetailRepository.getBookingDetailsByPassengerIdTag(pId);
        //List<FlightVo> _tempBDFlightsList = new ArrayList<>();
        try {
            //stuff flights from Passenger
            List _tempbdList = _tempPaxList.stream().map(pax -> {
                Hibernate.initialize(pax.getBookingDetails());
                return pax.getBookingDetails();
            }).collect(Collectors.toList());
        } catch (Exception ex) {
                logger.error("Get booking detail history failed.", ex);
        }
        return _tempPaxList;
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
        public List<FlightPax> getFlightPaxByPassengerIdList(List<Long> passengerIdList)
        {
            String sqlStr = "SELECT fp FROM FlightPax fp JOIN fp.passenger WHERE fp.passenger.id IN :pidList";
            Query query = em.createQuery(sqlStr);
            query.setParameter("pidList", passengerIdList);
            List<FlightPax> flightPaxList = query.getResultList();
            return flightPaxList;
        }
        
        @Override
        public List<Passenger> getPaxByPaxIdList(List<Long> passengerIdList)
        {
            String sqlStr = "SELECT p FROM Passenger p WHERE p.id IN :pidList";
            Query query = em.createQuery(sqlStr);
            query.setParameter("pidList", passengerIdList);
            List<Passenger> passengerList = query.getResultList();
            return passengerList;           
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
