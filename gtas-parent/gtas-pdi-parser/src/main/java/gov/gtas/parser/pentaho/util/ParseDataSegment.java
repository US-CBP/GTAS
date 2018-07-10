package gov.gtas.parser.pentaho.util;

import java.text.ParseException;
import java.util.regex.Pattern;

/**
 * 
 *
 */
public class ParseDataSegment {

	/**
	 * @param dataSegment
	 *            APIS data segment
	 * @param delimiter
	 *            the delimiter required to parse the data segment
	 * @return java.lang.String
	 * 			value of the APIS field extracted from the data segment
	 *
	 */
	
	public static String extractFieldData(String dataSegment, String delimiter) {
		
		String fieldData = null;

		if (dataSegment != null && !dataSegment.isEmpty()) {
			int index = dataSegment.lastIndexOf(delimiter);

			if (index != -1 && (index + 1 < dataSegment.length())) {
				fieldData = dataSegment.substring(index + 1);

			}

		}

		return fieldData;

	}// end of extractFieldData


	
	
	
	/**
	 * @param dataSegment
	 *            APIS data segment
	 * @param delimiter
	 *            the delimiter required to parse the data segment
	 * @return java.lang.String
	 * 			value of the APIS field extracted from the data segment
	 *
	 */
	
	public static String extractDateFieldData(String dataSegment, String delimiter, String currentFormat, String expectedFormat) throws ParseException {
		
		String fieldData = null;

		if (dataSegment != null && !dataSegment.isEmpty()) {
			int index = dataSegment.lastIndexOf(delimiter);

			if (index != -1 && (index + 1 < dataSegment.length())) {
				fieldData = dataSegment.substring(index + 1);
			}
			
			if(fieldData!=null)
			{
				fieldData = ApisStringParser.parseDateTimeAsStr(fieldData, currentFormat, expectedFormat); 
			}

		}

		return fieldData;

	}// end of extractFieldData

	/**
	 * @param dataSegment
	 *            APIS scheduled departure or arrival datetime data segment
	 * @param delimiter
	 *            the delimiter required to parse the data segment
	 * @return java.lang.String
	 * 				the arrival or departure date as string
	 *
	 */
	
	public static String extractFlightDateField(String flightDataSegment, String comDataElementSep) throws ParseException {
		
		String flightDateTime = null;

		if (flightDataSegment != null && !flightDataSegment.isEmpty()) {
			
			flightDataSegment = flightDataSegment.trim();
			int firstIndex = flightDataSegment.indexOf(comDataElementSep);
			
			
			if(firstIndex!= -1 && firstIndex+7 <= flightDataSegment.length() )
			{
					//use only yymmdd
					String flightDateTimeStr = flightDataSegment.substring(firstIndex+1,firstIndex+7);
					if(flightDateTimeStr.trim().length() == 6)
						flightDateTime = ApisStringParser.parseDateTimeAsStr(flightDateTimeStr,Constants.DATE_FORMAT_YYMMDD,Constants.DATE_FORMAT_YYYY_MM_DD);
				}
				
		}

		return flightDateTime;

	}// end of extractFieldData

	/**
	 * @param dataSegment
	 *            APIS scheduled departure or arrival datetime data segment
	 * @param delimiter
	 *            the delimiter required to parse the data segment
	 * @return java.lang.String
	 * 				the arrival or departure date time as string
	 *
	 */
	
	public static String extractFlightDateTimeField(String flightDataSegment, String comDataElementSep) throws ParseException {
		
		String flightDateTime = null;

		if (flightDataSegment != null && !flightDataSegment.isEmpty()) {
			
			flightDataSegment = flightDataSegment.trim();
			int firstIndex = flightDataSegment.indexOf(comDataElementSep);
			int lastIndex = flightDataSegment.lastIndexOf(comDataElementSep);
			
			if(firstIndex!= -1 && lastIndex!=-1 && firstIndex!=lastIndex)
			{
				
					String timePeriodCode = flightDataSegment.substring(lastIndex+1);
					if(timePeriodCode!=null && timePeriodCode.trim().equals(Constants.YYMMDDHHMM_CODE))
					{
						String flightDateTimeStr = flightDataSegment.substring(firstIndex+1, lastIndex);
						flightDateTime =  ApisStringParser.parseDateTimeAsStr(flightDateTimeStr,Constants.DATE_FORMAT_YYMMDDHHMM,Constants.DATE_FORMAT_YYYY_MM_DD_HH_MM);
					}
					
				
				
			}

		
		}

		return flightDateTime;

	}// end of extractFieldData

	
	/**
	 * @param dataSegment
	 *            APIS data segment
	 * @param delimiter
	 *            the delimiter required to parse the data segment
	 * @return java.lang.String
	 * 			value of the APIS field extracted from the middle of the data segment
	 * 
	 *			Example: dataSegment = DOC+V+20365346,  the returned fieldData = V
	 */
	
	public static String extractMiddleField(String dataSegment, String delimiter) {
		
		String fieldData = null;

		if (dataSegment != null && !dataSegment.isEmpty()) {
			int firstIndex = dataSegment.indexOf(delimiter);
			int lastIndex = dataSegment.lastIndexOf(delimiter);

			if(firstIndex!=-1 && lastIndex!=-1 && firstIndex < lastIndex-1)
				fieldData = dataSegment.substring(firstIndex+1,lastIndex);
				if(fieldData!=null)
					fieldData = fieldData.trim();
			}



		return fieldData;

	}// end of extractFieldData

	
	/**
	 * @param communicationSegment
	 *            	The APIS COM segment string
	 * @param dataElementSeparator
	 *            	The APIS data element separator character
	 * @param componentDataElementSep
	 *            	The APIS component data element separator character 
	 *                      
	 * @return java.lang.String array
	 * 				A string array of size 3 as follows
	 * 				[0] =  	Telephone number, null if the data is not present
	 * 				[1] = 	Fax number, null if the data is not present
	 * 				[2] = 	Email address, null if the data is not present
	 *
	 */
    public static String[] getContactInfo (String communicationSegment, String dataElementSeparator, String componentDataElementSep)
    {
    	String[] contactInfo = new String[3];
    	
    	if(communicationSegment!=null && !communicationSegment.isEmpty() && communicationSegment.trim().startsWith("COM"))
    	{
    		String[] comSegArray = communicationSegment.split(Pattern.quote(dataElementSeparator));
    		
    		if(comSegArray!=null && comSegArray.length > 0)
    		{
    			for(int i=0; i < comSegArray.length;i++)
    			{
    				if(comSegArray[i]!=null)
    				{
    					if(comSegArray[i].contains("TE"))
    					{
    						contactInfo[0] = ApisStringParser.getSubstrFistToDelimiter(comSegArray[i],componentDataElementSep);
    					}
    					else if(comSegArray[i].contains("FX"))
    					{
    						contactInfo[1] = ApisStringParser.getSubstrFistToDelimiter(comSegArray[i],componentDataElementSep);
    					}
    					else if(comSegArray[i].contains("EM"))
    					{
    						contactInfo[2] = ApisStringParser.getSubstrFistToDelimiter(comSegArray[i],componentDataElementSep);
    					}
    				}
    			}
    		}
    	}
    	
    	return contactInfo;
    }
    

    
}// end of ParseDataSegment
