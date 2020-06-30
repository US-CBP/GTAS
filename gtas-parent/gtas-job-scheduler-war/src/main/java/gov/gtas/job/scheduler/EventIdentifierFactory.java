package gov.gtas.job.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gtas.model.lookup.Airport;
import gov.gtas.parsers.edifact.EdifactLexer;
import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.paxlst.segment.unedifact.DTM;
import gov.gtas.parsers.paxlst.segment.unedifact.LOC;
import gov.gtas.parsers.paxlst.segment.unedifact.TDT;
import gov.gtas.parsers.pnrgov.segment.TVL_L0;
import gov.gtas.parsers.util.DateUtils;
import gov.gtas.services.LoaderUtils;
import gov.gtas.summary.EventIdentifier;
import gov.gtas.summary.MessageSummaryList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static gov.gtas.services.GtasLoaderImpl.*;
import static gov.gtas.services.GtasLoaderImpl.ETD_DATE_NO_TIMESTAMP_AS_LONG;

@Component
public class EventIdentifierFactory {
    private static final Logger logger = LoggerFactory.getLogger(EventIdentifierFactory.class);

    final
    LoaderUtils loaderUtils;

    public EventIdentifierFactory(LoaderUtils loaderUtils) {
        this.loaderUtils = loaderUtils;
    }

    public EventIdentifier createEventIdentifier(Message<?> message) throws ParseException {
        message.getHeaders();
        EventIdentifier eventIdentifier = new EventIdentifier();

        MessageSummaryList msl = null;
        String msg =  (String)message.getPayload();
        if (maybeJSON(msg.trim())) {
            try {
                ObjectMapper om = new ObjectMapper();
                msl = om.readValue(msg.trim(), MessageSummaryList.class);
            } catch (Exception ignored) {
                // We don't care if the message doesn't marshall.
                // It might be JSON so we try to marshal.
                // It might be a legitimate APIS/PNR EDIFACT message otherwise.
            }
            if (msl != null) {
                return msl.getEventIdentifier();
            }
        }

        if (message.getHeaders().get("eventType") != null) {
            MessageHeaders messageHeaders = message.getHeaders();
            if (messageHeaders.get("identifierList") != null) {
                @SuppressWarnings("unchecked")
                List<String>  eventArray = (List<String>) messageHeaders.get("identifierList");
                String eventKeyString = Objects.requireNonNull(eventArray).get(PRIME_FLIGHT_ORIGIN) + eventArray.get(PRIME_FLIGHT_DESTINATION)
                        + eventArray.get(PRIME_FLIGHT_CARRIER) + eventArray.get(PRIME_FLIGHT_NUMBER_STRING)
                        + eventArray.get(ETD_DATE_NO_TIMESTAMP_AS_LONG);
                String eventType = (String) messageHeaders.get("eventType");
                if (messageHeaders.get("originCountry") != null)  {
                    setOriginCountry(eventIdentifier, eventArray.get(PRIME_FLIGHT_ORIGIN));
                }
                if (messageHeaders.get("destinationCountry") != null)  {
                    setDestinationCountry(eventIdentifier, eventArray.get(PRIME_FLIGHT_DESTINATION));
                }
                eventIdentifier.setIdentifier(eventKeyString);
                eventIdentifier.setIdentifierArrayList(eventArray);
                eventIdentifier.setEventType(eventType);
            } else {
                logger.error("File type specified but message does not contain identifierList header!");
                // If identifier list header is present but incomplete
                // assume there is an error.
                throw new ParseException("Event array absent or incomplete");
            }
        } else {
            /*
             * Crafts prime flight key out of TVL0 line of a PNR message or DTM LOC and TDT
             * of an APIS message. Key is the following primeFlightKeyArray[0] = PRIME
             * FLIGHT ORIGIN primeFlightKeyArray[1] = PRIME FLIGHT DESTINATION
             * primeFlightKeyArray[2] = PRIME FLIGHT CARRIER
             * primeFlightKeyArray[3] = PRIME FLIGHT NUMBER
             * primeFlightKeyArray[4] = PRIME FLIGHT ETD DATE AS A STRING LONG VALUE
             * primeFlightKeyArray[5] = PRIME FLIGHT ETD TIMESTAMP AS A STRING LONG VALUE
             */
            String[] primeFlightKeyArray = new String[6];
            String payload = (String) message.getPayload();
            List<Segment> messageSegments = getMessageSegments(payload);
            boolean apisMessage = true;
            // Arbitrarily attempt to read prime flight from PNR first.
            for (Segment segment : messageSegments) {
                // Extract the prime flight information from a PNR message.
                // This will mirror prime flight array result of an APIS message.
                // PNR and APIS messages relating to the same prime flight
                // will always generate the same label.
                if (segment.getName().equalsIgnoreCase("TVL")) {
                    apisMessage = false;
                    TVL_L0 tvl = new TVL_L0(segment.getComposites());
                    primeFlightKeyArray[PRIME_FLIGHT_ORIGIN] = tvl.getOrigin().trim();
                    primeFlightKeyArray[PRIME_FLIGHT_DESTINATION] = tvl.getDestination().trim();
                    primeFlightKeyArray[PRIME_FLIGHT_CARRIER] = tvl.getCarrier().trim();
                    String primeFlightNumber = tvl.getFlightNumber().trim();
                    primeFlightNumber = addZerosToPrimeFlightIfNeeded(primeFlightNumber);
                    primeFlightKeyArray[PRIME_FLIGHT_NUMBER_STRING] = primeFlightNumber;
                    primeFlightKeyArray[ETD_DATE_NO_TIMESTAMP_AS_LONG] = Long.toString(flightDate(tvl));
                    primeFlightKeyArray[ETD_DATE_WITH_TIMESTAMP] = Long.toString(tvl.getEtd().getTime());
                    break;
                }
            }

            // If the attempt to parse a PNR doesn't result in a prime flight key attempt to
            // read segments as an APIS message.
            if (apisMessage) {
                boolean primeFlightArrivalFound = false;
                boolean primeFlightDepartFound = false;
                boolean primeFlightDepartDateFound = false;
                for (Segment seg : messageSegments) {
                    // Extract the prime flight information from an APIS message.
                    // This will mirror prime flight array result of a PNR message.
                    // PNR and APIS messages relating to the same prime flight
                    // will always generate the same label.
                    switch (seg.getName()) {
                        case "TDT":
                            // TDT is the parent of LOC and DTM. We are basing processing the loop off the
                            // messages in the
                            // messages below. This means the information relating the TDT can be
                            // overwritten several times
                            // before finding a prime flight.
                            TDT tdt = new TDT(seg.getComposites());
                            primeFlightKeyArray[PRIME_FLIGHT_CARRIER] = tdt.getC_carrierIdentifier();
                            String primeFlightNumber = tdt.getFlightNumber().trim();
                            primeFlightNumber = addZerosToPrimeFlightIfNeeded(primeFlightNumber);
                            primeFlightKeyArray[PRIME_FLIGHT_NUMBER_STRING] = primeFlightNumber;
                            break;
                        case "LOC":
                            LOC loc = new LOC(seg.getComposites());
                            // The arrival airport corresponds to the prime flight's arrival airport.
                            if (loc.getFunctionCode() == LOC.LocCode.ARRIVAL_AIRPORT) {
                                primeFlightKeyArray[PRIME_FLIGHT_DESTINATION] = loc.getLocationNameCode();
                                primeFlightArrivalFound = true;
                                // The departure airport corresponds with the prime flight's departure airport.
                            } else if (loc.getFunctionCode() == LOC.LocCode.DEPARTURE_AIRPORT) {
                                primeFlightKeyArray[PRIME_FLIGHT_ORIGIN] = loc.getLocationNameCode();
                                primeFlightDepartFound = true;
                            }
                            break;
                        case "DTM":
                            DTM dtm = new DTM(seg.getComposites());
                            // Take advantage that the next DTM after the prime flight departure airport
                            // will hold the prime flight ETD.
                            if (dtm.getDtmCode() == DTM.DtmCode.DEPARTURE && primeFlightDepartFound) {
                                primeFlightKeyArray[ETD_DATE_NO_TIMESTAMP_AS_LONG] = Long
                                        .toString(DateUtils.stripTime(dtm.getDtmValue()).getTime());
                                primeFlightKeyArray[ETD_DATE_WITH_TIMESTAMP] = Long.toString(dtm.getDtmValue().getTime());
                                primeFlightDepartDateFound = true;
                            }
                            break;
                        default:
                            break;
                    }
                    if (primeFlightArrivalFound && primeFlightDepartFound && primeFlightDepartDateFound) {
                        // Prime flight generated - stop processing message!
                        break;
                    }
                }
            }
            String eventKeyString = Objects.requireNonNull(primeFlightKeyArray)[PRIME_FLIGHT_ORIGIN] + primeFlightKeyArray[PRIME_FLIGHT_DESTINATION]
                    + primeFlightKeyArray[PRIME_FLIGHT_CARRIER] + primeFlightKeyArray[PRIME_FLIGHT_NUMBER_STRING]
                    + primeFlightKeyArray[ETD_DATE_NO_TIMESTAMP_AS_LONG];
            String eventType = apisMessage ? "APIS":"PNR";
            List<String> eventKeyStringList = Arrays.asList(primeFlightKeyArray);
            setOriginCountry(eventIdentifier, eventKeyStringList.get(PRIME_FLIGHT_ORIGIN));
            setDestinationCountry(eventIdentifier, eventKeyStringList.get(PRIME_FLIGHT_DESTINATION));
            eventIdentifier.setIdentifierArrayList(eventKeyStringList);
            eventIdentifier.setIdentifier(eventKeyString);
            eventIdentifier.setEventType(eventType);
        }
        return eventIdentifier;
    }

    private void setDestinationCountry(EventIdentifier eventIdentifier, String destAirport) {
        Airport dAirport = loaderUtils.getAirport(destAirport);
        if (dAirport != null) {
            eventIdentifier.setCountryDestination(dAirport.getCountry());
        }
    }

    private void setOriginCountry(EventIdentifier eventIdentifier, String originAirport) {
        Airport oAirport = loaderUtils.getAirport(originAirport);
        if (oAirport != null) {
            eventIdentifier.setCountryOrigin(oAirport.getCountry());
        }
    }
    private boolean maybeJSON(String potentialMessageList) {
        return (potentialMessageList.startsWith("{") || potentialMessageList.startsWith("[")) &&
                (potentialMessageList.endsWith("}") || potentialMessageList.endsWith("]"));
    }

    private static long flightDate(TVL_L0 tvl) {
        return DateUtils.stripTime(tvl.getEtd()).getTime();
    }

    private static List<Segment> getMessageSegments(String payload) {
        List<Segment> segments = new ArrayList<>();
        EdifactLexer lexer = new EdifactLexer(payload);
        try {
            segments = lexer.tokenize();
        } catch (ParseException e) {
            logger.error("error tokenizing segments", e);
        }
        return segments;
    }

    private static String addZerosToPrimeFlightIfNeeded(String primeFlightNumber) {
        StringBuilder primeFlightNumberBuilder = new StringBuilder(primeFlightNumber);
        while (primeFlightNumberBuilder.length() < 4) {
            primeFlightNumberBuilder.insert(0, "0");
        }
        return primeFlightNumberBuilder.toString();
    }
}
