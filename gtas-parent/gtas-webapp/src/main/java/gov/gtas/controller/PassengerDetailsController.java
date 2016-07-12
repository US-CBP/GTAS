/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.Address;
import gov.gtas.model.Agency;
import gov.gtas.model.CreditCard;
import gov.gtas.model.Disposition;
import gov.gtas.model.Document;
import gov.gtas.model.Email;
import gov.gtas.model.Flight;
import gov.gtas.model.FlightLeg;
import gov.gtas.model.FrequentFlyer;
import gov.gtas.model.Passenger;
import gov.gtas.model.Phone;
import gov.gtas.model.Pnr;
import gov.gtas.model.Seat;
import gov.gtas.model.lookup.DispositionStatus;
import gov.gtas.services.DispositionData;
import gov.gtas.services.FlightService;
import gov.gtas.services.PassengerService;
import gov.gtas.services.PnrService;
import gov.gtas.util.LobUtils;
import gov.gtas.vo.passenger.AddressVo;
import gov.gtas.vo.passenger.AgencyVo;
import gov.gtas.vo.passenger.CaseVo;
import gov.gtas.vo.passenger.CreditCardVo;
import gov.gtas.vo.passenger.DispositionVo;
import gov.gtas.vo.passenger.DocumentVo;
import gov.gtas.vo.passenger.EmailVo;
import gov.gtas.vo.passenger.FlightHistoryVo;
import gov.gtas.vo.passenger.FlightLegVo;
import gov.gtas.vo.passenger.FlightVo;
import gov.gtas.vo.passenger.FrequentFlyerVo;
import gov.gtas.vo.passenger.PassengerVo;
import gov.gtas.vo.passenger.PhoneVo;
import gov.gtas.vo.passenger.PnrVo;
import gov.gtas.vo.passenger.SeatVo;

@Controller
public class PassengerDetailsController {
    private static final Logger logger = LoggerFactory
            .getLogger(PassengerDetailsController.class);

    @Autowired
    private PassengerService pService;

    @Autowired
    private FlightService fService;

    @Autowired
    private PnrService pnrService;

    private static final String EMPTY_STRING = "";

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/passengers/passenger/{id}/details", method = RequestMethod.GET)
    public PassengerVo getPassengerByPaxIdAndFlightId(
            @PathVariable(value = "id") String paxId,
            @RequestParam(value = "flightId", required = false) String flightId) {
        PassengerVo vo = new PassengerVo();
        Iterator _tempIter;
        List _tempPnrList = new ArrayList();
        List<FlightVo> _tempFlightVoList = new ArrayList<FlightVo>();
        HashMap<Document, List<Flight>> _tempFlightHistoryMap = new HashMap<Document, List<Flight>>();

        Long id = Long.valueOf(paxId);
        Passenger t = pService.findById(id);
        Flight _tempFlight = fService.findById(Long.parseLong(flightId));
        Flight theFlight = null;
        if (flightId != null && _tempFlight.getId().toString().equals(flightId)) {
            vo.setFlightNumber(_tempFlight.getFlightNumber());
            vo.setCarrier(_tempFlight.getCarrier());
            vo.setFlightOrigin(_tempFlight.getOrigin());
            vo.setFlightDestination(_tempFlight.getDestination());
            vo.setFlightETA((_tempFlight.getEta() != null) ? _tempFlight
                    .getEta().toString() : EMPTY_STRING);
            vo.setFlightETD((_tempFlight.getEtd() != null) ? _tempFlight
                    .getEtd().toString() : EMPTY_STRING);
            vo.setFlightId(_tempFlight.getId().toString());
            theFlight = _tempFlight;
        }
        

        vo.setPaxId(String.valueOf(t.getId()));
        vo.setPassengerType(t.getPassengerType());
        vo.setLastName(t.getLastName());
        vo.setFirstName(t.getFirstName());
        vo.setMiddleName(t.getMiddleName());
        vo.setCitizenshipCountry(t.getCitizenshipCountry());
        vo.setDebarkation(t.getDebarkation());
        vo.setDebarkCountry(t.getDebarkCountry());
        vo.setDob(t.getDob());
        vo.setEmbarkation(t.getEmbarkation());
        vo.setEmbarkCountry(t.getEmbarkCountry());
        vo.setGender(t.getGender() != null ? t.getGender().toString() : "");
        vo.setResidencyCountry(t.getResidencyCountry());
        vo.setSuffix(t.getSuffix());
        vo.setTitle(t.getTitle());
        
        for (Seat s : t.getSeatAssignments()) {
            if (s.getFlight().getId() == theFlight.getId()) {
                if (s.getApis()) {
                    vo.setSeat(s.getNumber());
                    break;
                }
            }
        }
        
        _tempIter = t.getDocuments().iterator();
        while (_tempIter.hasNext()) {
            Document d = (Document) _tempIter.next();
            DocumentVo docVo = new DocumentVo();
            docVo.setDocumentNumber(d.getDocumentNumber());
            docVo.setDocumentType(d.getDocumentType());
            docVo.setIssuanceCountry(d.getIssuanceCountry());
            docVo.setExpirationDate(d.getExpirationDate());
            docVo.setIssuanceDate(d.getIssuanceDate());
            vo.addDocument(docVo);
        }
        
        List<Disposition> cases = pService.getPassengerDispositionHistory(id, Long.parseLong(flightId));
        if (CollectionUtils.isNotEmpty(cases)) {
            List<DispositionVo> history = new ArrayList<>();
            for (Disposition d : cases) {
                DispositionVo dvo = new DispositionVo();
                dvo.setComments(d.getComments());
                dvo.setCreatedAt(d.getCreatedAt());
                dvo.setStatus(d.getStatus().getName());
                dvo.setCreatedBy(d.getCreatedBy());
                history.add(dvo);
            }
            vo.setDispositionHistory(history);          
        }
        
        // Gather PNR Details
        _tempPnrList = pnrService.findPnrByPassengerIdAndFlightId(t.getId(),
                new Long(flightId));

        if (_tempPnrList.size() >= 1) {
            vo.setPnrVo(mapPnrToPnrVo((Pnr) _tempPnrList.get(0)));
        }

        return vo;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/passengers/passenger/flighthistory", method = RequestMethod.GET)
        public FlightHistoryVo getFlightHistoryByPassengerAndDocuments(
        @RequestParam(value = "paxId") String paxId){
        
        HashMap<Document, List<Flight>> _tempFlightHistoryMap = new HashMap<Document, List<Flight>>();
        FlightHistoryVo flightHistoryVo = new FlightHistoryVo();
        List<FlightVo> _tempFlightVoList = new ArrayList<FlightVo>();

        PassengerVo vo = new PassengerVo();
        
        Long id = Long.valueOf(paxId);
        Passenger t = pService.findById(id);
        
        // Gather Flight History Details
                _tempFlightHistoryMap = fService.getFlightsByPassengerNameAndDocument(
                        t.getFirstName(), t.getLastName(), t.getDocuments());

                for (Document document : _tempFlightHistoryMap.keySet()) {
                    for (Document doc : t.getDocuments()) {
                        if ((document.getDocumentNumber() != null)
                                && (document.getDocumentNumber().equals(
                                        doc.getDocumentNumber()) && (document
                                        .getDocumentType().equalsIgnoreCase(doc
                                        .getDocumentType())))) {
                            _tempFlightVoList.clear();
                            for (Flight flight : _tempFlightHistoryMap.get(document)) {
                                FlightVo _tempFlightVo = new FlightVo();
                                copyModelToVo(flight, _tempFlightVo);
                                _tempFlightVoList.add(_tempFlightVo);
                            }
                            flightHistoryVo.getFlightHistoryMap().put(
                                    doc.getDocumentNumber(), _tempFlightVoList);
                        }
                    }
                }
        
        return flightHistoryVo;
    }
    
    @RequestMapping(value = "/dispositionstatuses", method = RequestMethod.GET)
    public @ResponseBody List<DispositionStatus> getDispositionStatuses() {
        return pService.getDispositionStatuses();
    }   
    
    @RequestMapping(value = "/disposition", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody JsonServiceResponse createDisposition(@RequestBody DispositionData disposition) {
        pService.createDisposition(disposition);
        return new JsonServiceResponse(Status.SUCCESS, "Create disposition successful");
    }

    @RequestMapping(value = "/allcases", method = RequestMethod.GET)
    public @ResponseBody List<CaseVo> getAllDispositions() {
        return pService.getAllDispositions();
    }   
    
    /**
     * Util method to map PNR model object to VO
     * 
     * @param source
     * @return
     */
    public PnrVo mapPnrToPnrVo(Pnr source) {
        PnrVo target = new PnrVo();

        if(source.getRecordLocator() == null || source.getRecordLocator().isEmpty()){target.setPnrRecordExists(false); return target;}
        target.setPnrRecordExists(true);
        target.setRecordLocator(source.getRecordLocator());
        target.setBagCount(source.getBagCount());
        target.setDateBooked(source.getDateBooked());
        target.setCarrier(source.getCarrier());
        target.setDaysBookedBeforeTravel(source.getDaysBookedBeforeTravel());
        target.setDepartureDate(source.getDepartureDate());
        target.setFormOfPayment(source.getFormOfPayment());
        target.setOrigin(source.getOrigin());
        target.setOriginCountry(source.getOriginCountry());
        target.setPassengerCount(source.getPassengerCount());
        target.setDateReceived(source.getDateReceived());
        target.setRaw(LobUtils.convertClobToString(source.getRaw()));
        parseRawMessageToList(target);

        if (source.getAddresses() != null && source.getAddresses().size() > 0) {
            Iterator it = source.getAddresses().iterator();
            while (it.hasNext()) {
                Address a = (Address) it.next();
                AddressVo aVo = new AddressVo();

                try {

                    BeanUtils.copyProperties(aVo, a);

                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                target.getAddresses().add(aVo);

            } // End of While Loop

        }

        if (CollectionUtils.isNotEmpty(source.getAgencies())) {
            AgencyVo aVo = new AgencyVo();
            for (Agency agency : source.getAgencies()) {
                copyModelToVo(agency, aVo);
                target.getAgencies().add(aVo);
            }
        }

        if (source.getCreditCards() != null
                && source.getCreditCards().size() > 0) {
            Iterator it1 = source.getCreditCards().iterator();
            while (it1.hasNext()) {
                CreditCard cc = (CreditCard) it1.next();
                CreditCardVo cVo = new CreditCardVo();
                copyModelToVo(cc, cVo);
                target.getCreditCards().add(cVo);
            }
        }
        if (source.getFrequentFlyers() != null
                && source.getFrequentFlyers().size() > 0) {
            Iterator it2 = source.getFrequentFlyers().iterator();
            while (it2.hasNext()) {
                FrequentFlyer ff = (FrequentFlyer) it2.next();
                FrequentFlyerVo fVo = new FrequentFlyerVo();
                copyModelToVo(ff, fVo);
                target.getFrequentFlyerDetails().add(fVo);
            }
        }

        if (source.getEmails() != null && source.getEmails().size() > 0) {
            Iterator it3 = source.getEmails().iterator();
            while (it3.hasNext()) {
                Email e = (Email) it3.next();
                EmailVo eVo = new EmailVo();
                copyModelToVo(e, eVo);
                target.getEmails().add(eVo);
            }
        }

        if (source.getPhones() != null && source.getPhones().size() > 0) {
            Iterator it4 = source.getPhones().iterator();
            while (it4.hasNext()) {
                Phone p = (Phone) it4.next();
                PhoneVo pVo = new PhoneVo();
                copyModelToVo(p, pVo);
                target.getPhoneNumbers().add(pVo);
            }
        }
        
        if(source.getFlightLegs() != null && source.getFlightLegs().size() > 0){
            List<FlightLeg> _tempFL = source.getFlightLegs();
            for(FlightLeg fl : _tempFL){
                FlightLegVo flVo = new FlightLegVo();
                flVo.setLegNumber(fl.getLegNumber().toString());
                flVo.setFlightNumber(fl.getFlight().getFullFlightNumber());
                flVo.setOriginAirport(fl.getFlight().getOrigin());
                flVo.setDestinationAirport(fl.getFlight().getDestination());
                flVo.setFlightDate(fl.getFlight().getFlightDate().toString());
                flVo.setEtd(fl.getFlight().getEtd().toString());
                target.getFlightLegs().add(flVo);
            }
        }
        
        if(source.getPassengers() != null && source.getPassengers().size() > 0){
            Iterator it4 = source.getPassengers().iterator();
            while (it4.hasNext()) {
                Passenger p = (Passenger) it4.next();
                PassengerVo pVo = new PassengerVo();
                pVo.setLastName(p.getLastName());
                pVo.setFirstName(p.getFirstName());
                pVo.setMiddleName(p.getMiddleName());
                target.getPassengers().add(pVo);
                
                Set<Seat> seats = p.getSeatAssignments();
                for (Seat s : seats) {
                    if (!s.getApis()) {
                        SeatVo seatVo = new SeatVo();
                        seatVo.setFirstName(s.getPassenger().getFirstName());
                        seatVo.setLastName(s.getPassenger().getLastName());
                        seatVo.setNumber(s.getNumber());
                        seatVo.setFlightNumber(s.getFlight().getFullFlightNumber());
                        target.getSeatAssignments().add(seatVo);
                    }
                }
            }
        }
        
        return target;
    }

    /**
     * Util Method To Parse PNR Raw Format Message to List For The Front End
     * 
     * @param targetVo
     */
    private void parseRawMessageToList(PnrVo targetVo) {

        if (targetVo != null && targetVo.getRaw() != null) {
            StringTokenizer _tempStr = new StringTokenizer(targetVo.getRaw(),
                    "\n");
            ArrayList<String> _tempList = new ArrayList<String>();
            while (_tempStr.hasMoreTokens()) {
                _tempList.add(_tempStr.nextToken());
            }
            targetVo.setRawList(_tempList);
        }
    }

    /**
     * 
     * @param source
     * @param target
     */
    private void copyModelToVo(Object source, Object target) {

        try {
            BeanUtils.copyProperties(target, source);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
