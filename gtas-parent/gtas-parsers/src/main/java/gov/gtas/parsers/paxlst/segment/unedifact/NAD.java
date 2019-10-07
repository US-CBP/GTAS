/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst.segment.unedifact;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

/**
 * <p>
 * NAD: NAME AND ADDRESS
 * <p>
 * To specify a contact responsible for the message content. This may either be
 * an assigned profile or the name of the contact person.
 * <p>
 * Examples:
 * <ul>
 * <li>NAD+MS+ABC9876' Indicates that a profile has been established for this
 * contact with this assigned identification
 * <li>NAD+MS+++WILLIAMS:JANE' Indicates the name of the contact person
 * <li>NAD+FL+++DOE:JOHN:WAYNE+20 MAIN STREET+ANYCITY+VA+10053+USA Passenger on
 * an inbound international flight.
 * </ul>
 */
public class NAD extends Segment {
	public enum NadCode {
		REPORTING_PARTY("MS"), PASSENGER("FL"), CREW_MEMBER("FM"), INTRANSIT_PASSENGER("DDU"), INTRANSIT_CREW_MEMBER(
				"DDT");

		private final String code;

		private NadCode(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}

		private static final Map<String, NadCode> BY_CODE_MAP = new LinkedHashMap<>();
		static {
			for (NadCode rae : NadCode.values()) {
				BY_CODE_MAP.put(rae.code, rae);
			}
		}

		public static NadCode forCode(String code) {
			return BY_CODE_MAP.get(code);
		}
	}

	private NadCode nadCode;

	/** Used only with reporting party */
	private String profileName;

	private String lastName;
	private String firstName;
	private String middleName;
	private String numberAndStreetIdentifier;
	private String city;
	private String countrySubCode;
	private String postalCode;
	private String countryCode;

	public NAD(List<Composite> composites) {
		super(NAD.class.getSimpleName(), composites);
		for (int i = 0; i < numComposites(); i++) {
			Composite c = getComposite(i);

			switch (i) {
			case 0:
				this.nadCode = NadCode.forCode(c.getElement(0));
				break;
			case 3:
				if (c.numElements() == 1) {
					this.profileName = c.getElement(0);
				} else {
					this.lastName = c.getElement(0);
					this.firstName = c.getElement(1);
					this.middleName = c.getElement(2);
				}
				break;
			case 4:
				this.numberAndStreetIdentifier = c.getElement(0);
				break;
			case 5:
				this.city = c.getElement(0);
				break;
			case 6:
				this.countrySubCode = c.getElement(0);
				break;
			case 7:
				this.postalCode = c.getElement(0);
				break;
			case 8:
				this.countryCode = c.getElement(0);
				break;
			}
		}
	}

	public NadCode getNadCode() {
		return nadCode;
	}

	public String getProfileName() {
		return profileName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public String getNumberAndStreetIdentifier() {
		return numberAndStreetIdentifier;
	}

	public String getCity() {
		return city;
	}

	public String getCountrySubCode() {
		return countrySubCode;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getCountryCode() {
		return countryCode;
	}
}
