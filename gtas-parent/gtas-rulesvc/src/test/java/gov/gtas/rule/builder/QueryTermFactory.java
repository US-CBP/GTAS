package gov.gtas.rule.builder;

import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.querybuilder.mappings.*;

import static gov.gtas.rule.builder.RuleBuilderTestUtils.createQueryTerm;

public class QueryTermFactory {

    public QueryTerm create(IEntityMapping iEntityMapping, CriteriaOperatorEnum criteriaOperatorEnum, String value) {
        return create(iEntityMapping, criteriaOperatorEnum, new String[]{value});
    }

    public QueryTerm create(IEntityMapping iEntityMapping, CriteriaOperatorEnum criteriaOperatorEnum, String[] valueArray) {
        if (isAddressMapping(iEntityMapping)) {
            AddressMapping addressMapping = (AddressMapping) iEntityMapping;
            switch (addressMapping) {
                case CITY:
                case COUNTRY:
                case ADDRESS_LINE_1:
                case ADDRESS_LINE_2:
                case ADDRESS_LINE_3:
                case POSTAL_CODE:
                case STATE:
                    return createQueryTerm(
                            EntityEnum.ADDRESS,
                            addressMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.STRING);

            }
        } else if (isBagMapping(iEntityMapping)) {
            BagMapping bagMapping = (BagMapping) iEntityMapping;
            switch (bagMapping) {
                case AIRLINE:
                case BAG_IDENTIFICATION:
                case DATA_SOURCE:
                case DESTINATION:
                case DESTINATION_AIRPORT:
                case BAG_PAX_OWNER_ID:
                case BAG_FLIGHT_OWNER_ID:
                    return createQueryTerm(
                            EntityEnum.BAG,
                            bagMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.STRING);
            }
        } else if (isBookingDetailMapping(iEntityMapping)) {
            BookingDetailMapping bookingDetailMapping = (BookingDetailMapping) iEntityMapping;
            switch (bookingDetailMapping) {
                case ORIGIN:
                case DESTINATION:
                    return createQueryTerm(
                            EntityEnum.BOOKING_DETAIL,
                            bookingDetailMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.STRING);
            }
        } else if (isCreditCardMapping(iEntityMapping)) {
            CreditCardMapping creditCardMapping = (CreditCardMapping) iEntityMapping;
            switch (creditCardMapping) {
                case ACCOUNT_HOLDER:
                case CREDIT_CARD_NUMBER:
                case CREDIT_CARD_TYPE:
                    return createQueryTerm(
                            EntityEnum.CREDIT_CARD,
                            creditCardMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.STRING);
                case EXPIRATION_DATE:
                    return createQueryTerm(
                            EntityEnum.CREDIT_CARD,
                            creditCardMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.DATE);

            }
        } else if (isDocumentMapping(iEntityMapping)) {
            DocumentMapping documentMapping = (DocumentMapping) iEntityMapping;
            switch (documentMapping) {
                case EXPIRATION_DATE:
                case ISSUANCE_DATE:
                    return createQueryTerm(EntityEnum.DOCUMENT,
                            documentMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.DATE);
                case DOCUMENT_WATCHLIST_CATEGORY:
                    return createQueryTerm(EntityEnum.DOCUMENT,
                            documentMapping,
                            criteriaOperatorEnum,
                            new String[]{"1"},
                            TypeEnum.INTEGER);
                case ISSUANCE_COUNTRY:
                case DOCUMENT_NUMBER:
                case DOCUMENT_TYPE:
                case DOCUMENT_OWNER_ID:
                    return createQueryTerm(EntityEnum.DOCUMENT,
                            documentMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.STRING);
            }
        } else if (isDwellTimeMapping(iEntityMapping)) {
            DwellTimeMapping dwellTimeMapping = (DwellTimeMapping) iEntityMapping;
            switch (dwellTimeMapping) {
                case LOCATION:
                    return createQueryTerm(EntityEnum.DWELL_TIME,
                            dwellTimeMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.STRING);
                case DWELL_TIME:
                    return createQueryTerm(EntityEnum.DWELL_TIME,
                            dwellTimeMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.DOUBLE);
            }
        } else if (isEmailMapping(iEntityMapping)) {
            EmailMapping emailMapping = (EmailMapping) iEntityMapping;
            switch (emailMapping) {
                case EMAIL_ADDRESS:
                case DOMAIN:
                    return createQueryTerm(
                            EntityEnum.EMAIL,
                            emailMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.STRING);
            }
        } else if (isFlightMapping(iEntityMapping)) {
            FlightMapping flightMapping = (FlightMapping) iEntityMapping;
            switch (flightMapping) {
                case AIRPORT_DESTINATION:
                case AIRPORT_ORIGIN:
                case CARRIER:
                case COUNTRY_DESTINATION:
                case COUNTRY_ORIGIN:
                case DIRECTION:
                case FLIGHT_NUMBER:
                    return createQueryTerm(
                            EntityEnum.FLIGHT,
                            flightMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.STRING);
                case ETA:
                case ETD:
                case FLIGHT_DATE:
                    return createQueryTerm(
                            EntityEnum.FLIGHT,
                            flightMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.DATE);
                case IS_OPERATING_FLIGHT:
                case IS_MARKETING_FLIGHT:
                    return createQueryTerm(
                            EntityEnum.FLIGHT,
                            flightMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.BOOLEAN);
            }
        } else if (isFlightPaxMapping(iEntityMapping)) {
            FlightPaxMapping flightPaxMapping = (FlightPaxMapping) iEntityMapping;
            switch (flightPaxMapping) {
                case FIRST_ARRIVAL_PORT:
                case RESIDENCE_COUNTRY:
                case INSTALLATION_ADDRESS:
                case EMBARKATION_AIRPORT:
                case DEBARKATION_AIRPORT:
                case RESERVATION_REF_NUMBER:
                case EMBARKATION_COUNTRY:
                case DEBARKATION_COUNTRY:
                case FLIGHT_PAX_PAX_OWNER_ID:
                case FLIGHT_PAX_FLIGHT_OWNER_ID:
                    return createQueryTerm(
                            EntityEnum.FLIGHT_PAX,
                            flightPaxMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.STRING);
                case BAG_WEIGHT:
                case AVERAGE_BAG_WEIGHT:
                    return createQueryTerm(
                            EntityEnum.FLIGHT_PAX,
                            flightPaxMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.DOUBLE);
                case HEAD_OF_POOL:
                    return createQueryTerm(
                            EntityEnum.FLIGHT_PAX,
                            flightPaxMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.BOOLEAN);
                case BAG_COUNT:
                    return createQueryTerm(
                            EntityEnum.FLIGHT_PAX,
                            flightPaxMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.INTEGER);
            }
        } else if (isFrequentFlyerMapping(iEntityMapping)) {
            FrequentFlyerMapping frequentFlyerMapping = (FrequentFlyerMapping) iEntityMapping;
            switch (frequentFlyerMapping) {
                case CARRIER:
                case FREQUENT_FLYER_NUMBER:
                    return createQueryTerm(
                            EntityEnum.FREQUENT_FLYER,
                            frequentFlyerMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.STRING);
            }
        } else if (isHitsMapping(iEntityMapping)) {
            HitsMapping hitsMapping = (HitsMapping) iEntityMapping;
            switch (hitsMapping) {
                case HAS_RULE_HIT:
                case HAS_WATCHLIST_HIT:
                    return createQueryTerm(
                            EntityEnum.HITS,
                            hitsMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.BOOLEAN);
                case RULE_ID:
                    return createQueryTerm(
                            EntityEnum.HITS,
                            hitsMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.INTEGER);
            }
        } else if (isPassengerMapping(iEntityMapping)) {
            PassengerMapping passengerMapping = (PassengerMapping) iEntityMapping;
            switch (passengerMapping) {
                case CITIZENSHIP_COUNTRY:
                case DEBARKATION:
                case DEBARKATION_COUNTRY:
                case EMBARKATION:
                case EMBARKATION_COUNTRY:
                case GENDER:
                case FIRST_NAME:
                case LAST_NAME:
                case MIDDLE_NAME:
                case RESIDENCY_COUNTRY:
                case SEAT:
                case PASSENGER_TYPE:
                case TRAVEL_FREQUENCY:
                    return createQueryTerm(EntityEnum.PASSENGER,
                            passengerMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.STRING);
                case AGE:
                    return createQueryTerm(EntityEnum.PASSENGER,
                            passengerMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.INTEGER);
                case DOB:
                    return createQueryTerm(EntityEnum.PASSENGER,
                            passengerMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.DATE);
            }
        } else if (isPhoneMapping(iEntityMapping)) {
            PhoneMapping phoneMapping = (PhoneMapping) iEntityMapping;
            switch (phoneMapping) {
                case PHONE_NUMBER:
                    return createQueryTerm(EntityEnum.PHONE,
                            phoneMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.STRING);
            }
        } else if (isPnrMapping(iEntityMapping)) {
            PNRMapping pnrMapping = (PNRMapping) iEntityMapping;
            switch (pnrMapping) {
                case BAG_COUNT:
                case DAYS_BOOKED_BEFORE_TRAVEL:
                case PNR_ID:
                case PASSENGER_COUNT:
                    return createQueryTerm(EntityEnum.PNR,
                            pnrMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.INTEGER);
                case BAG_WEIGHT:
                    return createQueryTerm(EntityEnum.PNR,
                            pnrMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.DOUBLE);
                case BOOKING_DATE:
                case DATE_RECEIVED:
                case DEPARTURE_DATE:
                    return createQueryTerm(EntityEnum.PNR,
                            pnrMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.DATE);
                case CARRIER_CODE:
                case FORM_OF_PAYMENT:
                case ORIGIN_AIRPORT:
                case ORIGIN_COUNTRY:
                case RECORD_LOCATOR:
                case SEAT:
                    return createQueryTerm(EntityEnum.PNR,
                            pnrMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.STRING);
            }
        } else if (isTravelAgencyMapping(iEntityMapping)) {
            TravelAgencyMapping travelAgencyMapping = (TravelAgencyMapping) iEntityMapping;
            switch (travelAgencyMapping) {
                case COUNTRY:
                case IDENTIFIER:
                case LOCATION:
                case NAME:
                case CITY:
                case PHONE:
                    return createQueryTerm(EntityEnum.TRAVEL_AGENCY,
                            travelAgencyMapping,
                            criteriaOperatorEnum,
                            valueArray,
                            TypeEnum.STRING);
            }
        }
        throw new AssertionError("iEntityMapping: " + iEntityMapping.getClass() + ":" +iEntityMapping.getFriendlyName() + "  not implemented");
    }

    private boolean isTravelAgencyMapping(IEntityMapping iEntityMapping) {
        return iEntityMapping.getClass().equals(TravelAgencyMapping.class);
    }

    private boolean isPnrMapping(IEntityMapping iEntityMapping) {
        return iEntityMapping.getClass().equals(PNRMapping.class);
    }

    private boolean isPhoneMapping(IEntityMapping iEntityMapping) {
        return iEntityMapping.getClass().equals(PhoneMapping.class);
    }

    private boolean isHitsMapping(IEntityMapping iEntityMapping) {
        return iEntityMapping.getClass().equals(HitsMapping.class);
    }

    private boolean isFrequentFlyerMapping(IEntityMapping iEntityMapping) {
        return iEntityMapping.getClass().equals(FrequentFlyerMapping.class);
    }

    private boolean isFlightPaxMapping(IEntityMapping iEntityMapping) {
        return iEntityMapping.getClass().equals(FlightPaxMapping.class);
    }

    private boolean isEmailMapping(IEntityMapping iEntityMapping) {
        return iEntityMapping.getClass().equals(EmailMapping.class);
    }

    private boolean isDwellTimeMapping(IEntityMapping iEntityMapping) {
        return iEntityMapping.getClass().equals(DwellTimeMapping.class);
    }

    private boolean isCreditCardMapping(IEntityMapping iEntityMapping) {
        return iEntityMapping.getClass().equals(CreditCardMapping.class);
    }

    private boolean isBookingDetailMapping(IEntityMapping iEntityMapping) {
        return iEntityMapping.getClass().equals(BookingDetailMapping.class);
    }

    private boolean isBagMapping(IEntityMapping iEntityMapping) {
        return iEntityMapping.getClass().equals(BagMapping.class);
    }

    private boolean isAddressMapping(IEntityMapping iEntityMapping) {
        return iEntityMapping.getClass().equals(AddressMapping.class);
    }

    private boolean isDocumentMapping(IEntityMapping iEntityMapping) {
        return iEntityMapping.getClass().equals(DocumentMapping.class);
    }

    private boolean isPassengerMapping(IEntityMapping iEntityMapping) {
        return iEntityMapping.getClass().equals(PassengerMapping.class);
    }

    private boolean isFlightMapping(IEntityMapping iEntityMapping) {
        return iEntityMapping.getClass().equals(FlightMapping.class);
    }
}
