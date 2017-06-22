/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.Filter;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.gtas.model.Filter;
import gov.gtas.model.FlightDirection;
import gov.gtas.model.User;
import gov.gtas.model.lookup.Airport;
import gov.gtas.repository.LookUpRepository;

@Component
public class FilterServiceUtil {

    @Autowired
    private LookUpRepository lookupRepository;

    public FilterData mapFilterDataFromEntity(Filter entity) {

        FilterData filterData = null;

        if (entity != null) {
            Set<String> originAirports = entity.getOriginAirports().stream().map(airport -> airport.getIata())
                    .collect(Collectors.toSet());
            Set<String> destinationAirports = entity.getDestinationAirports().stream().map(airport -> airport.getIata())
                    .collect(Collectors.toSet());

            filterData = new FilterData(entity.getUser().getUserId(), entity.getFlightDirection().getcode(),
                    originAirports, destinationAirports, entity.getEtaStart(), entity.getEtaEnd());
        }

        return filterData;
    }

    public Filter mapFilterEntityFromFilterData(FilterData filterData) {

        Filter filter = null;
        if (filterData != null) {
            filter = new Filter();
            User user = new User();
            user.setUserId(filterData.getUserId());
            filter.setUser(user);

            // DetachedObject
            FlightDirection flightDirection = lookupRepository.getFlightDirections().stream()
                    .filter(fd -> (fd.getcode()).equals(filterData.getFlightDirection())).findFirst().get();

            filter.setFlightDirection(flightDirection);

            Set<Airport> originAirports = lookupRepository.getAllAirports().stream()
                    .filter(airport -> filterData.getOriginAirports().contains(airport.getIata()))
                    .collect(Collectors.toSet());

            Set<Airport> destinationAirports = lookupRepository.getAllAirports().stream()
                    .filter(airport -> filterData.getDestinationAirports().contains(airport.getIata()))
                    .collect(Collectors.toSet());

            filter.setOriginAirports(originAirports);
            filter.setDestinationAirports(destinationAirports);

            filter.setEtaStart(filterData.getEtaStart());
            filter.setEtaEnd(filterData.getEtaEnd());

        }

        return filter;
    }

}
