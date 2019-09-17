package gov.gtas.parsers.pnrgov.segment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import gov.gtas.parsers.pnrgov.enums.SSRDocoType;

public class SSRDoco {

	 private Logger logger = LoggerFactory.getLogger(SSRDocs.class);
	    private SSR ssr;
	    private static final String DOC_DATE_FORMAT = "ddMMMyy";

	    private String placeOfBirth;
	    private SSRDocoType ssrDocoType;
	    private String visaDocNumber;
	    private String visaDocPlaceOfIssuance;
	    private String visaDocIssuanceDate;
	    private String visaApplicableCountry;
	    private boolean infantIndicator;
	    private String nameInformation;
	    
	    

	    public SSRDoco(SSR ssr) {
	        this.ssr = ssr;
	        if (ssr.getFreeText() != null) {
	            populateSSRDoco();
	        } else {
	            logger.warn("NO SSR FREE TEXT. SSR DOCS NOT POPULATING!!");
	        }
	    }

	    private void populateSSRDoco() {
	        Splitter splitter = Splitter.on('/').trimResults();
	        Iterable<String> ssrTokens = splitter.split(ssr.getFreeText());
	        String[] freeTextInArrayForm = Iterables.toArray(ssrTokens, String.class);
	        
	        //Follow the Passenger Travel Documentation Information AIRIMP32 pg 150 grammar. Follows the same path split on the "/".
	        /*
	        * Use following order, starting at 1(this is predicated by a /):
	        * 1 : Place of Birth
	        * 2 : Travel Document Type ICAO 9303 = V
	        * 3 : Visa Document Number
	        * 4 : Visa Document Place of Issue
	        * 5 : Visa Document Issue Date DDMMMYY
	        * 6 : Country/State for which the Visa is applicable
	        * 7 : Infant Indicator I = Infant (if an infant not occupying seat)
	        * 8 : Name Information (If the other travel related information does not apply to all passengers in
	        * the PNR, follow with a hypehn and passenger name field for whome the elemnet applies.)
	        * 8(i) : Hyphen (-)
	        * 8(ii)	: PNR Associated Name Including number in party       
	        */

	        String ssrIsOffsetByOne = freeTextInArrayForm.length >= 3 ? freeTextInArrayForm[2] : "";
	        boolean offsetByOne = SSRDocoType.fromString(ssrIsOffsetByOne).isPresent();
	        for (int i = 0; i < freeTextInArrayForm.length; i++) {
	            String subSegment = freeTextInArrayForm[i];
	            if (offsetByOne && i < freeTextInArrayForm.length - 1) {
	                subSegment = freeTextInArrayForm[i + 1];
	            }
	            switch (i) {
	                case 0 :
	                    /*Always null. For example /P/USA/965155744/USA/24AUG53/F/22OCT24/FLEISHMAN/BOBBY/ALLEEN' will parse to
	                    an array containing  { "", P, USA, 965155744, USA, 24AUG53, F, 22OCT24, FLEISHMAN, BOBBY, ALLEEN }
	                    All legal messages are expected to parse the same way.
	                    */
	                    break;
	                case 1:
	                	this.placeOfBirth = subSegment;
	                    break;
	                case 2:
                        SSRDocoType.fromString(subSegment).ifPresent(ssrDocs -> this.ssrDocoType = ssrDocs);
                        if (this.ssrDocoType == null) {
                            this.ssrDocoType = SSRDocoType.NOT_PROVIDED;
                        }
	                    break;
	                case 3:
	                	this.visaDocNumber = subSegment;
	                    break;
	                case 4:
	                	this.visaDocPlaceOfIssuance = subSegment;
	                    break;
	                case 5:
	                	this.visaDocIssuanceDate = subSegment;
	                    break;
	                case 6:
	                	this.visaApplicableCountry = subSegment;
	                    break;
	                case 7:
	                	this.infantIndicator = "I".equalsIgnoreCase(subSegment);
	                    break;
	                case 8:
	                	this.nameInformation = subSegment;
	                    break;
	                default:
	                    logger.warn("FIELD NOT IMPLEMENTED FOR SSR DOCS. CHECK IMPLEMENTATION FOR ARRAY AT SPOT " + i);
	                    break;
	            }
	        }
	    }


	    public Logger getLogger() {
			return logger;
		}


		public void setLogger(Logger logger) {
			this.logger = logger;
		}


		public String getPlaceOfBirth() {
			return placeOfBirth;
		}


		public void setPlaceOfBirth(String placeOfBirth) {
			this.placeOfBirth = placeOfBirth;
		}


		public SSRDocoType getSsrDocoType() {
			return ssrDocoType;
		}


		public void setSsrDocoType(SSRDocoType ssrDocoType) {
			this.ssrDocoType = ssrDocoType;
		}


		public String getVisaDocNumber() {
			return visaDocNumber;
		}


		public void setVisaDocNumber(String visaDocNumber) {
			this.visaDocNumber = visaDocNumber;
		}


		public String getVisaDocPlaceOfIssuance() {
			return visaDocPlaceOfIssuance;
		}


		public void setVisaDocPlaceOfIssuance(String visaDocPlaceOfIssuance) {
			this.visaDocPlaceOfIssuance = visaDocPlaceOfIssuance;
		}


		public String getVisaDocIssuanceDate() {
			return visaDocIssuanceDate;
		}


		public void setVisaDocIssuanceDate(String visaDocIssuanceDate) {
			this.visaDocIssuanceDate = visaDocIssuanceDate;
		}


		public String getVisaApplicableCountry() {
			return visaApplicableCountry;
		}


		public void setVisaApplicableCountry(String visaApplicableCountry) {
			this.visaApplicableCountry = visaApplicableCountry;
		}


		public boolean getInfantIndicator() {
			return infantIndicator;
		}


		public void setInfantIndicator(boolean infantIndicator) {
			this.infantIndicator = infantIndicator;
		}


		public String getNameInformation() {
			return nameInformation;
		}


		public void setNameInformation(String nameInformation) {
			this.nameInformation = nameInformation;
		}


		public String getFreeText() {
	        return ssr.getFreeText();
	    }

	    public void setFreeText(String ssrText) {
	        ssr.setFreeText(ssrText);
	    }

	    public SSR getSsr() {
	        return ssr;
	    }

	    public void setSsr(SSR ssr) {
	        this.ssr = ssr;
	    }

	    public String getSupplementaryIdentifier() {
	        return "SSR";
	    }

	    public String getSpecialServiceRequirementCode() {
	        return SSR.DOCS;
	    }
	
}
