package gov.gtas.services.dto;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import gov.gtas.vo.HitDetailVo;
import gov.gtas.vo.NoteVo;
import gov.gtas.vo.passenger.FlightVo;
import gov.gtas.vo.passenger.FlightVoForFlightHistory;
import gov.gtas.vo.passenger.PassengerVo;

public class PaxDetailPdfDocRequest extends PdfDocumentRequest{
	
	
	private PassengerVo passengerVo;
	
	private List<FlightVoForFlightHistory> flightHistoryVoList;
	
	private LinkedHashSet<HitDetailVo> hitDetailVoList;
	
	private List<HitDetailVo> hitDetailHistoryVoList;
	
	private LinkedHashSet<NoteVo> eventNotesSet;
	
	private LinkedHashSet<NoteVo> eventHistoricalNotesSet;
	
	private Map<String,String> translationValues;
	
	private String highestSeverity;
	
	private String alert;
	
	private String tripType;
	
    
	public PassengerVo getPassengerVo() {
		return passengerVo;
	}

	public void setPassengerVo(PassengerVo passengerVo) {
		this.passengerVo = passengerVo;
	}



	public List<FlightVoForFlightHistory> getFlightHistoryVoList() {
		return flightHistoryVoList;
	}

	public void setFlightHistoryVoList(List<FlightVoForFlightHistory> flightHistoryVoList) {
		this.flightHistoryVoList = flightHistoryVoList;
	}

	public void setHitDetailVoList(LinkedHashSet<HitDetailVo> hitDetailVoList) {
		this.hitDetailVoList = hitDetailVoList;
	}

	public LinkedHashSet<HitDetailVo> getHitDetailVoList() {
		return hitDetailVoList;
	}

	public List<HitDetailVo> getHitDetailHistoryVoList() {
		return hitDetailHistoryVoList;
	}

	public void setHitDetailHistoryVoList(List<HitDetailVo> hitDetailHistoryVoList) {
		this.hitDetailHistoryVoList = hitDetailHistoryVoList;
	}

	public LinkedHashSet<NoteVo> getEventNotesSet() {
		return eventNotesSet;
	}

	public void setEventNotesSet(LinkedHashSet<NoteVo> eventNotesSet) {
		this.eventNotesSet = eventNotesSet;
	}

	public LinkedHashSet<NoteVo> getEventHistoricalNotesSet() {
		return eventHistoricalNotesSet;
	}

	public void setEventHistoricalNotesSet(LinkedHashSet<NoteVo> eventHistoricalNotesSet) {
		this.eventHistoricalNotesSet = eventHistoricalNotesSet;
	}
	
	public Map<String, String> getTranslationValues() {
		return translationValues;
	}

	public void setTranslationValues(Map<String, String> translationValues) {
		this.translationValues = translationValues;
	}

	public String getHighestSeverity() {
		return highestSeverity;
	}

	public void setHighestSeverity(String highestSeverity) {
		this.highestSeverity = highestSeverity;
	}

	public String getAlert() {
		return alert;
	}

	public void setAlert(String alert) {
		this.alert = alert;
	}

	public String getTripType() {
		return tripType;
	}

	public void setTripType(String tripType) {
		this.tripType = tripType;
	}


	
	
	
	

}
