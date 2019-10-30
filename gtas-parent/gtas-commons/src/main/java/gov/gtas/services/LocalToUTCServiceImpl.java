/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import com.moodysalem.TimezoneMapper;
import gov.gtas.model.lookup.Airport;
import gov.gtas.vo.lookup.AirportVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.*;

import java.util.Date;

@Component
public class LocalToUTCServiceImpl implements GtasLocalToUTCService {

	private Logger logger = LoggerFactory.getLogger(GtasLocalToUTCService.class);
	private AirportService airportService;

	public LocalToUTCServiceImpl(AirportService airportService) {
		this.airportService = airportService;
	}

	@Override
	public Date convertFromAirportCode(String airportCode, Date date) {
		Airport airport = getAirport(airportCode);
		Date utcDate = date;
		if (airport == null) {
			logger.warn("No airport found; date not processed; airport not found is " + airportCode);
			return utcDate;
		} else if (utcDate == null) {
			logger.warn("Unable to process; date is null!");
			return null; // passed in date is null - returning same value.
		} else {
			ZoneId zoneId = getZoneIdFromAirport(airport);
			LocalDateTime localDateAsUTC = convertToLocalDateViaMillisecond(date);
			int secondsFromUTC = zoneId.getRules().getOffset(localDateAsUTC).getTotalSeconds();
			LocalDateTime accurateDateTime = localDateAsUTC.minusSeconds(secondsFromUTC);
			utcDate = Date.from(accurateDateTime.atZone(ZoneOffset.UTC).toInstant());
		}
		return utcDate;
	}

	private LocalDateTime convertToLocalDateViaMillisecond(Date dateToConvert) {
		return Instant.ofEpochMilli(dateToConvert.getTime()).atZone(ZoneOffset.UTC).toLocalDateTime();
	}

	private ZoneId getZoneIdFromAirport(Airport airport) {
		ZoneId zoneId;
		if (airport.getLatitude() == null || airport.getLongitude() == null) {
			logger.warn("engine did not find zone id. Using system default!!");
			zoneId = ZoneId.systemDefault();
		} else {
			double airportLatitude = airport.getLatitude().doubleValue();
			double airportLongitude = airport.getLongitude().doubleValue();
			String tzName = TimezoneMapper.tzNameAt(airportLatitude, airportLongitude);
			if (tzName == null) {
				logger.warn("engine did not find zone id. Using system default!");
				zoneId = ZoneId.systemDefault();
			} else {
				try {
					zoneId = ZoneId.of(tzName);
				} catch (DateTimeException dte) {
					logger.warn("TZ + " + tzName + " can not be found, using default!" + dte);
					zoneId = ZoneId.systemDefault();
				}
			}
		}
		return zoneId;
	}

	protected Airport getAirport(String airportCode) {
		AirportVo airportVo;
		int IATA_LENGTH = 3;
		int IACO_LENGTH = 4;
		if (airportCode == null || !(airportCode.length() == IACO_LENGTH || airportCode.length() == IATA_LENGTH)) {
			logger.warn("No valid airport in database!!");
			airportVo = null;
		} else if (airportCode.length() == IACO_LENGTH) {
			airportVo = airportService.getAirportByFourLetterCode(airportCode);
		} else {
			airportVo = airportService.getAirportByThreeLetterCode(airportCode);
		}

		if (airportVo == null) {
			return null;
		}

		return AirportServiceImpl.buildAirport(airportVo);
	}
}
