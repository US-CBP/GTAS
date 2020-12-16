/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.Airport;
import gov.gtas.repository.AirportRepository;
import gov.gtas.repository.AirportRepositoryCustom;
import gov.gtas.vo.lookup.AirportVo;
import gov.gtas.vo.lookup.AirportLookupVo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class AirportServiceImpl implements AirportService {

  @Resource
  private AirportRepository airportRepo;
  @Resource
  private AirportRepositoryCustom airportRepoCust;

  @Override
  @Transactional
  public AirportVo create(AirportVo port) {
    Airport savedAirport = airportRepo.save(buildAirport(port));

    return buildAirportVo(savedAirport);
  }

  @Override
  @Transactional
  public AirportVo delete(Long id) {
    AirportVo airportVo = this.findById(id);

    if (airportVo != null) {
      //airportRepo.delete(buildAirport(airportVo));
      airportVo = archive(airportVo);
    }

    return airportVo;
  }

  @Override
  @Transactional
  public List<AirportVo> findAll() {
    List<Airport> allAirports = (List<Airport>) airportRepo.findAll();

    List<AirportVo> allAirportVos = new ArrayList<>();

    for (Airport airport : allAirports) {
      allAirportVos.add(buildAirportVo(airport));
    }

    return allAirportVos;
  }

  @Override
  @Transactional
  public List<AirportLookupVo> getAirportLookup() {
    List<Airport> allAirports = (List<Airport>) airportRepo.findAll();

    List<AirportLookupVo> allAirportVos = new ArrayList<>();

    for (Airport airport : allAirports) {
      allAirportVos.add(new AirportLookupVo(airport.getName(), airport.getIata()));
    }

    return allAirportVos;
  }

  @Override
  @Transactional
  public AirportVo update(AirportVo port) {
    Airport savedAirport = airportRepo.save(buildAirport(port));

    return buildAirportVo(savedAirport);
  }

  @Override
  @Transactional
  public AirportVo findById(Long id) {
    Airport airport = airportRepo.findOne(id);

    if (airport == null) {
      return null;
    }

    return buildAirportVo(airport);
  }

  @Override
  @Transactional
  public AirportVo restore(AirportVo airport) {
    Airport restoredAirport = airportRepoCust.restore(buildAirport(airport));

    return buildAirportVo(restoredAirport);
  }

  @Override
  @Transactional
  public int restoreAll() {
    return airportRepoCust.restoreAll();
  }

  @Override
  public List<AirportVo> findAllNonArchived() {
    List<Airport> allNonArchivedAirports = airportRepo.findAllNonArchived();
    List<AirportVo> allNonArchivedAirportVos = new ArrayList<>();

    for(Airport a: allNonArchivedAirports){
      allNonArchivedAirportVos.add(buildAirportVo(a));
    }

    return allNonArchivedAirportVos;
  }

  @Override
  @Transactional
  @Cacheable(value = "airportCache", key = "#airportCode")
  public AirportVo getAirportByThreeLetterCode(String airportCode) {
    List<Airport> airports = airportRepo.getAirportByThreeLetterCode(airportCode);

    if (airports != null && airports.size() > 0) {
      return buildAirportVo(airports.get(0));
    }

    return null;
  }

  @Override
  @Transactional
  @Cacheable(value = "airportCache", key = "#airportCode")
  public AirportVo getAirportByFourLetterCode(String airportCode) {
    List<Airport> airports = airportRepo.getAirportByFourLetterCode(airportCode);

    if (airports != null && airports.size() > 0) {
      return buildAirportVo(airports.get(0));
    }

    return null;
  }

  private AirportVo archive(AirportVo avo){
    if (avo != null) {
      Airport a = buildAirport(avo);
      a.setArchived(true);
      airportRepo.save(a);
    }

    return avo;
  }

  static AirportVo buildAirportVo(Airport airport) {
    return new AirportVo(airport.getId(), airport.getOriginId(), airport.getName(), airport.getIata(),
        airport.getIcao(), airport.getCountry(), airport.getCity(), airport.getLatitude(), airport.getLongitude(),
        airport.getUtcOffset(), airport.getTimezone(), airport.getArchived());
  }

  public static Airport buildAirport(AirportVo airportVo) {
    return new Airport(airportVo.getId(), airportVo.getOriginId(), airportVo.getName(), airportVo.getIata(),
        airportVo.getIcao(), airportVo.getCountry(), airportVo.getCity(), airportVo.getLatitude(),
        airportVo.getLongitude(), airportVo.getUtcOffset(), airportVo.getTimezone(), airportVo.getArchived());
  }

}
