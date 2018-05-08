package gov.gtas.rest.dao.impl;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import gov.gtas.rest.dao.PassengerDao;
import gov.gtas.rest.mapper.CasesMapper;
import gov.gtas.rest.mapper.DocumentMapper;
import gov.gtas.rest.mapper.HitsSummaryMapper;
import gov.gtas.rest.mapper.PassengerMapper;
import gov.gtas.rest.mapper.PassengerTravelDetailsMapper;
import gov.gtas.rest.model.CaseHitDispComment;
import gov.gtas.rest.model.CaseHitDisposition;
import gov.gtas.rest.model.Cases;
import gov.gtas.rest.model.Document;
import gov.gtas.rest.model.HitDetail;
import gov.gtas.rest.model.HitsSummary;
import gov.gtas.rest.model.Passenger;
import gov.gtas.rest.model.PassengerTravelDetail;
import gov.gtas.rest.request.PassengerRequest;
import gov.gtas.rest.response.PassengerResponse;


@Repository
public class PassengerDaoImpl implements PassengerDao {

	
	@Autowired
	private PassengerMapper passengerMapper;
	
	@Autowired
	private DocumentMapper documentMapper;
	
	@Autowired
	private PassengerTravelDetailsMapper passengerTravelDetailsMapper;
	
	@Autowired
	private HitsSummaryMapper hitsSummaryMapper;
	
	@Autowired
	private CasesMapper casesMapper;
	
	
	@Override
	public PassengerResponse findByID(PassengerRequest passengerRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PassengerResponse findPassengerByName(PassengerRequest passengerRequest) {
		
		List<Passenger> passengerList = passengerMapper.findPassengerByName(passengerRequest.getModel().getFirstName(), passengerRequest.getModel().getLastName());
		PassengerResponse passengerResponse = new PassengerResponse();
		passengerResponse.setPassengers(passengerList);
		
		List<Document> documents = null;
		List<PassengerTravelDetail> travelDetail = null;
		List<HitsSummary> hitsSummaryList = null;
		
		//find documents
		for (Passenger passenger : passengerList)
		{
			documents = documentMapper.findDocumentByPassengerId(passenger.getId());
			passenger.setDocuments(documents);
			travelDetail = passengerTravelDetailsMapper.findPassengerTravelDetail(passenger.getId());
			passenger.setTravelDetail(travelDetail);
		}
		//find hit details
		for (Passenger passenger : passengerList)
		{
			hitsSummaryList = hitsSummaryMapper.findHitSummaryByPassengerId(passenger.getId());
			
			//find hit detail
			for (HitsSummary hitsSummary : hitsSummaryList)
			{
				List<HitDetail> hitDetailList = hitsSummaryMapper.findHitDetailByHitSummaryId(hitsSummary.getId());
				hitsSummary.setHitDetail(hitDetailList);
			}
			
			String dobStr = new SimpleDateFormat("yyyy-MM-dd").format(passenger.getDob());
			
			List<Cases> casesList = casesMapper.findCasesByName(passenger.getFirstName(), passenger.getLastName(), dobStr);
			for(Cases cases : casesList)
				
			{
				List<CaseHitDisposition> caseHitDispositionsList = casesMapper.findCaseHitDisposition(cases.getId());
				cases.setCaseHitDisposition(caseHitDispositionsList);
				
				for(CaseHitDisposition caseHitDisposition : caseHitDispositionsList)
				{
					List<CaseHitDispComment> caseHitDipsComments =  casesMapper.findCaseHitDispComments(caseHitDisposition.getId());
					caseHitDisposition.setCaseHitDispComment(caseHitDipsComments);
				}
				
			}
			
			
			
			passenger.setHitsSummary(hitsSummaryList);
			passenger.setCases(casesList);
		}
		
		return passengerResponse;
	}

}
