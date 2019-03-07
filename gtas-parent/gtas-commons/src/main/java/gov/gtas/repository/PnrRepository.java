/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gov.gtas.model.Pnr;
import gov.gtas.model.lookup.Airport;

public interface PnrRepository extends MessageRepository<Pnr> {
    @Query("select pnr from Pnr pnr " +
            "join fetch pnr.passengers pax " +
            "left join fetch pax.documents " +
            "join fetch pax.flightPaxList fpxl " +
            "join fetch fpxl.flight " +
            "join fetch pnr.flights f " +
            "where pax.id = :passengerId and f.id = :flightId")
    public List<Pnr> getPnrsByPassengerIdAndFlightId(@Param("passengerId") Long passengerId,
                                        @Param("flightId") Long flightId);
    
    @Query("select pnr from Pnr pnr join pnr.passengers pax where pax.id = :passengerId")
    public List<Pnr> getPnrsByPassengerId(@Param("passengerId") Long passengerId);

    @Query("SELECT pnr FROM Pnr pnr WHERE pnr.createDate >= current_date() - 1")
    public List<Pnr> getPNRsByDates();
    
    default Pnr findOne(Long id)
    {
    	return findById(id).orElse(null);
    }
}
