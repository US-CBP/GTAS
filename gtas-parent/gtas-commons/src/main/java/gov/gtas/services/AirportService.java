/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;
import gov.gtas.model.lookup.Airport;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGE_ADMIN;

public interface AirportService {
    
  @PreAuthorize(PRIVILEGE_ADMIN)
  public Airport create(Airport port);
  @PreAuthorize(PRIVILEGE_ADMIN)
  public Airport delete(Long id);
  public List<Airport> findAll();
  @PreAuthorize(PRIVILEGE_ADMIN)
  public Airport update(Airport port) ;
  @PreAuthorize(PRIVILEGE_ADMIN)
  public Airport restore(Airport airport);
  @PreAuthorize(PRIVILEGE_ADMIN)
  public int restoreAll();
  public Airport findById(Long id);
  public Airport getAirportByThreeLetterCode(String airportCode);
  public Airport getAirportByFourLetterCode(String airportCode);

}
