/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst.segment.usedifact;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.util.ParseUtils;

public class PDT extends Segment {
    private static final String DATE_FORMAT = "yyMMdd";
    
    public enum DocType {
        PASSPORT,
        VISA,
        ALIEN_REGISTRATION
    }
    public enum PersonStatus {
        PAX,
        CREW,
        IN_TRANSIT
    }
    private DocType documentType;
    private String documentNumber;
    private Date c_dateOfExpiration;
    private String iataOriginatingCountry;
    private String lastName;
    private String firstName;
    private String c_middleNameOrInitial;
    private Date dob;
    private String gender;
    private PersonStatus personStatus;
    private String iataEmbarkationCountry;
    private String iataEmbarkationAirport;
    private String iataDebarkationCountry;    
    private String iataDebarkationAirport;
    
    public PDT(List<Composite> composites) throws ParseException {
        super(PDT.class.getSimpleName(), composites);
        for (int i = 0; i < numComposites(); i++) {
            Composite c = getComposite(i);
            switch (i) {
            case 0:
                char code = c.getElement(0).charAt(0);
                switch (code) {
                case 'V':
                    this.documentType = DocType.VISA;
                    break;
                case 'A':
                    this.documentType = DocType.ALIEN_REGISTRATION;
                    break;
                default:
                    this.documentType = DocType.PASSPORT;
                    break;
                }
            
                String[] tmp = c.getElement(0).split("/");
                if (tmp.length >= 2) {
                    this.documentNumber = tmp[1];
                }
                
                if (StringUtils.isNotBlank(c.getElement(1))) {
                    this.c_dateOfExpiration = ParseUtils.parseDateTime(c.getElement(1), DATE_FORMAT);
                }
                
                this.iataOriginatingCountry = c.getElement(2);
                break;
                
            case 1:
                this.lastName = c.getElement(0);
                this.firstName = c.getElement(1);
                this.c_middleNameOrInitial = c.getElement(2);
                
                if (StringUtils.isNotBlank(c.getElement(3))) {
                	this.dob = ParseUtils.parseDateTime(c.getElement(3), DATE_FORMAT);
            	}
                this.gender = c.getElement(4);
                break;
            
            case 2:
                switch (c.getElement(0)) {
                case "PAX":
                    this.personStatus = PersonStatus.PAX;
                    break;
                case "CRW":
                    this.personStatus = PersonStatus.CREW;
                    break;
                case "ITI":
                    this.personStatus = PersonStatus.IN_TRANSIT;
                    break;
                default:
                    logger.error("unknown person type: " + c.getElement(0));
                    return;
                }
                break;
                
            case 3:
                String emb = c.getElement(0);
                this.iataEmbarkationCountry = emb.substring(0, 2);
                this.iataEmbarkationAirport = emb.substring(2, emb.length());
                String deb = c.getElement(1);
                this.iataDebarkationCountry = deb.substring(0, 2);
                this.iataDebarkationAirport = deb.substring(2, deb.length());
                break;
            }
        }
    }

    public DocType getDocumentType() {
        return documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public Date getC_dateOfExpiration() {
        return c_dateOfExpiration;
    }

    public String getIataOriginatingCountry() {
        return iataOriginatingCountry;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getC_middleNameOrInitial() {
        return c_middleNameOrInitial;
    }

    public Date getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public PersonStatus getPersonStatus() {
        return personStatus;
    }

    public String getIataEmbarkationCountry() {
        return iataEmbarkationCountry;
    }

    public String getIataEmbarkationAirport() {
        return iataEmbarkationAirport;
    }

    public String getIataDebarkationCountry() {
        return iataDebarkationCountry;
    }

    public String getIataDebarkationAirport() {
        return iataDebarkationAirport;
    }
}
