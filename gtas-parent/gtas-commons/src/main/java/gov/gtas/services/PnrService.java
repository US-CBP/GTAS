/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.util.Date;
import java.util.List;

import gov.gtas.model.Pnr;

public interface PnrService {
    
    public Pnr create(Pnr pnr);
    public Pnr delete(Long id);
    public Pnr update(Pnr pnr);
    public Pnr findById(Long id);
    public List<Pnr> findAll();
    public List<Pnr> findByPassengerId(Long passengerId);
    /*A duplicate method to avoid 'LazyInitializationException' in the Controller -- Can be removed after a fix */
    public List<Pnr> findPnrByPassengerIdAndFlightId(Long passengerId, Long flightId);
    public List<Pnr> getPNRsByDates(Date startDate, Date endDate);
}
