package gov.gtas.services;

import gov.gtas.services.search.FlightPassengerVo;

import java.util.List;

public interface ApisControllerService {

    List<FlightPassengerVo> generateFlightPaxVoByApisRef(String ref);
}
