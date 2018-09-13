/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.enumtype;


public enum EntityEnum {
    
        ADDRESS ("ADDRESS", "Address", "a", ".addresses"),
        BOOKING_DETAIL ("BOOKING DETAIL", "BookingDetail", "bl", ".bookingDetails"),
        CREDIT_CARD ("CREDIT CARD", "CreditCard", "cc", ".creditCards"),
        DOCUMENT ("DOCUMENT", "Document", "d", ".documents"),
        EMAIL ("EMAIL", "Email", "e", ".emails"),
        FLIGHT ("FLIGHT", "Flight", "f", ".flights"),
       // FLIGHT_LEG ("FLIGHT LEG", "FlightLeg", "fl", ".flightLegs"),
        FREQUENT_FLYER ("FREQUENT FLYER", "FrequentFlyer", "ff", ".frequentFlyers"),
        HITS ("HITS", "HitsSummary", "h", ""),
        PASSENGER("PASSENGER", "Passenger", "p", ".passengers"),
        PHONE ("PHONE", "Phone", "ph", ".phones"),
        PNR ("PNR", "Pnr", "pnr", ".pnrs"),
        TRAVEL_AGENCY ("TRAVEL AGENCY", "Agency", "ag", ".agencies"),
		DWELL_TIME ("DWELL TIME", "DwellTime", "dwell", ".dwellTimes"),
		BAG("BAG","Bag","bag",".bags"), 
		FLIGHT_PAX("FLIGHT PAX", "FlightPax", "flightpax",".flightPaxList");
        
        private String friendlyName;
        private String entityName;
        private String alias;
        private String entityReference;
        
        private EntityEnum(String friendlyName, String entityName, String alias, String entityReference) {
            this.friendlyName = friendlyName;
            this.entityName = entityName;
            this.alias = alias;
            this.entityReference = entityReference;
            
        }

        public String getFriendlyName() {
            return friendlyName;
        }

        public String getEntityName() {
            return entityName;
        }

        public String getAlias() {
            return alias;
        }
        
        public String getEntityReference() {
            return entityReference;
        }

        public static EntityEnum getEnum(String value) {
            
            for (EntityEnum entityEnum : EntityEnum.values()) {
                 if(entityEnum.getEntityName().equalsIgnoreCase(value)) {
                     return entityEnum;
                 }
             }
            
            throw new IllegalArgumentException();
        }
}
