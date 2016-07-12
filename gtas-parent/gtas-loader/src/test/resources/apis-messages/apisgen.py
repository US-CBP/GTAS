#!/usr/bin/python

# generates random APIS file
# to execute, first run 'pip install names'

import names
import random

carriers = ['Q5','4O','M3','GB','9T','5N','LD','9N','6A','GU','JP','A3','KH','RE','EI','4M','EE','AJ','7T','M0','P7','P5','7L','A4','SU','SU','D9','KG','5P','AR','3I','2K','F2','DF','5D','AM','WL','VH','PQ','VV','6I','6R','M7','QA','AV','FK','8U','ZI','AH','K7','G9','QN','KC','CC','UU','W9','BT','ZU','BM','YL','BP','BX','TY','SB','AC','QK','TX','2Q','CA','A7','GI','4J','TI','AG','YN','EN','6D','N5','UX','PC','OF','AF','4A','GL','NY','IX','3H','I9','JM','JS','7F','GW','L9','NX','MD','QM','KM','CW','MV','MK','ZV','MC','9U','SW','XN','NZ','7A','EL','PX','4N','YW','AP','FJ','2P','PJ','V7','HM','4D','VP','SL','VT','TN','TC','8T','TS','8C','3N','NF','VL','ZW','UM','RU','P2','FL','SI','AK','D7','4Y','K9','QH','Y5','YQ','CG','M8','6L','2T','D4','AS','KO','LV','AZ','NH','CD','4W','5A','Z8','AA','A8','M6','MO','OY','G6','O4','5F','7S','FG','Q9','IZ','U8','JW','8A','OD','OZ','4K','ER','5W','8V','7B','RC','EV','5Y','KK','IP','IQ','GR','XM','OS','2E','ZR','7U','4B','FP','Q4','Z3','HC','9V','J2','7A','AD','JA','CJ','EC','B9','UP','2B','PG','B4','3Y','JV','B2','LZ','B3','CH','A8','5Q','BG','5Z','BZ','BV','QW','KF','BF','U5','E6','DB','BA','BD','SN','FB','UZ','VE','5C','5J','C2','WZ','V0','SS','MO','5T','PT','TL','BB','2G','W8','CV','3Q','BW','RV','XG','CX','KX','9M','WE','6C','C5','RP','CI','CK','MU','8Y','CZ','KN','PN','OQ','A2','AU','QI','CF','WX','ZM','C4','C7','9L','MN','OH','XK','I5','5L','MX','CM','CP','Y4','DE','C3','CO','CS','7V','F5','7C','OU','CU','5R','CY','OK','D5','D0','ES','L3','D3','N2','H8','9J','DX','0D','JD','DL','LH','Z6','ZD','E3','KB','9H','BR','B5','WK','MS','LY','6S','A0','EK','EM','7H','B8','E7','OV','ET','EY','5B','UI','GJ','4L','QY','EW','7E','EZ','MB','8K','8D','XE','ZY','FC','QE','FX','FV','FO','AY','7F','5H','RF','F3','FY','TE','BE','7Y','HK','Y0','F9','2F','F6','GT','Z5','GY','6G','GP','GC','GA','4G','A9','QB','G5','G0','Y2','GH','GK','G7','CN','GS','GD','ZK','IJ','3R','IF','GF','3M','HQ','HR','HU','2H','HF','WP','HA','HN','YO','JB','9I','HT','HW','DU','EO','5K','8H','HD','HB','HX','KA','UO','QX','II','4I','IK','IB','X8','FI','IO','I2','7I','6K','6E','D6','3L','ZA','ID','L8','I4','IR','EP','IA','Q2','WC','6H','I3','JC','JO','O2','DV','G7','DZ','JI','JL','NU','JU','8J','9W','QJ','PP','J0','S2','LS','0J','GX','3K','BL','JX','3B','DW','N3','R3','5M','HZ','R5','J4','6J','HO','8K','KD','WA','KL','K4','CB','RQ','E2','3A','KQ','KG','YK','IT','Y9','KR','KE','KY','VD','KU','GO','JF','TM','LI','LO','XO','LT','WJ','N7','LA','UC','XL','LP','IL','QV','LE','NG','LN','4V','7D','L7','QL','LR','6M','LM','8L','LH','CL','L5','LG','5V','L2','9Y','OM','7G','SY','MB','XV','8I','VM','IN','W5','MH','MA','AE','MP','YD','VL','N5','4X','U7','IG','MZ','YV','XJ','BH','ME','MG','YX','MJ','7M','MW','2M','ZB','YM','M9','C2','N8','VZ','UB','8M','AI','IC','O9','UE','9O','ON','ZN','M4','NO','9X','KZ','LF','NA','NS','NC','NW','J3','DY','N9','OL','V8','O6','BK','OA','WY','OG','8Q','EA','R2','OX','OJ','O7','9Q','8F','RH','I8','QZ','9M','SJ','3F','Q8','LW','P8','PK','0P','9P','7Q','I7','P6','H9','KS','PR','US','9E','P2','PO','M4','PD','NI','3X','PM','FE','PU','U4','F3','PB','2P','EB','QF','QR','R6','RT','ZL','2O','FN','YS','WQ','AT','SG','BI','6D','RZ','RJ','RA','RM','WB','RD','FR','4Z','BU','SP','S4','UG','8R','Q7','LX','FA','4Q','S8','ZS','E5','SV','W7','SK','YR','RZ','NL','6Q','SC','F4','FM','ZH','J5','8S','S5','S7','3U','FT','ZP','MI','SQ','SQ','S9','XW','GQ','GV','UQ','H2','KI','XT','OO','SX','BC','LQ','5G','JZ','XR','MM','OG','VU','4S','IE','SA','G2','9S','WN','JK','NK','9C','UL','S6','2S','2I','QP','NB','SD','XQ','WG','PY','7J','RB','DT','TI','PZ','JJ','EQ','TP','RO','3V','T4','OR','6B','TA','R9','TQ','HJ','U9','3Z','P5','L6','FD','TG','DK','MT','BY','3P','ZT','9D','8P','WI','N4','T0','AX','GE','UN','HV','AL','T9','TH','9K','LU','9P','QT','VR','VW','S5','5F','TU','3T','TK','T5','6T','HK','VO','B7','5X','US','UJ','UT','PS','UF','UA','4H','EU','UV','U6','UR','HY','NN','G3','VF','6Z','LC','V4','0V','VN','V5','VX','VS','DJ','VA','VK','ZG','XF','VI','V6','WH','8O','WS','WF','7W','WM','IW','3W','WO','T8','SE','MF','YC','Y8','IY','ZJ','K8','Q3','Z4','3Z','X9']

airports = ['GKA','MAG','HGU','LAE','POM','WWK','UAK','GOH','SFJ','THU','AEY','EGS','HFN','HZK','IFJ','KEF','PFJ','RKV','SIJ','VEY','YAM','YAV','YAW','YAY','YAZ','YBB','YBC','YBG','YBK','YBL','YBR','YCB','YCD','YCG','YCH','YCL','YCO','YCT','YCW','YCY','YZS','YDA','YDB','YDC','YDF','YDL','YDN','YDQ','YEG','YEK','YEN','YET','YEU','YEV','YFB','YFC','YFO','YFR','YFS','YGK','YGL','YGP','YGQ','YGR','YHB','YHD','YHI','YHK','YHM','YHU','YHY','YHZ','YIB','YIO','YJN','YJT','YKA','YKF','YKL','YKY','YKZ','YLD','YLJ','YLL','YLT','YLW','YMA','YMJ','YMM','YMO','YMW','YMX','YNA','YND','YNM','YOC','YOD','YOJ','YOW','YPA','YPE','YPG','YPL','YPN','YPQ','YPR','YPY','YQA','YQB','YQF','YQG','YQH','YQK','YQL','YQM','YQQ','YQR','YQT','YQU','YQV','YQW','YQX','YQY','YQZ','YRB','YRI','YRJ','YRM','YRT','YSB','YSC','YSJ','YSM','YSR','YSU','YSY','YTE','YTH','YTR','YTS','YTZ','YUB','YUL','YUT','YUX','YUY','YVC','YVG','YVM','YVO','YVP','YVQ','YVR','YVT','YVV','YWA','YWG','YWK','YWL','YWY','YXC','YXD','YXE','YXH','YXJ','YXL','YXP','YXR','YXS','YXT','YXU','YXX','YXY','YYB','YYC','YYD','YYE','YYF','YYG','YYH','YYJ','YYL','YYN','YYQ','YYR','YYT','YYU','YYW','YYY','YYZ','YZD','YZE','YZF','YZH','YZP','YZR','YZT','YZU','YZV','YZW','YZX','ZFA','ZFM','BJA','ALG','DJG','QFD','VVZ','TMR','GJL','AAE','CZL','TEE','HRM','TID','TIN','QAS','TAF','TLM','ORN','MUW','AZR','BSK','ELG','GHA','HME','INZ','TGR','LOO','TMX','OGX','IAM','COO','OUA','BOY','ACC','TML','NYI','TKD','ABJ','BYK','DJO','HGO','MJC','SPY','ASK','ABV','AKR','BNI','CBQ','ENU','QUS','IBA','ILR','JOS','KAD','KAN','MIU','MDI','LOS','MXJ','PHC','SKO','YOL','ZAR','MFQ','NIM','THZ','AJY','ZND','MIR','TUN','GAF','GAE','DJE','EBM','SFA','TOE','LRL','LFW','ANR','BRU','CRL','QKT','LGG','OST','BBJ','AOC','4I7','BBH','SXF','DRS','ERF','FRA','FMO','HAM','THF','CGN','DUS','MUC','NUE','LEJ','SCN','STR','TXL','HAJ','BRE','HHN','MHG','XFW','KEL','LBC','ZCA','ESS','MGL','PAD','DTM','AGB','OBF','FDH','SZW','ZSN','BYU','HOQ','ZNV','ZQF','ZQC','ZQL','BWE','KSF','BRV','EME','WVN','BMK','NRD','FLF','GWT','KDL','URE','EPU','TLL','TAY','ENF','KEV','HEM','HEL','HYV','IVL','JOE','JYV','KAU','KEM','KAJ','KOK','KAO','KTT','KUO','LPP','MHQ','MIK','OUL','POR','RVN','SVL','SOT','TMP','TKU','QVY','VAA','VRK','BFS','ENK','BHD','LDY','BHX','CVT','GLO','MAN','NQY','LYE','YEO','CWL','SWS','BRS','LPL','LTN','PLH','BOH','SOU','QLA','ACI','GCI','JER','ESH','BQH','LGW','LCY','FAB','BBS','LHR','SEN','LYX','MSE','CAX','BLK','HUY','BWF','LBA','CEG','IOM','NCL','MME','EMA','KOI','LSI','WIC','ABZ','INV','GLA','EDI','ILY','PIK','BEB','SDZ','DND','SYY','TRE','ADX','LMO','CBG','NWI','STN','EXT','FZO','OXF','MHZ','FFD','BZZ','ODH','NHT','QCY','BEQ','WTN','KNF','MPN','AMS','MST','EIN','GRQ','DHR','LWR','RTM','UTC','ENS','LID','WOE','ORK','GWY','DUB','NOC','KIR','SNN','SXL','WAT','AAR','BLL','CPH','EBJ','KRP','ODE','RKE','RNN','SGD','SKS','TED','FAE','STA','AAL','LUX','AES','ANX','ALF','BNN','BOO','BGO','BJF','KRS','BDU','EVE','VDB','FRO','OSL','HAU','HAA','KSU','KKN','FAN','MOL','MJF','LKL','NTB','OLA','RRS','RYG','LYR','SKE','SRP','SSJ','TOS','TRF','TRD','SVG','GDN','KRK','KTW','POZ','RZE','SZZ','OSP','WAW','WRO','IEG','RNB','GOT','JKG','LDK','GSE','KVB','THN','KSK','MXX','NYO','KID','JLD','OSK','KLR','MMX','HAD','VXO','EVG','GEV','HUV','KRF','LYC','SDL','OER','KRN','SFT','UME','VHM','AJR','ORB','VST','LLA','ARN','BMA','BLE','HLF','GVX','LPI','NRK','VBY','SPM','RMS','GHF','ZCN','ZNF','GKE','RLG','FEL','GUT','ALJ','AGZ','BIY','BFN','CPT','DUR','ELS','GCJ','GRJ','HDS','JNB','KIM','KLZ','HLA','LAY','MGH','MEZ','NCS','DUH','PLZ','PHW','PTG','PZB','NTY','UTW','RCB','SBU','SIS','SZK','LTA','ULD','UTN','UTT','VRU','VIR']



header = """UNA:*.? $
UNB*UNOA:4*SDT:ZZ*JMAPIS:ZZ*140211:1800*1402111800**APIS$
UNG *PAXLST*AMERICAN AIRLINES:ZZ*JMAPIS:ZZ*140211:1800*1402111800*UN*D:02B$
UNH*1402111800*PAXLST:D:02B:UN:IATA*AA346*10:F$
BGM*745$"""

reporting_party = """NAD*MS***SABRE CENTRAL$
COM*918-833-3535:TE*918-292-7090:FX$"""

flight = """TDT*20*{0}{1}$
LOC*125*{2}$
DTM*189:1405111340:201$
LOC*87*{3}$
DTM*232:1405111700:201$"""

pax = """NAD*FL***{0}:{1}:$
ATT*2**M$
DTM*329:541016$
LOC*178*{2}$
LOC*179*{3}$
NAT*2*USA$
RFF*AVF:QXGYLO$
DOC*P:110:111*{4}$
DTM*36:180221$
LOC*91*USA$"""

footer = """CNT*42:136$
UNT*121*140211180 0$
UNE*1*1402111800$
UNZ*1*1402111800$"""

print header
print reporting_party

carrier = random.choice(carriers)
flight_num = random.randint(1, 999)
embark = random.choice(airports)
debark = random.choice(airports)
print flight.format(carrier, flight_num, embark, debark)

for x in range(0, 10):
  doc_num = random.randint(100000000,400000000)
  print pax.format(names.get_last_name().upper(), names.get_first_name().upper(), embark, debark, doc_num);

print footer
