package gov.gtas.parser.pentaho.util;





/**
 * This class implements methods to compute or extract flight related information
 * from an input a string. 
 *
 */
public class ApisFlightUtil {
	
	
	/**
	 * @param firstAirportInDestCtry
	 *           	The first airport in destination Country
	 * @param nextAirportInDestCtry
	 *            	The next airport in destination country
	 * @Param lastAirportInDestCtry
	 * 				The last airport in destination country
	 * @return int
	 * 				The number of flight legs		
	 *
	 */
	
	public static Long getNumberOfFlightLegs(String firstAirportInDestCtry,String nextAirportInDestCtry,String lastAirportInDestCtry)
	{
		Long numberOfFlightLeg = Long.valueOf(0L);
		
		if( firstAirportInDestCtry!=null && !firstAirportInDestCtry.isEmpty() )
			numberOfFlightLeg = numberOfFlightLeg + 1;
		if( nextAirportInDestCtry!=null && !nextAirportInDestCtry.isEmpty() )
			numberOfFlightLeg = numberOfFlightLeg + 1;
		if( lastAirportInDestCtry!=null && !lastAirportInDestCtry.isEmpty() )
			numberOfFlightLeg = numberOfFlightLeg + 1;
		
		return numberOfFlightLeg;
	}
	
    
    /**
	 * @param flightDesignator
	 *            Flight designator a.k.a carrier code + flight number
	 *            
	 * @return java.lang.String
	 *			The character portion of the flightDesignator input, which is the carrier code.
	 */
    public static String getFlightNumber(String flightDesignator) {       
        
    	String flightNumber = "";
    	char c ;
    	boolean isDigit = false;
    	
    	if(flightDesignator!=null && !flightDesignator.isEmpty())
    	{
    		for(int i=0;i<flightDesignator.length();i++)
    		{
    			
    			c = flightDesignator.charAt(i);
    			isDigit = Character.isDigit(c);
    			if(isDigit)
    			{
    				flightNumber = flightNumber + c;
    			}
    			
    		}
    		
    	}
    	
    	if(flightNumber.isEmpty())
    	{
    		flightNumber = null;
    	}
    	
    	return flightNumber;
    }
    
    /**
	 * @param flightDesignator
	 *            Flight designator a.k.a carrier code + flight number
	 *            
	 * @return ava.lang.String
	 *			The character portion of the flightDesignator input, which is the carrier code.
	 */
    public static String getCarrierCode(String flightDesignator) {       
        
    	String carrierCode = "";
    	char c ;
    	boolean isDigit = false;
    	
    	if(flightDesignator!=null && !flightDesignator.isEmpty())
    	{
    		for(int i=0;i<flightDesignator.length();i++)
    		{
    			
    			c = flightDesignator.charAt(i);
    			isDigit = Character.isDigit(c);
    			if(!isDigit)
    			{
    				carrierCode = carrierCode + c;
    			}
    			
    		}
    		
    	}
    	
    	if(carrierCode.isEmpty())
    	{
    		carrierCode = null;
    	}
    	
    	return carrierCode;
    }
}
