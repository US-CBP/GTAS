package gov.gtas.parser.pentaho.util;

import gov.gtas.parser.pentaho.bean.UNA;

public final class UNAParser {
	
	
	private static final String DEFAULT_SERVICE_STRING_ADVICE = ":+.?*'";
	private static final String SERVICE_STRING_ADVICE_ID = "UNA";
	
	
	public static String getServiceString(String unaSegment)
	{
		String serviceString = null;
		
		if(unaSegment == null || unaSegment.isEmpty())
			return DEFAULT_SERVICE_STRING_ADVICE;
		else
		{
			String[] serviceStringAdviceArray = unaSegment.split(SERVICE_STRING_ADVICE_ID);
			
			if(serviceStringAdviceArray==null || serviceStringAdviceArray.length != 2)
			{
				return DEFAULT_SERVICE_STRING_ADVICE; 
			}
			else if (serviceStringAdviceArray.length == 2)
			{
				String tempServAdvString = serviceStringAdviceArray[1];
				
				if(tempServAdvString!=null && !tempServAdvString.isEmpty() && tempServAdvString.trim().length()==6)
				{
					serviceString = tempServAdvString.trim();
				}
				else
				{
					return DEFAULT_SERVICE_STRING_ADVICE; 
				}
				
			}
			
		}
		return serviceString;
		
	}
	
	public static UNA getUNA(String unaSegment )
	{
		UNA una = new UNA();
		
		String serviceStringAdvice = getServiceString(unaSegment);
		
		if(serviceStringAdvice!=null && serviceStringAdvice.trim().length() == 6)
		{
			una.setServiceStringAdvice(serviceStringAdvice);
			una.setComponentDataElementSep(Character.toString(serviceStringAdvice.charAt(0)));
			una.setDataElementSep(Character.toString(serviceStringAdvice.charAt(1)));
			una.setDecimalMark(Character.toString(serviceStringAdvice.charAt(2)));
			una.setReleaseCharacter(Character.toString(serviceStringAdvice.charAt(3)));
			una.setRepetitionSep(Character.toString(serviceStringAdvice.charAt(4)));
			una.setSegmentTerminator(Character.toString(serviceStringAdvice.charAt(5)));
						
			
		}
		
		return una;
	}

}
