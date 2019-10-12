package gov.gtas.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ApisGeneratorUtil gets called from controller classes to verify the UGANDA specific files 
 * and convert them into APIS files and upload to the designated directory to process.
 * @see gov.gtas.controller.UploadController
 *
 */
public class ApisGeneratorUtil {
	private static final Logger logger = LoggerFactory.getLogger(ApisGeneratorUtil.class);
	public static final String STR_FLIGHT = "FLIGHT:";
	public static final String STR_DATE = "DATE:";
	public static final String STR_EMBARK = "PT.OF EMBARKATION:";
	public static final String STR_DEST = "PT.OF DEST:";
	private static String newline=System.getProperty("line.separator");

    public static StringBuilder processAndConvertFile(File fin) throws IOException {
    	String flightregex="^FLIGHT: (.*)\\s+DATE: (.*)$";
    	String orginRegex=".*PT.OF EMBARKATION: (.*)\\s+PT.OF DEST: (.*)$";
    	String paxRegex ="^[0-9][0-9][0-9] (.*)$";
        Pattern pattern = Pattern.compile(flightregex); 
        Pattern patternorigin = Pattern.compile(orginRegex); 
        Pattern patternpax = Pattern.compile(paxRegex); 
    	String line = null;
		StringBuilder sb=new StringBuilder();
		String carrier = "";
		String flight_num ="";
		String origin = "";
		String dest = "";
		String flight_date ="";
		String mod_f_Date="";
		boolean enhancedManifest=false;

		try (FileInputStream fis = new FileInputStream(fin); BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {

			while ((line = br.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				Matcher matcherorigen = patternorigin.matcher(line);
				Matcher matcherpax = patternpax.matcher(line);
				while (matcher.find()) {
					String header = matcher.group();
					header = header.replaceAll("\\s+", "");
					carrier = header.substring(header.indexOf(ApisGeneratorUtil.STR_FLIGHT) + ApisGeneratorUtil.STR_FLIGHT.length(), header.indexOf(ApisGeneratorUtil.STR_DATE) - 3);
					flight_num = header.substring(header.indexOf(ApisGeneratorUtil.STR_FLIGHT) + ApisGeneratorUtil.STR_FLIGHT.length() + 2, header.indexOf(ApisGeneratorUtil.STR_DATE));
					flight_date = header.substring(header.indexOf(ApisGeneratorUtil.STR_DATE) + ApisGeneratorUtil.STR_DATE.length(), header.length());
					mod_f_Date = getDateInYYMMDD(flight_date);
					prepareHeader(carrier, flight_num, sb);
					enhancedManifest = true;
				}
				while (matcherorigen.find()) {
					String origenDetails = matcherorigen.group();
					origin = origenDetails.substring(origenDetails.indexOf(ApisGeneratorUtil.STR_EMBARK) + ApisGeneratorUtil.STR_EMBARK.length(), origenDetails.indexOf(ApisGeneratorUtil.STR_DEST));
					dest = origenDetails.substring(origenDetails.indexOf(ApisGeneratorUtil.STR_DEST) + ApisGeneratorUtil.STR_DEST.length(), origenDetails.length());
					if (StringUtils.isNotBlank(origin) && StringUtils.isNotBlank(dest)) {
						origin = origin.replaceAll("\\s+", "");
						dest = dest.replaceAll("\\s+", "");

					}
					prepareFlightSegment(sb, carrier, flight_num, origin, mod_f_Date, dest);
				}
				while (matcherpax.find()) {
					String paxDetails = matcherpax.group();
					String[] tokens = paxDetails.split("/");
					if (tokens != null && tokens.length > 11) {
						buildPaxList(sb, paxDetails, origin, dest);
					}
				}
			}
		}

    	if(enhancedManifest){
    		buildApisFooter(sb);
    	}

    	return sb;
    }
    
	private static void buildApisHeader(StringBuilder sb,String flightnum){
		sb.append("UNA:+.? '"+newline);
		sb.append("UNB+UNOA:4+SDT:ZZ+JMAPIS:ZZ+140211:1800+1402111800++APIS'");
		sb.append("UNG+PAXLST+TEST DATA:ZZ+JMAPIS:ZZ+140201:1800+1402111800+UN+D:02B'"+newline);
		sb.append("UNH+1402111800+PAXLST:D:02B:UN:IATA+");
		sb.append(flightnum+"+10:F'"+newline);
		sb.append("BGM+745'"+newline);
	}
	
	private static void prepareFlightSegment(StringBuilder sb,String carrier,String flight_num, String origin, String flight_date, String dest){
		sb.append("TDT+20+");
		sb.append(carrier+flight_num+"'"+newline);
    	sb.append("LOC+125+"+origin+"'");
		sb.append("DTM+189:"+flight_date+"1340:201'"+newline);
		sb.append("LOC+87+"+dest+"'"+newline);
		sb.append("DTM+232:"+flight_date+"1700:201'"+newline);
	}
	
	private static void buildPaxList(StringBuilder sb,String paxDetail,String origin,String dest){
    	String lastName="";
		String firstName="";
		String gender="";
		String embark="";
		String debark="";
		String docnum="000000000";
		String bagno="";
		String weight="";
		String bagtag="";
		String seat="";
		String mName="";
    	String[] tokens=paxDetail.split("/");
     	if(tokens != null && tokens.length > 11){
    		//LNAME/FNAME/TYPE/SEAT/BAGS/WEIGHT/BAGTAG/TKT#/IN.FLT/TR.ORG/OT.FLT/F.DST/SPECIAL
    		if(StringUtils.isNotBlank(tokens[0]) && (tokens[0].indexOf(" ") >-1)){
    			lastName= tokens[0].substring(tokens[0].indexOf(" "), tokens[0].length());
    			lastName=lastName.replaceAll("\\.+", "");
    		}
    		if(StringUtils.isNotBlank(tokens[1])){
    			firstName= tokens[1];
    			firstName=firstName.replaceAll("\\.+", "");
    			//firstName=firstName.replaceAll("\\s+", "");
    		}
    		if(StringUtils.isBlank(firstName)){
    			firstName="NONAME";
    		}
    		if(firstName.indexOf(" ") > -1){
    			mName=getMiddleNameFromName(firstName);
    			firstName=firstName.substring(0, firstName.indexOf(" "));
    		}
    		if(StringUtils.isNotBlank(tokens[2])){
    			gender=tokens[2].replaceAll("\\.+", "");
    		}
    		if(StringUtils.isBlank(gender)){
    			gender="N";
    		}
    		if(StringUtils.isNotBlank(tokens[3])){
    			seat=tokens[3].replaceAll("\\.+", "");
    		}
    		if(StringUtils.isNotBlank(tokens[4])){
    			bagno=tokens[4].replaceAll("\\.+", "");
    		}
    		if(StringUtils.isNotBlank(tokens[5])){
    			weight=tokens[5].replaceAll("\\.+", "");
    		}
    		if(StringUtils.isNotBlank(tokens[6])){
    			bagtag=tokens[6].replaceAll("\\.+", "");
    		}
    		if(StringUtils.isNotBlank(tokens[9])){
    			embark=tokens[9].replaceAll("\\.+", "");
    		}
    		if(StringUtils.isNotBlank(tokens[11])){
    			debark=tokens[11].replaceAll("\\.+", "");
    		}
       		if(StringUtils.isBlank(embark) || (StringUtils.isNotBlank(embark) && embark.length() < 3)){
       			embark=origin;
    		}
       		if(StringUtils.isBlank(debark)|| (StringUtils.isNotBlank(debark) && debark.length() < 3)){
       			debark=dest;
    		}
    	}
		sb.append("NAD+FL+++"+lastName+":"+firstName+":"+mName+"'"+newline);
		sb.append("ATT+2++"+gender+"'"+newline);
		sb.append("DTM+329:541016'"+newline);
		//FTX+BAG+++ZZ012345:3'
		sb.append("FTX+BAG+"+weight+"++"+bagtag+":"+bagno+"'"+newline);
		sb.append("LOC+178+"+embark+"'"+newline);
		sb.append("LOC+179+"+debark+"'"+newline);
		sb.append("NAT+2+UGA'"+newline);
		sb.append("RFF+AVF:"+"QXGYLO'"+newline);
		if(StringUtils.isNotBlank(seat)){
			sb.append("RFF+SEA:"+seat+"'"+newline);
		}
		sb.append("DOC+P:110:111+"+docnum+"'"+newline);
		sb.append("DTM+36:180221'"+newline);
		sb.append("LOC+91+UGA'"+newline);
	}

	public static boolean isUgandaManifest(File fin) {
		if (fin == null) {
			return false;
		}

		String flightregex = "^FLIGHT: (.*)\\s+DATE: (.*)$";
		Pattern pattern = Pattern.compile(flightregex);
		FileInputStream fis = null;
		BufferedReader br = null;
		String line;
		try {
			fis = new FileInputStream(fin);
			br = new BufferedReader(new InputStreamReader(fis));
			while ((line = br.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					return true;
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("Error in APIS generator:", e);
		} catch (IOException e) {
			logger.error("Error in APIS generator", e);
		} finally {
			fin.deleteOnExit();
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error("Error in APIS generator.", e);
				}
			}

			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error("Error in APIS generator.", e);
				}
			}
		}

		return false;
	}
	
	private static String getDateInYYMMDD(String dateString){
		//TVL ddmmyy
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMMdd");
    	Date date=new Date();
		try {
			Date date1 = formatter.parse(dateString);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date1);
			date1=cal.getTime();
			date=date1;
			
		} catch (ParseException e) {
			//logger.error("error!", e);
		}
		SimpleDateFormat df = new SimpleDateFormat("ddMMyy");
		return df.format(date);
	}
	
	private static String getMiddleNameFromName(String firstName){
		String[] temp=firstName.split(" ");
		if(temp != null && temp.length >=2){
			return temp[1];
		}
		return null;
	}
	
	private static void buildApisFooter(StringBuilder sb){
		//logger.info("Building APIS Footer");
		sb.append("CNT+42:136'"+newline);
		sb.append("UNT+121+1402111800'"+newline);
		sb.append("UNE+1+1402111800'"+newline);
		sb.append("UNZ+1+1402111800'"+newline);
	}
	
    private static void prepareHeader(String carrier,String flight_num,StringBuilder sb){
    	buildApisHeader(sb, carrier+flight_num);
    }
    
	public static String msgFormat(String s, Object... args) {
        return new MessageFormat(s).format(args);
    }
}