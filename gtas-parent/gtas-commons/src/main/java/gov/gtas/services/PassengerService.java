/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.Disposition;
import gov.gtas.model.HitsSummary;
import gov.gtas.model.Passenger;
import gov.gtas.model.User;
import gov.gtas.model.lookup.DispositionStatus;
import gov.gtas.services.dto.PassengersPageDto;
import gov.gtas.services.dto.PassengersRequestDto;
import gov.gtas.vo.passenger.CaseVo;
import gov.gtas.vo.passenger.PassengerVo;

import java.util.List;

public interface PassengerService {
    public Passenger create(Passenger passenger);
    public Passenger update(Passenger passenger) ;
    
    public Passenger findById(Long id);
    public List<Passenger> getPassengersByLastName(String lastName);
    
    public List<Disposition> getPassengerDispositionHistory(Long passengerId, Long flightId);
    public void createDisposition(DispositionData disposition, User user);
    public void createDisposition(HitsSummary hit);
    public List<DispositionStatus> getDispositionStatuses();
    public List<CaseVo> getAllDispositions();
    
    public void createOrEditDispositionStatus(DispositionStatus ds);
    public void deleteDispositionStatus(DispositionStatus ds);
    
    /**
     * 
     * @param flightId optional
     * @param request
     * @return
     */
    public PassengersPageDto getPassengersByCriteria(Long flightId, PassengersRequestDto request);

    public void fillWithHitsInfo(PassengerVo vo, Long flightId, Long passengerId);
}
