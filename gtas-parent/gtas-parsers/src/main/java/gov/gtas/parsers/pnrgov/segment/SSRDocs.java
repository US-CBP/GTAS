package gov.gtas.parsers.pnrgov.segment;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import gov.gtas.parsers.pnrgov.enums.SSRDocsGender;
import gov.gtas.parsers.pnrgov.enums.SSRDocsType;
import gov.gtas.parsers.util.ParseUtils;
import gov.gtas.parsers.vo.DocumentVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSRDocs {

	private Logger logger = LoggerFactory.getLogger(SSRDocs.class);
	private SSR ssr;
	private static final String DOC_DATE_FORMAT = "ddMMMyy";

	private SSRDocsType ssrDocsType;
	private String issueingCountryState;
	private String travelDocNumber;
	private String nationality;
	private String dob;
	private SSRDocsGender gender;
	private String travelDocumentExpirationDate;
	private String surname;
	private String firstGivenName;
	private String secondGivenName;
	private Boolean holder;
	private String nameInformationLine;

	public SSRDocs(SSR ssr) {
		this.ssr = ssr;
		if (ssr.getFreeText() != null) {
			populateSSRDocs();
		} else {
			logger.warn("NO SSR FREE TEXT. SSR DOCS NOT POPULATING!!");
		}
	}

	private void populateSSRDocs() {
		Splitter splitter = Splitter.on('/').trimResults();
		Iterable<String> ssrTokens = splitter.split(ssr.getFreeText());
		String[] freeTextInArrayForm = Iterables.toArray(ssrTokens, String.class);
		// Follow the Passenger Travel Documentation Information AIRIMP32 pg 146
		// grammar. Follows the same path split on the "/".
		/*
		 * Use following order, starting at 1(this is predicated by a /): 1 : Travel
		 * Document Type 2 : Travel Document Issuing Country/State 3 : Travel Document
		 * Number 4 : Passenger Nationality 5 : Date of Birth DDMMMYY 6 : Gender of
		 * passenger 7 : Travel Document Expiry Date 8 : Travel Document Surname 9 :
		 * Travel Document First Given Name 10 : Travel Document Second Given Name 11:
		 * If multi-passenger passport and this SSR is the primary passport include the
		 * letter H 12: Name information - e.g. follow with a hyphen.
		 */

		// A common issue with SSR DOCS is to have the travel document offset by 1. Flag
		// if this is the case.
		// It isn't to spec to parse these documents but we will attempt to permit these
		// by taking advantage
		// of the ssr doc type code being different than the country code.
		String ssrIsOffsetByOne = freeTextInArrayForm.length >= 3 ? freeTextInArrayForm[2] : "";
		boolean offsetByOne = SSRDocsType.fromString(ssrIsOffsetByOne).isPresent();
		for (int i = 0; i < freeTextInArrayForm.length; i++) {
			String subSegment = freeTextInArrayForm[i];
			if (offsetByOne && i < freeTextInArrayForm.length - 1) {
				subSegment = freeTextInArrayForm[i + 1];
			}
			switch (i) {
			case 0:
				/*
				 * Always null. For example
				 * /P/USA/965155744/USA/24AUG53/F/22OCT24/FLEISHMAN/BOBBY/ALLEEN' will parse to
				 * an array containing { "", P, USA, 965155744, USA, 24AUG53, F, 22OCT24,
				 * FLEISHMAN, BOBBY, ALLEEN } All legal messages are expected to parse the same
				 * way.
				 */
				break;
			case 1:
				SSRDocsType.fromString(subSegment).ifPresent(ssrDocs -> this.ssrDocsType = ssrDocs);
				if (this.ssrDocsType == null) {
					this.ssrDocsType = SSRDocsType.NOT_PROVIDED;
				}
				break;
			case 2:
				this.issueingCountryState = subSegment;
				break;
			case 3:
				this.travelDocNumber = subSegment;
				break;
			case 4:
				this.nationality = subSegment;
				break;
			case 5:
				this.dob = subSegment;
				break;
			case 6:
				SSRDocsGender.fromString(subSegment).ifPresent(ssrGender -> this.gender = ssrGender);
				break;
			case 7:
				this.travelDocumentExpirationDate = subSegment;
				break;
			case 8:
				this.surname = subSegment;
				break;
			case 9:
				this.firstGivenName = subSegment;
				break;
			case 10:
				this.secondGivenName = subSegment;
				break;
			case 11:
				this.holder = "H".equalsIgnoreCase(subSegment);
				break;
			case 12:
				this.nameInformationLine = subSegment;
				break;
			default:
				logger.warn("FIELD NOT IMPLEMENTED FOR SSR DOCS. CHECK IMPLEMENTATION FOR ARRAY AT SPOT " + i);
				break;
			}
		}
	}

	public DocumentVo toDocumentVo() {
		DocumentVo documentVo = new DocumentVo();
		if (ssrDocsType != null) {
			documentVo.setDocumentType(ssrDocsType.toString());
		}
		documentVo.setDocumentNumber(travelDocNumber);
		documentVo.setIssuanceCountry(issueingCountryState);
		try {
			if (StringUtils.isNotBlank(travelDocumentExpirationDate)) {
				documentVo.setExpirationDate(ParseUtils.parseDateTime(travelDocumentExpirationDate, DOC_DATE_FORMAT));
			}
		} catch (Exception ignored) {
			logger.warn("failed to parse expiration date on SSR DOCS");
		}

		return documentVo;
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

	public SSRDocsType getSsrDocsType() {
		return ssrDocsType;
	}

	public String getIssueingCountryState() {
		return issueingCountryState;
	}

	public String getTravelDocNumber() {
		return travelDocNumber;
	}

	public String getNationality() {
		return nationality;
	}

	public String getDob() {
		return dob;
	}

	public SSRDocsGender getGender() {
		return gender;
	}

	public String getTravelDocumentExpirationDate() {
		return travelDocumentExpirationDate;
	}

	public String getSurname() {
		return surname;
	}

	public String getFirstGivenName() {
		return firstGivenName;
	}

	public String getSecondGivenName() {
		return secondGivenName;
	}

	public Boolean getHolder() {
		return holder;
	}

	public String getNameInformationLine() {
		return nameInformationLine;
	}
}
