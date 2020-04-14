package gov.gtas.services;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gov.gtas.enumtype.HitSeverityEnum;
import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.HitDetail;
import gov.gtas.model.HitMaker;
import gov.gtas.model.HitViewStatus;
import gov.gtas.model.Passenger;
import gov.gtas.model.Pnr;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.services.dto.PassengerNoteSetDto;
import gov.gtas.services.dto.PaxDetailPdfDocRequest;
import gov.gtas.services.dto.PaxDetailPdfDocResponse;
import gov.gtas.util.PaxDetailVoUtil;
import gov.gtas.vo.HitDetailVo;
import gov.gtas.vo.passenger.DocumentVo;
import gov.gtas.vo.passenger.FlightVoForFlightHistory;
import gov.gtas.vo.passenger.PassengerVo;

@Service
public class EventReportServiceImpl implements EventReportService {

	private static final Logger logger = LoggerFactory.getLogger(EventReportServiceImpl.class);

	private FlightService flightService;

	private PnrService pnrService;

	private PassengerService passengerService;

	private HitDetailService hitDetailService;

	private PassengerNoteService passengerNoteService;

	private EventReportPdfService<PaxDetailPdfDocRequest, PaxDetailPdfDocResponse> passengerEventReportService;

	@Resource
	private ApisMessageRepository apisMessageRepository;

	public EventReportServiceImpl(FlightService flightService, PnrService pnrService,
			PassengerService passengerService, HitDetailService hitDetailService,
			EventReportPdfService<PaxDetailPdfDocRequest, PaxDetailPdfDocResponse> passengerEventReportService,
			PassengerNoteService passengerNoteService) {
		this.flightService = flightService;
		this.pnrService = pnrService;
		this.passengerService = passengerService;
		this.passengerEventReportService = passengerEventReportService;
		this.hitDetailService = hitDetailService;
		this.passengerNoteService = passengerNoteService;

	}

	public PaxDetailPdfDocResponse createPassengerEventReport(Long paxId, Long flightId) {

		PaxDetailPdfDocRequest paxDetailPdfDocRequest = new PaxDetailPdfDocRequest();
		PaxDetailPdfDocResponse paxDetailPdfDocResponse = new PaxDetailPdfDocResponse();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
		PassengerVo passengerVo = new PassengerVo();
		Flight flight = flightService.findById(flightId);

		Passenger passenger = passengerService.findByIdWithFlightAndDocuments(paxId);

		if (passenger != null && flight != null) {
			if (flight.getId().equals(flightId)) {
				passengerVo.setFlightNumber(flight.getFlightNumber());
				passengerVo.setCarrier(flight.getCarrier());
				passengerVo.setFlightOrigin(flight.getOrigin());
				passengerVo.setFlightDestination(flight.getDestination());
				passengerVo.setFlightETA(formatter.format(flight.getMutableFlightDetails().getEta()));
				passengerVo.setFlightETD(formatter.format(flight.getMutableFlightDetails().getEtd()));
				passengerVo.setFlightOrigin(flight.getOrigin());
				passengerVo.setFlightDestination(flight.getDestination());
				passengerVo.setFlightId(flight.getId().toString());
				passengerVo.setFlightIdTag(flight.getIdTag());
			}

			passengerVo.setPaxId(String.valueOf(passenger.getId()));
			if (passenger.getPassengerIDTag() != null) {
				passengerVo.setPaxIdTag(passenger.getPassengerIDTag().getIdTag());
			}
			passengerVo.setPassengerType(passenger.getPassengerDetails().getPassengerType());
			passengerVo.setLastName(passenger.getPassengerDetails().getLastName());
			passengerVo.setFirstName(passenger.getPassengerDetails().getFirstName());
			passengerVo.setMiddleName(passenger.getPassengerDetails().getMiddleName());
			passengerVo.setNationality(passenger.getPassengerDetails().getNationality());
			passengerVo.setDebarkation(passenger.getPassengerTripDetails().getDebarkation());
			passengerVo.setDebarkCountry(passenger.getPassengerTripDetails().getDebarkCountry());
			passengerVo.setDob(passenger.getPassengerDetails().getDob());
			passengerVo.setAge(passenger.getPassengerDetails().getAge());
			passengerVo.setEmbarkation(passenger.getPassengerTripDetails().getEmbarkation());
			passengerVo.setEmbarkCountry(passenger.getPassengerTripDetails().getEmbarkCountry());
			passengerVo.setGender(
					passenger.getPassengerDetails().getGender() != null ? passenger.getPassengerDetails().getGender()
							: "");
			passengerVo.setResidencyCountry(passenger.getPassengerDetails().getResidencyCountry());
			passengerVo.setSuffix(passenger.getPassengerDetails().getSuffix());
			passengerVo.setTitle(passenger.getPassengerDetails().getTitle());

			Iterator<Document> documentIterator = passenger.getDocuments().iterator();
			while (documentIterator.hasNext()) {
				Document document = documentIterator.next();
				DocumentVo documentVo = new DocumentVo();
				documentVo.setDocumentNumber(document.getDocumentNumber());
				documentVo.setDocumentType(document.getDocumentType());
				documentVo.setIssuanceCountry(document.getIssuanceCountry());
				documentVo.setExpirationDate(document.getExpirationDate());
				documentVo.setIssuanceDate(document.getIssuanceDate());
				passengerVo.addDocument(documentVo);
			}

			// PNR info
			List<Pnr> pnrList = pnrService.findPnrByPassengerIdAndFlightId(passenger.getId(), flightId);

			if (!pnrList.isEmpty()) {
				Pnr latestPnr = PaxDetailVoUtil.getLatestPnrFromList(pnrList);
				passengerVo.setPnrVo(PaxDetailVoUtil.mapPnrToPnrVo(latestPnr));
				paxDetailPdfDocRequest.setTripType(latestPnr.getTripType());
			}

			paxDetailPdfDocRequest.setPassengerVo(passengerVo);
						
			//HIT INFO
			setHitInformation(paxDetailPdfDocRequest, paxId);
			//HIT HISTORY
			setHitHistory(paxDetailPdfDocRequest, paxId);

			// Flight History
			setFlightHistory(paxDetailPdfDocRequest, paxId);

			// Notes
			setNotes(paxDetailPdfDocRequest, paxId);

			// Historical Notes
			setNoteHistory(paxDetailPdfDocRequest, paxId);

			try {
				paxDetailPdfDocResponse = passengerEventReportService.createPaxDetailReport(paxDetailPdfDocRequest);
			} catch (Exception exception) {
				logger.error("An error has occurred when creating pdf requests", exception);
			}

		}

		return paxDetailPdfDocResponse;
	}

	public void setHitInformation(PaxDetailPdfDocRequest paxDetailPdfDocRequest, Long paxId)
	{
		Set<HitDetail> hitDetailSet = hitDetailService.getByPassengerId(paxId);
		if (hitDetailSet != null) {
			HitDetailVo hitDetailVo;
			LinkedHashSet<HitDetailVo> hitDetailVoList = new LinkedHashSet<>();
			for (HitDetail htd : hitDetailSet) {
				hitDetailVo = new HitDetailVo();
				hitDetailVo.setRuleId(htd.getRuleId());
				hitDetailVo.setRuleTitle(htd.getTitle());
				hitDetailVo.setRuleDesc(htd.getDescription());
				hitDetailVo.setSeverity(htd.getHitMaker().getHitCategory().getSeverity().toString());
				HitMaker lookout = htd.getHitMaker();
				HitCategory hitCategory = lookout.getHitCategory();
				hitDetailVo.setCategory(hitCategory.getName() + "(" + htd.getHitEnum().getDisplayName() + ")");
				hitDetailVo.setRuleAuthor(htd.getHitMaker().getAuthor().getUserId());
				hitDetailVo.setRuleConditions(htd.getRuleConditions());
				hitDetailVo.setRuleTitle(htd.getTitle());
				StringJoiner stringJoiner = new StringJoiner(", ");
				for (HitViewStatus hitViewStatus : htd.getHitViewStatus()) {
					stringJoiner.add(hitViewStatus.getHitViewStatusEnum().toString());
				}
				hitDetailVo.setFlightDate(htd.getFlight().getMutableFlightDetails().getEtd());
				hitDetailVo.setStatus(stringJoiner.toString());
				hitDetailVoList.add(hitDetailVo);
			}

			paxDetailPdfDocRequest.setHitDetailVoList(hitDetailVoList);
			String highestSeverityValue = getHighestHitSeverity(hitDetailSet);
			paxDetailPdfDocRequest.setHighestSeverity(highestSeverityValue);
		}
		
	}
	
	public void setHitHistory(PaxDetailPdfDocRequest paxDetailPdfDocRequest, Long paxId)
	{
		
		List<Passenger> passengersWithSamePassengerIdTag = passengerService
				.getBookingDetailHistoryByPaxID(paxId);
		Set<Passenger> passengerSet = new HashSet<>(passengersWithSamePassengerIdTag);
		Passenger p = passengerService.findById(paxId);
		passengerSet.remove(p);
		List<HitDetailVo> hitDetailHistoryVoList = hitDetailService.getLast10RecentHits(passengerSet);
		paxDetailPdfDocRequest.setHitDetailHistoryVoList(hitDetailHistoryVoList);
		
		
		
	}
	
	public void setFlightHistory(PaxDetailPdfDocRequest paxDetailPdfDocRequest, Long paxId)
	{
		List<Passenger> passengerRecList = passengerService.getBookingDetailHistoryByPaxID(paxId);
		if (passengerRecList != null) {
			List<FlightVoForFlightHistory> flightVoFHList = PaxDetailVoUtil
					.copyBookingDetailFlightModelToVo(passengerRecList);
			paxDetailPdfDocRequest.setFlightHistoryVoList(flightVoFHList);
		}
	
	}
	
	public void setNotes(PaxDetailPdfDocRequest paxDetailPdfDocRequest, Long paxId)
	{
		PassengerNoteSetDto passengerNoteSetDto = passengerNoteService.getAllEventNotes(paxId);
		if (passengerNoteSetDto != null)
			paxDetailPdfDocRequest.setEventNotesSet(passengerNoteSetDto.getPaxNotes());
		
	}
	
	public void setNoteHistory(PaxDetailPdfDocRequest paxDetailPdfDocRequest, Long paxId)
	{
		PassengerNoteSetDto passengerNoteHistorySetDto = passengerNoteService.getPrevious10PassengerNotes(paxId);
		if (passengerNoteHistorySetDto != null)
			paxDetailPdfDocRequest.setEventHistoricalNotesSet(passengerNoteHistorySetDto.getPaxNotes());
		
	}
	
	private String getHighestHitSeverity(Set<HitDetail> hitDetailSet) {
		String highestSeverity = HitSeverityEnum.NORMAL.toString();
		String severityValue = "";

		if (hitDetailSet != null && !hitDetailSet.isEmpty()) {
			int i = 0;
			for (HitDetail htd : hitDetailSet) {

				severityValue = htd.getHitMaker().getHitCategory().getSeverity().toString();

				if (severityValue.equalsIgnoreCase(HitSeverityEnum.HIGH.toString())) {
					highestSeverity = severityValue;
				} else if (severityValue.equalsIgnoreCase(HitSeverityEnum.TOP.toString())
						&& highestSeverity.equalsIgnoreCase(HitSeverityEnum.NORMAL.toString())) {

					highestSeverity = severityValue;
				}

				i++;
			}
		}

		return highestSeverity;
	}

}
