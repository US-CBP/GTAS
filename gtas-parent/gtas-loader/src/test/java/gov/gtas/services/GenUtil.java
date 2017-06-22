/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.dto.PaxDto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GenUtil {
    private static String[] firstNames={"JAMES",    
            "JOHN","ROBERT","MICHAEL","WILLIAM","DAVID","RICHARD","CHARLES","JOSEPH","THOMAS","CHRISTOPHER","DANIEL","PAUL","MARK", 
            "DONALD","GEORGE","KENNETH","STEVEN","EDWARD","BRIAN","RONALD","ANTHONY","KEVIN","JASON","MATTHEW", "GARY","TIMOTHY",   
            "JOSE","JEFFREY","FRANK","SCOTT","ERIC","STEPHEN","ANDREW","RAYMOND","GREGORY","JOSHUA","JERRY","DENNIS","WALTER","PATRICK",    
            "PETER","HAROLD","DOUGLAS","HENRY","CARL","ARTHUR", "RYAN","ROGER","JOE","JUAN","JACK","ALBERT","JONATHAN", "JUSTIN","TERRY",   
            "GERALD","KEITH","SAMUEL","WILLIE","RALPH","LAWRENCE","NICHOLAS","ROY","BENJAMIN","MARY","PATRICIA","LINDA","BARBARA","ELIZABETH",  
            "JENNIFER", "MARIA","SUSAN","MARGARET","DOROTHY","LISA","NANCY","KAREN","BETTY","HELEN","SANDRA","DONNA","CAROL","RUTH",    
            "SHARON","MICHELLE","LAURA","SARAH","KIMBERLY", "DEBORAH","JESSICA","SHIRLEY","CYNTHIA","ANGELA","MELISSA","BRENDA","AMY",  
            "ANNA"};
    private static String[] lastNames={"SMITH", 
            "JOHNSON","WILLIAMS","BROWN","JONES","DAVIS","MILLER","JACKSON","THOMAS","LEE","WILSON","TAYLOR","ANDERSON","HARRIS","MOORE",   
            "THOMPSON","WHITE","SINGH","MARTIN","LEWIS","ROBINSON","WALKER","YOUNG","CLARK","PATEL","ALLEN","SCOTT","HALL","WRIGHT",    
            "KING","GREEN","HILL","KHAN","MITCHELL","CARTER","ADAMS","BAKER","ALI","NGUYEN","CAMPBELL","NELSON","ROBERTS","SILVA",
            "PHILLIPS", "EDWARDS","TURNER","EVANS", "PARKER"};
    private static String[] carriers ={"Q5","4O","M3","GB","9T","5N","LD","9N","6A","GU","JP","A3","KH","RE","EI","4M","EE","AJ","7T","M0","P7","P5","7L","A4","SU","SU","D9","KG","5P","AR","3I","2K","F2","DF","5D","AM","WL","VH","PQ","VV","6I","6R","M7","QA","AV","FK","8U","ZI","AH","K7","G9","QN","KC","CC","UU","W9","BT","ZU","BM","YL","BP","BX","TY","SB","AC","QK","TX","2Q","CA","A7","GI","4J","TI","AG","YN","EN","6D","N5","UX","PC","OF","AF","4A","GL","NY","IX","3H","I9","JM","JS","7F","GW","L9","NX","MD","QM","KM","CW","MV","MK","ZV","MC","9U","SW","XN","NZ","7A","EL","PX","4N","YW","AP","FJ","2P","PJ","V7","HM","4D","VP","SL","VT","TN","TC","8T","TS","8C","3N","NF","VL","ZW","UM","RU","P2","FL","SI","AK","D7","4Y","K9","QH","Y5","YQ","CG","M8","6L","2T","D4","AS","KO","LV","AZ","NH","CD","4W","5A","Z8","AA","A8","M6","MO","OY","G6","O4","5F","7S","FG","Q9","IZ","U8","JW","8A","OD","OZ","4K","ER","5W","8V","7B","RC","EV","5Y","KK","IP","IQ","GR","XM","OS","2E","ZR","7U","4B","FP","Q4","Z3","HC","9V","J2","7A","AD","JA","CJ","EC","B9","UP","2B","PG","B4","3Y","JV","B2","LZ","B3","CH","A8","5Q","BG","5Z","BZ","BV","QW","KF","BF","U5","E6","DB","BA","BD","SN","FB","UZ","VE","5C","5J","C2","WZ","V0","SS","MO","5T","PT","TL","BB","2G","W8","CV","3Q","BW","RV","XG","CX","KX","9M","WE","6C","C5","RP","CI","CK","MU","8Y","CZ","KN","PN","OQ","A2","AU","QI","CF","WX","ZM","C4","C7","9L","MN","OH","XK","I5","5L","MX","CM","CP","Y4","DE","C3","CO","CS","7V","F5","7C","OU","CU","5R","CY","OK","D5","D0","ES","L3","D3","N2","H8","9J","DX","0D","JD","","LH","Z6","ZD","E3","KB","9H","BR","B5","WK","MS","LY","6S","A0","EK","EM","7H","B8","E7","OV","ET","EY","5B","UI","GJ","4L","QY","EW","7E","EZ","MB","8K","8D","XE","ZY","FC","QE","FX","FV","FO","AY","7F","5H","RF","F3","FY","TE","BE","7Y","HK","Y0","F9","2F","F6","GT","Z5","GY","6G","GP","GC","GA","4G","A9","QB","G5","G0","Y2","GH","GK","G7","CN","GS","GD","ZK","IJ","3R","IF","GF","3M","HQ","HR","HU","2H","HF","WP","HA","HN","YO","JB","9I","HT","HW","DU","EO","5K","8H","HD","HB","HX","KA","UO","QX","II","4I","IK","IB","X8","FI","IO","I2","7I","6K","6E","D6","3L","ZA","ID","L8","I4","IR","EP","IA","Q2","WC","6H","I3","JC","JO","O2","DV","G7","DZ","JI","JL","NU","JU","8J","9W","QJ","PP","J0","S2","LS","0J","GX","3K","BL","JX","3B","DW","N3","R3","5M","HZ","R5","J4","6J","HO","8K","KD","WA","KL","K4","CB","RQ","E2","3A","KQ","KG","YK","IT","Y9","KR","KE","KY","VD","KU","GO","JF","TM","LI","LO","XO","LT","WJ","N7","LA","UC","XL","LP","IL","QV","LE","NG","LN","4V","7D","L7","QL","LR","6M","LM","8L","LH","CL","L5","LG","5V","L2","9Y","OM","7G","SY","MB","XV","8I","VM","IN","W5","MH","MA","AE","MP","YD","VL","N5","4X","U7","IG","MZ","YV","XJ","BH","ME","MG","YX","MJ","7M","MW","2M","ZB","YM","M9","C2","N8","VZ","UB","8M","AI","IC","O9","UE","9O","ON","ZN","M4","NO","9X","KZ","LF","NA","NS","NC","NW","J3","DY","N9","OL","V8","O6","BK","OA","WY","OG","8Q","EA","R2","OX","OJ","O7","9Q","8F","RH","I8","QZ","9M","SJ","3F","Q8","LW","P8","PK","0P","9P","7Q","I7","P6","H9","KS","PR","US","9E","P2","PO","M4","PD","NI","3X","PM","FE","PU","U4","F3","PB","2P","EB","QF","QR","R6","RT","ZL","2O","FN","YS","WQ","AT","SG","BI","6D","RZ","RJ","RA","RM","WB","RD","FR","4Z","BU","SP","S4","UG","8R","Q7","LX","FA","4Q","S8","ZS","E5","SV","W7","SK","YR","RZ","NL","6Q","SC","F4","FM","ZH","J5","8S","S5","S7","3U","FT","ZP","MI","SQ","SQ","S9","XW","GQ","GV","UQ","H2","KI","XT","OO","SX","BC","LQ","5G","JZ","XR","MM","OG","VU","4S","IE","SA","G2","9S","WN","JK","NK","9C","UL","S6","2S","2I","QP","NB","SD","XQ","WG","PY","7J","RB","DT","TI","PZ","JJ","EQ","TP","RO","3V","T4","OR","6B","TA","R9","TQ","HJ","U9","3Z","P5","L6","FD","TG","DK","MT","BY","3P","ZT","9D","8P","WI","N4","T0","AX","GE","UN","HV","AL","T9","TH","9K","LU","9P","QT","VR","VW","S5","5F","TU","3T","TK","T5","6T","HK","VO","B7","5X","US","UJ","UT","PS","UF","UA","4H","EU","UV","U6","UR","HY","NN","G3","VF","6Z","LC","V4","0V","VN","V5","VX","VS","DJ","VA","VK","ZG","XF","VI","V6","WH","8O","WS","WF","7W","WM","IW","3W","WO","T8","SE","MF","YC","Y8","IY","ZJ","K8","Q3","Z4","3Z","X9"};
    private static String[] airports ={"GKA","MAG","HGU","LAE","POM","WWK","UAK","GOH","SFJ","THU","AEY","EGS","HFN","HZK","IFJ","KEF","PFJ","RKV","SIJ","VEY","YAM","YAV","YAW","YAY","YAZ","YBB","YBC","YBG","YBK","YBL","YBR","YCB","YCD","YCG","YCH","YCL","YCO","YCT","YCW","YCY","YZS","YDA","YDB","YDC","YDF","YDL","YDN","YDQ","YEG","YEK","YEN","YET","YEU","YEV","YFB","YFC","YFO","YFR","YFS","YGK","YGL","YGP","YGQ","YGR","YHB","YHD","YHI","YHK","YHM","YHU","YHY","YHZ","YIB","YIO","YJN","YJT","YKA","YKF","YKL","YKY","YKZ","YLD","YLJ","YLL","YLT","YLW","YMA","YMJ","YMM","YMO","YMW","YMX","YNA","YND","YNM","YOC","YOD","YOJ","YOW","YPA","YPE","YPG","YPL","YPN","YPQ","YPR","YPY","YQA","YQB","YQF","YQG","YQH","YQK","YQL","YQM","YQQ","YQR","YQT","YQU","YQV","YQW","YQX","YQY","YQZ","YRB","YRI","YRJ","YRM","YRT","YSB","YSC","YSJ","YSM","YSR","YSU","YSY","YTE","YTH","YTR","YTS","YTZ","YUB","YUL","YUT","YUX","YUY","YVC","YVG","YVM","YVO","YVP","YVQ","YVR","YVT","YVV","YWA","YWG","YWK","YWL","YWY","YXC","YXD","YXE","YXH","YXJ","YXL","YXP","YXR","YXS","YXT","YXU","YXX","YXY","YYB","YYC","YYD","YYE","YYF","YYG","YYH","YYJ","YYL","YYN","YYQ","YYR","YYT","YYU","YYW","YYY","YYZ","YZD","YZE","YZF","YZH","YZP","YZR","YZT","YZU","YZV","YZW","YZX","ZFA","ZFM","BJA","ALG","DJG","QFD","VVZ","TMR","GJL","AAE","CZL","TEE","HRM","TID","TIN","QAS","TAF","TLM","ORN","MUW","AZR","BSK","ELG","GHA","HME","INZ","TGR","LOO","TMX","OGX","IAM","COO","OUA","BOY","ACC","TML","NYI","TKD","ABJ","BYK","DJO","HGO","MJC","SPY","ASK","ABV","AKR","BNI","CBQ","ENU","QUS","IBA","ILR","JOS","KAD","KAN","MIU","MDI","LOS","MXJ","PHC","SKO","YOL","ZAR","MFQ","NIM","THZ","AJY","ZND","MIR","TUN","GAF","GAE","DJE","EBM","SFA","TOE","LRL","LFW","ANR","BRU","CRL","QKT","LGG","OST","BBJ","AOC","4I7","BBH","SXF","DRS","ERF","FRA","FMO","HAM","THF","CGN","DUS","MUC","NUE","LEJ","SCN","STR","TXL","HAJ","BRE","HHN","MHG","XFW","KEL","LBC","ZCA","ESS","MGL","PAD","DTM","AGB","OBF","FDH","SZW","ZSN","BYU","HOQ","ZNV","ZQF","ZQC","ZQL","BWE","KSF","BRV","EME","WVN","BMK","NRD","FLF","GWT","KDL","URE","EPU","TLL","TAY","ENF","KEV","HEM","HEL","HYV","IVL","JOE","JYV","KAU","KEM","KAJ","KOK","KAO","KTT","KUO","LPP","MHQ","MIK","OUL","POR","RVN","SVL","SOT","TMP","TKU","QVY","VAA","VRK","BFS","ENK","BHD","LDY","BHX","CVT","GLO","MAN","NQY","LYE","YEO","CWL","SWS","BRS","LPL","LTN","PLH","BOH","SOU","QLA","ACI","GCI","JER","ESH","BQH","LGW","LCY","FAB","BBS","LHR","SEN","LYX","MSE","CAX","BLK","HUY","BWF","LBA","CEG","IOM","NCL","MME","EMA","KOI","LSI","WIC","ABZ","INV","GLA","EDI","ILY","PIK","BEB","SDZ","DND","SYY","TRE","ADX","LMO","CBG","NWI","STN","EXT","FZO","OXF","MHZ","FFD","BZZ","ODH","NHT","QCY","BEQ","WTN","KNF","MPN","AMS","MST","EIN","GRQ","DHR","LWR","RTM","UTC","ENS","LID","WOE","ORK","GWY","DUB","NOC","KIR","SNN","SXL","WAT","AAR","BLL","CPH","EBJ","KRP","ODE","RKE","RNN","SGD","SKS","TED","FAE","STA","AAL","LUX","AES","ANX","ALF","BNN","BOO","BGO","BJF","KRS","BDU","EVE","VDB","FRO","OSL","HAU","HAA","KSU","KKN","FAN","MOL","MJF","LKL","NTB","OLA","RRS","RYG","LYR","SKE","SRP","SSJ","TOS","TRF","TRD","SVG","GDN","KRK","KTW","POZ","RZE","SZZ","OSP","WAW","WRO","IEG","RNB","GOT","JKG","LDK","GSE","KVB","THN","KSK","MXX","NYO","KID","JLD","OSK","KLR","MMX","HAD","VXO","EVG","GEV","HUV","KRF","LYC","SDL","OER","KRN","SFT","UME","VHM","AJR","ORB","VST","LLA","ARN","BMA","BLE","HLF","GVX","LPI","NRK","VBY","SPM","RMS","GHF","ZCN","ZNF","GKE","RLG","FEL","GUT","ALJ","AGZ","BIY","BFN","CPT","DUR","ELS","GCJ","GRJ","HDS","JNB","KIM","KLZ","HLA","LAY","MGH","MEZ","NCS","DUH","PLZ","PHW","PTG","PZB","NTY","UTW","RCB","SBU","SIS","SZK","LTA","ULD","UTN","UTT","VRU","VIR"};
    private static String[] alphas ={"A","B","C","D","E","F","G","H","I","J","K","L","M","N"};
    private static String[] usairports={"BHM","HSV","IFP","GCN","AZA","PHX","TUS","YUM","XNA","FSM","ACV","FAT","LGB","LAX","OAK","ONT","PSP","SMF","SAN","SFO","DEN","BDL","DAB","MCO","SFB","TPA","PBI","ATL","SAV","ITO","HNL","BOI","SUN","BLV","ORD","PIA","RFD","FWA","IND","DSM","ICT","SDF","AEX","MSY","BGR","BWI","BOS","FNT","AZO","MBS","DLH","GPT","MCI","BIL","GRI","LAS","MHT","ACY","EWR","ABQ","ALB","JFK","PBG","CLT","GFK","CLE","MFR","PHL","PIT","CHS","MEM","AMA","DFW","HOU","SLC","BTV","IAD","RIC","CLM","CRW","ATW"};
    private static String[] ebolaAirports={"FNA","BTE","ROB","MLW"};
    //LBR,GIN,SLE "CKY","BKJ",
    
    public static String getEbolaAirport(){
        Random random = new Random();
        int select=random.nextInt(ebolaAirports.length);
        return ebolaAirports[select];
    }
    
    public static String getEbolaCountry(String airport){
        String country="";
        switch(airport){
            case "FNA":country="SLE";
                break;
            case "BTE":country="SLE";
                break;
            case "MLW":country="LBR";
                break;
            case "ROB":country="LBR";
                break;
            //default: country = "GIN";
                //break;                
        }
        return country;
    }
    public static String getUsAirport(){
        Random random = new Random();
        int select=random.nextInt(usairports.length);
        return usairports[select];
    }
    
    public static String getLastName(){
        Random random = new Random();
        int select=random.nextInt(lastNames.length);
        return lastNames[select];
    }
    
    public static String getDocumentNumber(){
        Random random = new Random();
        int select=random.nextInt(99)*1234;
        int select2=random.nextInt(alphas.length);
        return alphas[select2]+"0"+select;
    }
    public static String getFirstName(){
        Random random = new Random();
        int select=random.nextInt(firstNames.length);
        return firstNames[select];
    }
    
    public static String getCarrier(){
        Random random = new Random();
        int select=random.nextInt(carriers.length);
        return carriers[select];
    }
    
    public static String getAirport(){
        Random random = new Random();
        int select=random.nextInt(airports.length);
        return airports[select];
    }
    
    public static String getFlightNumber(){
        Random random = new Random();
        Integer select = random.nextInt(999);
        return select.toString();
    }
    public static String getRecordLocator(){
        Random random = new Random();
        Integer select = random.nextInt(9999);
        return getAirport()+select.toString();
    }
    public static int getRandomNumber(int bound){
        Random random = new Random();
        return random.nextInt(bound);
    }
    
    public static String getDate(int i){
        Date date=new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, i);
        cal.add(Calendar.HOUR, -i);
        date=cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyy:hhmm");
        return df.format(date);
    }
    
    public static String getDate(){
        Date date=new Date();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyy:hhmm");
        return df.format(date);
    }
    
    public static String getTicketDate(){
        Date date=new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        cal.add(Calendar.HOUR, -1);
        SimpleDateFormat df = new SimpleDateFormat("ddMMyy:hhmm");
        return df.format(date);
    }
    
    public static String getPhoneNumber(){
        return "0011"+getRandomNumber(9)+getRandomNumber(9)+getRandomNumber(9)+
                getRandomNumber(9)+getRandomNumber(9)+getRandomNumber(9)+
                getRandomNumber(9)+getRandomNumber(9)+getRandomNumber(9)+getRandomNumber(9);
    }
    public static String getSponsorAddress(){
        Map<Integer,String> adresses = new HashMap<>();
        adresses.put(0, "4532 WILSON STREET PHILADELPHIA, PA 34288");
        adresses.put(1, "23 NIKSON DRIVE PHILADELPHIA, PA 34288");
        adresses.put(2, "41222 URAL DRIVE ALDIE, VA 20106");
        adresses.put(3, "14544 NELSON STREET HARRISBURG, PA 33022");
        adresses.put(4, "534 GIBSON DRIVE LOUISVILLE, KY 53024");
        adresses.put(5, "1234 CLIFFWOOD PKWY IRVING, CA 60098");
        adresses.put(6, "23456 RALLY STREET CHARLESTOWN, WV 45345");
        adresses.put(7, "7890 MASON ROAD HERNDON, VA 20170");
        adresses.put(8, "6578 ELDEN STREET HERNDON, VA 20171");
        adresses.put(9, "8976 WALL STREET NEWYORK, NY 10171");
        Integer select = getRandomNumber(9);
        return adresses.get(select);    
    }
    public static String getPaxAddress(){
        //20 MAIN STREET+ANYCITY+VA+10053+USA
        Map<Integer,String> adresses = new HashMap<>();
        adresses.put(0, "1120 MAIN STREET+HERNDON+VA+20170+USA");
        adresses.put(1, "2011 ELDEN STREET+PHILADELPHIA+PA+34288+USA");
        adresses.put(2, "41222 URAL DRIVE+ANYCITY+VA+20105+USA");
        adresses.put(3, "14544 NELSON STREET+HARRISBURG+PA+33022+USA");
        adresses.put(4, "534 GIBSON DRIVE+LOUISVILLE+KY+53024+USA");
        adresses.put(5, "2330 RALLY STREET+CHARLESTOWN+WV+45345+USA");
        adresses.put(6, "1620 MAIN STREET+IRVING+CA+10053+USA");
        adresses.put(7, "2077 MAIN STREET+BOSTON+MA+10003+USA");
        adresses.put(8, "2770 MAIN STREET+NEWYORK+NA+10053+USA");
        adresses.put(9, "1270 MAIN STREET+NEWYORK+NY+10171+USA");
        Integer select = getRandomNumber(9);
        return adresses.get(select);
    }
    public static String getAddress(){
        Map<Integer,String> adresses = new HashMap<>();
        adresses.put(0, "700:4532 WILSON STREET:PHILADELPHIA:PA::US:34288");
        adresses.put(1, "700:4532 WILSON STREET:PHILADELPHIA:PA::US:34288");
        adresses.put(2, "700:41222 URAL DRIVE:ALDIE:VA::US:20106");
        adresses.put(3, "700:14544 NELSON STREET:HARRISBURG:PA::US:33022");
        adresses.put(4, "700:534 GIBSON DRIVE:LOUISVILLE:KY::US:53024");
        adresses.put(5, "700:1234 CLIFFWOOD PKWY:IRVING:CA::US:60098");
        adresses.put(6, "700:23456 RALLY STREET:CHARLESTOWN:WV::US:45345");
        adresses.put(7, "700:7890 MASON ROAD:HERNDON:VA::US:20170");
        adresses.put(8, "700:6578 ELDEN STREET:HERNDON:VA::US:20171");
        adresses.put(9, "700:8976 WALL STREET:NEWYORK:NY::US:10171");
        Integer select = getRandomNumber(9);
        return adresses.get(select);
    }
    public static String getCountryCode(){
        String[] codes ={"USA","AUS","GBR","AFG","AUT","BHS","BHR","BRA","CAN","CHN","DNK","EGY",
                "FRA","DEU","HUN","ISL","IND","IDN","JPN","KEN","KWT","MEX","PAK","PAN","QAT","RUS","SAU","ZAF"};
        Integer select = getRandomNumber(codes.length);
        return codes[select];
    }
    public static String getCcNum(){
        Map<Integer,String> cc = new HashMap<>();
        ////FOP+CC:::VI:XXXXXXXX1186:0211'
        cc.put(0, "VI:XXXXXXXX"+getRandomNumber(9)+getRandomNumber(9)+getRandomNumber(9)+getRandomNumber(9)+":0"+getRandomNumber(9)+"1"+9+"'");
        cc.put(1, "AE:XXXXXXXX"+getRandomNumber(9)+getRandomNumber(9)+getRandomNumber(9)+getRandomNumber(9)+":0"+getRandomNumber(9)+"1"+9+"'");
        cc.put(2, "MC:XXXXXXXX"+getRandomNumber(9)+getRandomNumber(9)+getRandomNumber(9)+getRandomNumber(9)+":0"+getRandomNumber(9)+"1"+9+"'");
        Integer select = getRandomNumber(2);
        return cc.get(select);
    }
    
    public static String getPnrDate(){
        //DAT-ddmmyy:hhmm
        Date date=new Date();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyy:hhmm");
        return df.format(date);
    }
    
    public static String getBirthDate(){
        //SSR-12JUL12
        String[] months={"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
        Integer select = getRandomNumber(months.length);
        String month=months[select];
        String year="7"+getRandomNumber(9);
        String date="2"+getRandomNumber(9);
        return date+month+year;
    }
    public static String getGender(){
        String[] genders={"M","F"};
        Integer select = getRandomNumber(genders.length);
        return genders[select];
    }
    public static String getExpiryDate(){
        String[] months={"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
        Integer select = getRandomNumber(months.length);
        String month=months[select];
        String date="2"+getRandomNumber(9);
        int year=17+getRandomNumber(5);
        return date+month+year;
    }
    public static String getDepArlTime(int i){
        //TVL ddmmyy
        Date date=new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, i);
        date=cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyy:hhmm");
        return df.format(date);
    }
    public static String getYYMMDD(int i){
        //TVL ddmmyy
        Date date=new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, i);
        date=cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyMMdd:hhmm");
        return df.format(date);
    }
    public static String getPaxBirthday(){
        int i=-(3+getRandomNumber(70));
        Date date=new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, i);
        cal.add(Calendar.HOUR, getRandomNumber(50));
        cal.add(Calendar.MONTH, getRandomNumber(9));
        date=cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
        return df.format(date);
    }
    public static String getApisPaxBirthday(String dob){
        //06MAR72
        //String day=dob.substring(0, 1);
        //int year = Integer.parseInt(dateString.substring(0, 4));
        //int month = Integer.parseInt(dateString.substring(4, 6));
        //int day = Integer.parseInt(dateString.substring(6));
        int day=Integer.parseInt(dob.substring(0,2));
        String month=dob.substring(2,5);
        int mon=1;
        
        switch(month){
            case "JAN":mon=1;
                break;
            case "FEB":mon=2;
                break;
            case "MAR":mon=3;
                break;
            case "APR":mon=4;
                break;
            case "MAY":mon=5;
                break;
            case "JUN":mon=6;
                break;
            case "JUL":mon=7;
                break;
            case "AUG":mon=8;
                break;
            case "SEP":mon=9;
                break;
            case "OCT":mon=10;
                break;
            case "NOV":mon=11;
                break;
            case "DEC":mon=12;
                break;
        }
        int year=Integer.parseInt(dob.substring(5));
        
        Calendar cal = new GregorianCalendar(year,mon,day);
        Date date=cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
        //System.out.println("DOB<<<<<<<<<<----"+df.format(date));
        return df.format(date);
    }
    public static List<PaxDto> getWatchList(){
        List<PaxDto> watchList=new ArrayList<>();
        PaxDto one =new PaxDto();
        one.setFirstName("Madelyne");
        one.setMiddleName("Jennifer");
        one.setLastName("Pryor");
        one.setDob("06MAR72");
        watchList.add(one);
        
        PaxDto two =new PaxDto();
        two.setFirstName("John");
        two.setLastName("Wraith");
        two.setDob("28SEP58");
        watchList.add(two);
        
        PaxDto three =new PaxDto();
        three.setFirstName("Lex");
        three.setLastName("Luthor");
        three.setDob("15JUL45");
        watchList.add(three);
        
        PaxDto four =new PaxDto();
        four.setFirstName("Slade");
        one.setMiddleName("Joseph");
        four.setLastName("Wilson");
        four.setDob("11JUN70");
        watchList.add(four);
        
        PaxDto five =new PaxDto();
        five.setFirstName("John");
        five.setLastName("Constantine");
        five.setDob("30APR69");
        watchList.add(five);

        PaxDto six =new PaxDto();
        six.setFirstName("Valerie");
        six.setLastName("Hart");
        six.setDob("05AUG75");
        watchList.add(six);
        
        PaxDto seven =new PaxDto();
        seven.setFirstName("Diana");
        seven.setLastName("Prince");
        seven.setDob("01JAN60");
        watchList.add(seven);
        
        PaxDto eight =new PaxDto();
        eight.setFirstName("Henry");
        eight.setMiddleName("Jonathan");
        eight.setLastName("Pym");
        eight.setDob("20AUG78");
        watchList.add(eight);
        
        PaxDto nine =new PaxDto();
        nine.setFirstName("Wanda");
        nine.setLastName("Maximoff");
        nine.setDob("24MAR80");
        watchList.add(nine);
        
        return watchList;
    }
    public static PaxDto getPaxDto(){
        List<PaxDto> watchList=getWatchList();
        Integer select = getRandomNumber(watchList.size());
        PaxDto dto=watchList.get(select);
        return dto;
    }
    public static String getPnrCreateDate(){
    	//230516:181348
    	Calendar cal = Calendar.getInstance();
    	Date date=cal.getTime();
    	SimpleDateFormat df = new SimpleDateFormat("ddMMyy:HHmmss");
    	return df.format(date);
    }
}
