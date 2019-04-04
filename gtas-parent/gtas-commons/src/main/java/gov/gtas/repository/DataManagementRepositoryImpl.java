package gov.gtas.repository;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import gov.gtas.enumtype.DataManagementTruncation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import gov.gtas.enumtype.AuditActionType;
import gov.gtas.model.User;
import gov.gtas.services.AuditLogPersistenceService;
import java.util.stream.Collectors;


@Repository
public class DataManagementRepositoryImpl implements DataManagementRepository 
{
	
	@Autowired
	private AuditLogPersistenceService auditLogPersistenceService;
	
	private static final Logger logger = LoggerFactory
			.getLogger(DataManagementRepositoryImpl.class);
        
        
//        public static enum DataTruncationType
//        {
//           ALL,
//           APIS_ONLY,
//           PNR_ONLY
//        }
	
	// these tables are listed in the order that they must be truncated
	private static final String HITS_DISPOSITION_COMMENTS_TABLE_NAME = "hits_disposition_comments";
	private static final String HITS_DISPOSITION_TABLE_NAME = "hits_disposition";
        private static final String CASES_TABLE_NAME = "cases";	
	private static final String TICKET_FARE_TABLE_NAME = "ticket_fare";
	private static final String BAG_TABLE_NAME = "bag";
	private static final String DISPOSITION_TABLE_NAME = "disposition";
	private static final String PAYMENT_FORM_TABLE_NAME = "payment_form";
	private static final String APIS_MESSAGE_FLIGHT_PAX_TABLE_NAME = "apis_message_flight_pax";
	private static final String FLIGHT_PAX_TABLE_NAME = "flight_pax";
	private static final String HIT_DETAIL_TABLE_NAME = "hit_detail";
	private static final String HITS_SUMMARY_TABLE_NAME = "hits_summary";
	private static final String DOCUMENT_TABLE_NAME = "document";
	private static final String APIS_MESSAGE_PASSENGER_TABLE_NAME = "apis_message_passenger";
	private static final String FLIGHT_PASSENGER_TABLE_NAME = "flight_passenger";
	private static final String APIS_MESSAGE_FLIGHT_TABLE_NAME = "apis_message_flight";
	private static final String FLIGHT_LEG_TABLE_NAME = "flight_leg";
	private static final String SEAT_TABLE_NAME = "seat";
	private static final String APIS_MESSAGE_REPORTING_PARTY_TABLE_NAME = "apis_message_reporting_party";
	private static final String REPORTING_PARTY_TABLE_NAME = "reporting_party";
	private static final String APIS_MESSAGE_TABLE_NAME = "apis_message";
	private static final String PNR_PASSENGER_TABLE_NAME = "pnr_passenger";
	private static final String PNR_FLIGHT_TABLE_NAME = "pnr_flight";
	private static final String PNR_CODESHARES_TABLE_NAME = "pnr_codeshares";
        private static final String FLIGHT_HIT_RULE_TABLE_NAME = "flight_hit_rule";
        private static final String FLIGHT_HIT_WATCHLIST_TABLE_NAME = "flight_hit_watchlist";
	private static final String CODE_SHARE_FLIGHT_TABLE_NAME = "code_share_flight";
        private static final String MUTABLE_FLIGHT_DETAILS_TABLE_NAME = "mutable_flight_details";
	private static final String FLIGHT_TABLE_NAME = "flight";
	private static final String PNR_AGENCY_TABLE_NAME = "pnr_agency";
	private static final String AGENCY_TABLE_NAME = "agency";
	private static final String PNR_CREDIT_CARD_TABLE_NAME = "pnr_credit_card";
	private static final String CREDIT_CARD_TABLE_NAME = "credit_card";
	private static final String PNR_FREQUENT_FLYER_TABLE_NAME = "pnr_frequent_flyer";
	private static final String FREQUENT_FLYER_TABLE_NAME = "frequent_flyer";
	private static final String PNR_PHONE_TABLE_NAME = "pnr_phone";
	private static final String PHONE_TABLE_NAME = "phone";
	private static final String PNR_EMAIL_TABLE_NAME = "pnr_email";
	private static final String EMAIL_TABLE_NAME = "email";
	private static final String PNR_ADDRESS_TABLE_NAME = "pnr_address";
	private static final String PNR_DWELLTIME_TABLE_NAME = "pnr_dwelltime";
	private static final String PNR_BOOKING_TABLE_NAME = "pnr_booking";
	private static final String PAX_BOOKING_TABLE_NAME = "pax_booking";
	private static final String BOOKINGDETAIL_TABLE_NAME = "BookingDetail";
	private static final String ADDRESS_TABLE_NAME = "address";
	private static final String DWELL_TIME_TABLE_NAME = "dwell_time";
	private static final String PNR_TABLE_NAME = "pnr";
	private static final String MESSAGE_TABLE_NAME = "message";
	private static final String PASSENGER_ID_TAG_TABLE_NAME = "passenger_id_tag";
        private static final String HITS_DISPOSITION_COMMENTS_ATTACHMENT_TABLE_NAME = "hits_disposition_comments_attachment";
        private static final String ATTACHMENT_TABLE_NAME = "attachment";
	private static final String PASSENGER_TABLE_NAME = "passenger";
	private static final String LOADER_AUDIT_LOGS_TABLE_NAME = "loader_audit_logs";
	private static final String ERROR_DETAIL_TABLE_NAME = "error_detail";
	private static final String AUDIT_LOG_TABLE_NAME = "audit_log";
	private static final String DASHBOARD_MESSAGE_STATS_TABLE_NAME = "dashboard_message_stats";
        // new tables
        private static final String CASE_COMMENT_TABLE_NAME = "case_comment";
        private static final String FLIGHT_PASSENGER_COUNT_TABLE_NAME = "flight_passenger_count";
        private static final String PASSENGER_DETAILS_TABLE_NAME = "passenger_details";
        private static final String PASSENGER_TRIP_DETAILS_TABLE_NAME = "passenger_trip_details";
        private static final String PASSENGER_WL_TIMESTAMP_TABLE_NAME = "passenger_wl_timestamp";
        private static final String PAX_WATCHLIST_LINK_TABLE_NAME = "pax_watchlist_link";
        private static final String MESSAGE_STATUS_TABLE_NAME = "message_status";

                
	private static final String MESSAGE_ID_LIST_KEY = "messageIdList";
	private static final String TOTAL_PAX_ID_SET_KEY = "totalPaxIdSet";
	private static final String TOTAL_FLIGHT_ID_SET_KEY = "totalFlightIdSet";
	private static final String CASE_ID_LIST_KEY = "caseIdList";
        private static final String ATTACHMENT_ID_LIST_KEY = "attachmentIdList";
	private static final String HITS_SUMMARY_ID_LIST_KEY = "hitsSummaryIdList";
	private static final String REPORTING_PARTY_ID_LIST_KEY = "reportingPartyIdList";
	private static final String CODE_SHARE_ID_LIST_KEY = "codeShareIdList";
	private static final String AGENCY_ID_LIST_KEY = "agencyIdList";
	private static final String CREDIT_CARD_ID_LIST_KEY = "creditCardIdList";
	private static final String FREQUENT_FLYER_ID_LIST_KEY = "frequentFlyerIdList";
	private static final String PHONE_ID_LIST_KEY = "phoneIdList";
	private static final String EMAIL_ID_LIST_KEY = "emailIdList";
	private static final String ADDRESS_ID_LIST_KEY = "addressIdList";
	private static final String DWELL_ID_LIST_KEY = "dwellIdList";
	private static final String BOOKING_ID_LIST_KEY = "bookingIdList";
	private static final String HIT_DISPOSITION_ID_LIST_KEY = "hitDispositionIdList";
	
	private static String APIS_PAX_ID_SQL = " SELECT passenger_id from apis_message_passenger where apis_message_id in :otherIdList ";
	private static String PNR_PAX_ID_SQL =  " SELECT passenger_id from pnr_passenger where pnr_id in :otherIdList ";
	private static String APIS_FLIGHT_ID_SQL = " SELECT flight_id from apis_message_flight where apis_message_id in :otherIdList ";
	private static String PNR_FLIGHT_ID_SQL = " SELECT flight_id from pnr_flight where pnr_id in :otherIdList ";
	private static String REPORTING_PARTY_ID_SQL = " SELECT reporting_party_id from apis_message_reporting_party where apis_message_id in :otherIdList ";
	private static String CODE_SHARE_ID_SQL = "  SELECT codeshare_id from pnr_codeshares where pnr_id in :otherIdList ";
	private static String AGENCY_ID_LIST = "  SELECT agency_id from pnr_agency where pnr_id in :otherIdList ";
	private static String CREDIT_CARD_ID_SQL = " SELECT credit_card_id from pnr_credit_card where pnr_id in :otherIdList";
	private static String FREQUENT_FLYER_ID_SQL = "  SELECT ff_id from pnr_frequent_flyer where pnr_id in :otherIdList ";
	private static String PHONE_ID_SQL = "  SELECT phone_id from pnr_phone where pnr_id in :otherIdList ";
	private static String EMAIL_ID_SQL = " SELECT email_id from pnr_email where pnr_id in :otherIdList";
	private static String ADDRESS_ID_SQL = " SELECT address_id from pnr_address where pnr_id in :otherIdList";
	private static String DWELL_ID_SQL = " SELECT dwell_id from pnr_dwelltime where pnr_id in :otherIdList";
	private static String BOOKING_ID_SQL = " SELECT booking_detail_id from pnr_booking where pnr_id in :otherIdList";
        private static String ATTACHMENT_ID_SQL = " SELECT id from attachment where passenger_id in :otherIdList";
        
        private static String APIS_MESSAGE_ID_SQL = " SELECT apis_message_id from apis_message_flight where flight_id in :otherIdList";
        private static String PNR_ID_SQL = " SELECT pnr_id from pnr_flight where flight_id in :otherIdList";
        private static String APIS_FLIGHT_ID_SQL2 = " SELECT flight_id from apis_message_flight where flight_id in :otherIdList";
        private static String PNR_FLIGHT_ID_SQL2 = " SELECT flight_id from pnr_flight where flight_id in :otherIdList";
	
	private Map< String, Collection<BigInteger> > inClauseIdListsMap;
	
	private List<String> messageList;
	
	// table name, id name, list key in each row
	private List< List<String> > sqlDeleteElements;
	
	
	@PersistenceContext
	private EntityManager em;

	
	@SuppressWarnings("unchecked")
	@Override
	public void truncateAllMessageDataByDate(LocalDate localDate,  User currentUser, DataManagementTruncation type) throws Exception
	{
		inClauseIdListsMap = new HashMap<>();
		
		sqlDeleteElements = new ArrayList<>();
		
		messageList = new ArrayList<>();
		
		boolean isEmptyMessageList = false;
		try
		{
			// this will be false if there are no messages to delete before the selected date.
			boolean continueBool = retrieveAllListsAndLoadIntoMap(localDate, type);
			
			if (continueBool)
			{
				initializeSqlDeleteElements(type);
				
				deleteFromAllTablesWithInClause();
				
				deleteFromAllTablesWithDate(localDate);
				
				writeActionToAuditLog(localDate, currentUser);
				
				// write messages after all deletes have completed successfully. 
				// If an exception is thrown, then no deletes will happen.
				for (String message : messageList)
				{
					logger.info(message);
				}
			}
			else
			{
				isEmptyMessageList  = true;
			}
			
		}
		catch (Exception ex)
		{
			logger.error("Error truncating GTAS data, message: " + ex.getMessage(), ex);
			throw new Exception("Error truncating GTAS data, message: " + ex.getMessage());
		}
		
		if (isEmptyMessageList)
		{
			throw new Exception("There are no messages to delete before the date " + localDate.toString());	
		}

	}
	
	private void initializeSqlDeleteElements(DataManagementTruncation type)
	{
             // here we have the trio: table name, id name, and key to list for IN clause. These will be used by deleteFromAllTablesWithInClause().
            // These are in order; do not change the order unless there are database changes.
                List<String> strList = Arrays.asList(HITS_DISPOSITION_COMMENTS_ATTACHMENT_TABLE_NAME,"attachment_id",ATTACHMENT_ID_LIST_KEY);
                sqlDeleteElements.add(strList);
		strList = Arrays.asList(HITS_DISPOSITION_COMMENTS_TABLE_NAME,"hit_disp_id",HIT_DISPOSITION_ID_LIST_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(HITS_DISPOSITION_TABLE_NAME,"case_id",CASE_ID_LIST_KEY);
		sqlDeleteElements.add(strList);
                strList = Arrays.asList(CASE_COMMENT_TABLE_NAME,"cc_id",CASE_ID_LIST_KEY);
                sqlDeleteElements.add(strList);
                strList = Arrays.asList(CASES_TABLE_NAME,"id",CASE_ID_LIST_KEY);
		sqlDeleteElements.add(strList);
		 strList = Arrays.asList(TICKET_FARE_TABLE_NAME,"passenger_id",TOTAL_PAX_ID_SET_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(BAG_TABLE_NAME,"passenger_id",TOTAL_PAX_ID_SET_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(DISPOSITION_TABLE_NAME,"paxId",TOTAL_PAX_ID_SET_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(PAYMENT_FORM_TABLE_NAME,"pnr_id",MESSAGE_ID_LIST_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(APIS_MESSAGE_FLIGHT_PAX_TABLE_NAME,"apis_message_id",MESSAGE_ID_LIST_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(FLIGHT_PAX_TABLE_NAME,"passenger_id",TOTAL_PAX_ID_SET_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(HIT_DETAIL_TABLE_NAME,"hits_summary_id",HITS_SUMMARY_ID_LIST_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(HITS_SUMMARY_TABLE_NAME,"passenger_id",TOTAL_PAX_ID_SET_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(DOCUMENT_TABLE_NAME,"passenger_id",TOTAL_PAX_ID_SET_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(APIS_MESSAGE_PASSENGER_TABLE_NAME,"apis_message_id",MESSAGE_ID_LIST_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(FLIGHT_PASSENGER_TABLE_NAME,"passenger_id",TOTAL_PAX_ID_SET_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(APIS_MESSAGE_FLIGHT_TABLE_NAME,"apis_message_id",MESSAGE_ID_LIST_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(FLIGHT_LEG_TABLE_NAME,"pnr_id",MESSAGE_ID_LIST_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(SEAT_TABLE_NAME,"passenger_id",TOTAL_PAX_ID_SET_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(APIS_MESSAGE_REPORTING_PARTY_TABLE_NAME,"apis_message_id",MESSAGE_ID_LIST_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(REPORTING_PARTY_TABLE_NAME,"id",REPORTING_PARTY_ID_LIST_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(APIS_MESSAGE_TABLE_NAME,"id",MESSAGE_ID_LIST_KEY);
		sqlDeleteElements.add(strList);
                
                if (!type.equals(DataManagementTruncation.APIS_ONLY))
                {
                    strList = Arrays.asList(PNR_PASSENGER_TABLE_NAME,"pnr_id",MESSAGE_ID_LIST_KEY);
                    sqlDeleteElements.add(strList);	
                    strList = Arrays.asList(PNR_FLIGHT_TABLE_NAME,"pnr_id",MESSAGE_ID_LIST_KEY);
                    sqlDeleteElements.add(strList);
                    strList = Arrays.asList(PNR_CODESHARES_TABLE_NAME,"pnr_id",MESSAGE_ID_LIST_KEY);
                    sqlDeleteElements.add(strList);
                }
                
                
                strList = Arrays.asList(FLIGHT_HIT_RULE_TABLE_NAME,"fhr_flight_id",TOTAL_FLIGHT_ID_SET_KEY);
		sqlDeleteElements.add(strList);
                strList = Arrays.asList(FLIGHT_HIT_WATCHLIST_TABLE_NAME,"fhw_flight_id",TOTAL_FLIGHT_ID_SET_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(CODE_SHARE_FLIGHT_TABLE_NAME,"id",CODE_SHARE_ID_LIST_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(FLIGHT_PASSENGER_COUNT_TABLE_NAME,"fp_flight_id",TOTAL_FLIGHT_ID_SET_KEY);
		sqlDeleteElements.add(strList);
        	strList = Arrays.asList(MUTABLE_FLIGHT_DETAILS_TABLE_NAME,"flight_id",TOTAL_FLIGHT_ID_SET_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(FLIGHT_TABLE_NAME,"id",TOTAL_FLIGHT_ID_SET_KEY);
		sqlDeleteElements.add(strList);
                
                 if (!type.equals(DataManagementTruncation.APIS_ONLY))
                {               
                    strList = Arrays.asList(PNR_AGENCY_TABLE_NAME,"agency_id",AGENCY_ID_LIST_KEY);
                    sqlDeleteElements.add(strList);
                    strList = Arrays.asList(AGENCY_TABLE_NAME,"id",AGENCY_ID_LIST_KEY);
                    sqlDeleteElements.add(strList);
                    strList = Arrays.asList(PNR_CREDIT_CARD_TABLE_NAME,"pnr_id",MESSAGE_ID_LIST_KEY);
                    sqlDeleteElements.add(strList);
                    strList = Arrays.asList(CREDIT_CARD_TABLE_NAME,"id",CREDIT_CARD_ID_LIST_KEY);
                    sqlDeleteElements.add(strList);
                    strList = Arrays.asList(PNR_FREQUENT_FLYER_TABLE_NAME,"pnr_id",MESSAGE_ID_LIST_KEY);
                    sqlDeleteElements.add(strList);
                    strList = Arrays.asList(FREQUENT_FLYER_TABLE_NAME,"id",FREQUENT_FLYER_ID_LIST_KEY);
                    sqlDeleteElements.add(strList);
                    strList = Arrays.asList(PNR_PHONE_TABLE_NAME,"phone_id",PHONE_ID_LIST_KEY);
                    sqlDeleteElements.add(strList);
                    strList = Arrays.asList(PNR_EMAIL_TABLE_NAME,"pnr_id",MESSAGE_ID_LIST_KEY);
                    sqlDeleteElements.add(strList);
                    strList = Arrays.asList(EMAIL_TABLE_NAME,"id",EMAIL_ID_LIST_KEY);
                    sqlDeleteElements.add(strList);
                    strList = Arrays.asList(PNR_ADDRESS_TABLE_NAME,"pnr_id",MESSAGE_ID_LIST_KEY);
                    sqlDeleteElements.add(strList);
                    strList = Arrays.asList(PNR_DWELLTIME_TABLE_NAME,"pnr_id",MESSAGE_ID_LIST_KEY);
                    sqlDeleteElements.add(strList);
                    strList = Arrays.asList(DWELL_TIME_TABLE_NAME,"id",DWELL_ID_LIST_KEY);
                    sqlDeleteElements.add(strList);
                    strList = Arrays.asList(PNR_BOOKING_TABLE_NAME,"pnr_id",MESSAGE_ID_LIST_KEY);
                    sqlDeleteElements.add(strList);
                    strList = Arrays.asList(PNR_TABLE_NAME,"id",MESSAGE_ID_LIST_KEY);
                    sqlDeleteElements.add(strList);
                }
                 
                strList = Arrays.asList(ADDRESS_TABLE_NAME,"id",ADDRESS_ID_LIST_KEY);
                sqlDeleteElements.add(strList);
                strList = Arrays.asList(PHONE_TABLE_NAME,"id",PHONE_ID_LIST_KEY);
                sqlDeleteElements.add(strList);
                strList = Arrays.asList(PAX_BOOKING_TABLE_NAME,"pax_id",TOTAL_PAX_ID_SET_KEY);
                sqlDeleteElements.add(strList);
                
                
                strList = Arrays.asList(BOOKINGDETAIL_TABLE_NAME,"id",BOOKING_ID_LIST_KEY);
                sqlDeleteElements.add(strList); 
                
                strList = Arrays.asList(MESSAGE_STATUS_TABLE_NAME,"ms_message_id",MESSAGE_ID_LIST_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(MESSAGE_TABLE_NAME,"id",MESSAGE_ID_LIST_KEY);
		sqlDeleteElements.add(strList);
                strList = Arrays.asList(ATTACHMENT_TABLE_NAME, "passenger_id",TOTAL_PAX_ID_SET_KEY);
                sqlDeleteElements.add(strList);
		strList = Arrays.asList(PASSENGER_ID_TAG_TABLE_NAME,"pax_id",TOTAL_PAX_ID_SET_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(PASSENGER_DETAILS_TABLE_NAME,"pd_passenger_id",TOTAL_PAX_ID_SET_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(PASSENGER_TRIP_DETAILS_TABLE_NAME,"ptd_id",TOTAL_PAX_ID_SET_KEY);
		sqlDeleteElements.add(strList);
		strList = Arrays.asList(PASSENGER_WL_TIMESTAMP_TABLE_NAME,"pwlt_id",TOTAL_PAX_ID_SET_KEY);
		sqlDeleteElements.add(strList); 
		strList = Arrays.asList(PAX_WATCHLIST_LINK_TABLE_NAME,"passenger_id",TOTAL_PAX_ID_SET_KEY);
		sqlDeleteElements.add(strList);                
		strList = Arrays.asList(PASSENGER_TABLE_NAME,"id",TOTAL_PAX_ID_SET_KEY);
		sqlDeleteElements.add(strList);
		
	}
	
	private void deleteFromAllTablesWithInClause() throws Exception
	{
		for (List<String> strList : sqlDeleteElements)
		{
			String tableName = strList.get(0);
			String idName = strList.get(1);
			String listKey = strList.get(2);
			
			deleteFromTableWithInClause(tableName, idName, listKey);
		}

	}
	
	private void deleteFromAllTablesWithDate(LocalDate localDate) throws Exception
	{
		deleteFromTableWithDate(LOADER_AUDIT_LOGS_TABLE_NAME, "created_at", localDate); 
		deleteFromTableWithDate(ERROR_DETAIL_TABLE_NAME, "timestamp", localDate);
		deleteFromTableWithDate(AUDIT_LOG_TABLE_NAME, "timestamp", localDate);
		deleteFromTableWithDate(DASHBOARD_MESSAGE_STATS_TABLE_NAME, "dt_modified", localDate);
	}
	
	private void deleteFromTableWithInClause(String tableName, String idName, String listKey) throws Exception
	{
		try
		{
		   Collection<BigInteger> list = inClauseIdListsMap.get(listKey);
		   if ((list != null) && (!list.isEmpty()))
		   {
		       String sqlString = " DELETE FROM " + tableName + " WHERE " + idName + " IN "	+ ":" + listKey;
		       Query query = em.createNativeQuery(sqlString);
		       query.setParameter(listKey, list);
		       int numDeleted = query.executeUpdate();
		       //logger.info(numDeleted + " rows deleted from table " + tableName);
		       messageList.add("Data truncation: " + numDeleted + " rows deleted from table " + tableName);
		   }
	       
		}
		catch (Exception ex)
		{
		   String message = "Error deleting " + tableName + " table: " + ex.getMessage();
		   logger.error(message);
		   throw new Exception(message);
		}
		
	}
	
	private void deleteFromTableWithDate(String tableName, String idName, LocalDate localDate) throws Exception
	{
		try
		{
                   String sqlString = " DELETE FROM " + tableName + " WHERE " + idName + " <  :localDate";
                   Query query = em.createNativeQuery(sqlString);
                   query.setParameter("localDate", localDate.toString());
                   int numDeleted = query.executeUpdate();
                   //logger.info(numDeleted + " rows deleted from table " + tableName);
                   messageList.add("Data truncation: " + numDeleted + " rows deleted from table " + tableName);
		       
		}
		catch (Exception ex)
		{
		   String message = "Error deleting " + tableName + " table: " + ex.getMessage();
		   logger.error(message);
		   throw new Exception(message);
		}		

	}
        
	private boolean retrieveAllListsAndLoadIntoMap(LocalDate localDate, DataManagementTruncation type)
	{
            boolean returnBool = true; 

            List<BigInteger> totalFlightIdList = getFlightIdListBeforeDate(localDate);
            
            if (!totalFlightIdList.isEmpty())
            {
                final List<BigInteger> apisPaxIdList;
                final List<BigInteger> pnrPaxIdList;
                List<BigInteger> apisFlightIdList;
                List<BigInteger> pnrFlightIdList;
                List<BigInteger> messagesWithError = new ArrayList<>();

                List<BigInteger> apisMessageIdList = new ArrayList<>();
                List<BigInteger> pnrIdList =  new ArrayList<>();
                Set<BigInteger> totalMessageIdSet = new HashSet<>();
                Set<BigInteger> totalPaxIdSet = new HashSet<>();

                apisMessageIdList = getSomeIdList(APIS_MESSAGE_ID_SQL, totalFlightIdList);
                pnrIdList = getSomeIdList(PNR_ID_SQL, totalFlightIdList);
                apisPaxIdList = getSomeIdList(APIS_PAX_ID_SQL, apisMessageIdList);
                pnrPaxIdList = getSomeIdList(PNR_PAX_ID_SQL, pnrIdList);
                messagesWithError =  getMessagesWithErrorBeforeDate(localDate);
                
                // delete messages that failed parsing with an error and have no associated flight.
                totalMessageIdSet.addAll(messagesWithError);

                if (type.equals(DataManagementTruncation.ALL))
                {
                    totalMessageIdSet.addAll(apisMessageIdList);
                    totalMessageIdSet.addAll(pnrIdList);
                    totalPaxIdSet.addAll(apisPaxIdList);
                    totalPaxIdSet.addAll(pnrPaxIdList);
                    
                }
                else if (type.equals(DataManagementTruncation.APIS_ONLY))
                {
                   apisFlightIdList = getSomeIdList(APIS_FLIGHT_ID_SQL2, totalFlightIdList);
                   pnrFlightIdList = getSomeIdList(PNR_FLIGHT_ID_SQL2, totalFlightIdList);
                   
                   // apis flights must not also be referenced by PNR, otherwise we cannot delete them.
                   List<BigInteger> filteredApisFlightList = apisFlightIdList.stream().filter(f -> !pnrFlightIdList.contains(f)).collect(Collectors.toList());
                   if (filteredApisFlightList == null || filteredApisFlightList.isEmpty())
                   {
                      returnBool = false; 
                   }
                   
                   totalMessageIdSet.addAll(apisMessageIdList);
                   totalFlightIdList = filteredApisFlightList;
                   // we need apis passengers that are not also PNR passengers, otherwise we cannot delete them.
                   List<BigInteger> filteredApisPaxList = apisPaxIdList.stream().filter(p -> !pnrPaxIdList.contains(p)).collect(Collectors.toList());
                   //totalPaxIdSet.addAll(apisPaxIdList);
                   totalPaxIdSet.addAll(filteredApisPaxList);
                }
                else if (type.equals(DataManagementTruncation.PNR_ONLY))
                {
                    pnrFlightIdList = getSomeIdList(PNR_FLIGHT_ID_SQL2, totalFlightIdList); 
                    apisFlightIdList = getSomeIdList(APIS_FLIGHT_ID_SQL2, totalFlightIdList);
                    // apis flights must not also be referenced by PNR, otherwise we cannot delete them.
                    List<BigInteger> filteredPnrFlightList = pnrFlightIdList.stream().filter(f -> !apisFlightIdList.contains(f)).collect(Collectors.toList());
                    if (filteredPnrFlightList == null || filteredPnrFlightList.isEmpty())
                    {
                       returnBool = false; 
                    }
                   
                    totalMessageIdSet.addAll(pnrIdList);
                    totalFlightIdList = filteredPnrFlightList;
                    // we need PNR passengers that are not also APIS passengers, otherwise we cannot delete them.
                    List<BigInteger> filteredPnrPaxList = pnrPaxIdList.stream().filter(p -> !apisPaxIdList.contains(p)).collect(Collectors.toList());
                   // totalPaxIdSet.addAll(pnrPaxIdList);
                    totalPaxIdSet.addAll(filteredPnrPaxList);
                }

                    List<BigInteger> caseIdList = this.getCaseIdList(totalPaxIdSet);

                    List<BigInteger> hitsSummaryIdList = this.getHitsSummaryIdList(totalPaxIdSet);

                    List<BigInteger> reportingPartyIdList = getSomeIdList(REPORTING_PARTY_ID_SQL, totalMessageIdSet);

                    List<BigInteger>  codeShareIdList = getSomeIdList(CODE_SHARE_ID_SQL, totalMessageIdSet);
                    
                    List<BigInteger> bookingIdList =  getSomeIdList(BOOKING_ID_SQL, totalMessageIdSet);
                    
                    List<BigInteger> phoneIdList = getSomeIdList(PHONE_ID_SQL, totalMessageIdSet);

                    
                    if (!type.equals(DataManagementTruncation.APIS_ONLY))
                    { 
                        List<BigInteger> agencyIdList = getSomeIdList(AGENCY_ID_LIST, totalMessageIdSet);

                        List<BigInteger> creditCardIdList = getSomeIdList(CREDIT_CARD_ID_SQL, totalMessageIdSet);

                        List<BigInteger> frequentFlyerIdList = getSomeIdList(FREQUENT_FLYER_ID_SQL, totalMessageIdSet);

                        List<BigInteger> emailIdList = getSomeIdList(EMAIL_ID_SQL, totalMessageIdSet);

                        List<BigInteger> addressIdList = getSomeIdList(ADDRESS_ID_SQL, totalMessageIdSet);

                        List<BigInteger> dwellIdList = getSomeIdList(DWELL_ID_SQL, totalMessageIdSet);

                        addListToMap(ADDRESS_ID_LIST_KEY, addressIdList);
                        addListToMap(AGENCY_ID_LIST_KEY, agencyIdList);
                        
                        addListToMap(CREDIT_CARD_ID_LIST_KEY, creditCardIdList);
                        addListToMap(DWELL_ID_LIST_KEY, dwellIdList);
                        addListToMap(EMAIL_ID_LIST_KEY, emailIdList);
                        addListToMap(FREQUENT_FLYER_ID_LIST_KEY, frequentFlyerIdList);
                        
                    }

                    List<BigInteger> hitDispositionIdList = this.getHitDispositionIdList(caseIdList);

                    List<BigInteger> attachmentIdList = this.getSomeIdList(ATTACHMENT_ID_SQL, totalPaxIdSet);

                    addListToMap(PHONE_ID_LIST_KEY, phoneIdList);
                    addListToMap(BOOKING_ID_LIST_KEY, bookingIdList);
                    addListToMap(CASE_ID_LIST_KEY, caseIdList);
                    addListToMap(CODE_SHARE_ID_LIST_KEY, codeShareIdList);
                    addListToMap(HITS_SUMMARY_ID_LIST_KEY, hitsSummaryIdList);
                    addListToMap(HIT_DISPOSITION_ID_LIST_KEY, hitDispositionIdList);
                    addListToMap(REPORTING_PARTY_ID_LIST_KEY, reportingPartyIdList);
                    addListToMap(TOTAL_FLIGHT_ID_SET_KEY, totalFlightIdList);
                    addListToMap(TOTAL_PAX_ID_SET_KEY, totalPaxIdSet);
                    addListToMap(MESSAGE_ID_LIST_KEY, totalMessageIdSet);
                    addListToMap(ATTACHMENT_ID_LIST_KEY, attachmentIdList);                

            }
            else
            {
              returnBool = false;  
            }
           return returnBool;
        }        
	
	private void addListToMap(String key, Collection<BigInteger> value)
	{
		this.getInClauseIdListsMap().put(key,value);		
		
	}
	
	private List<BigInteger> getMessageIdListBeforeDate(LocalDate localDate)
	{
		List<BigInteger> messageIdList = new ArrayList<>();		
		String sqlQuery = " SELECT id from message where create_date < :localDate ";
		Query query = em.createNativeQuery(sqlQuery);
		query.setParameter("localDate", Date.valueOf(localDate));
		
		messageIdList = query.getResultList();
		
		return messageIdList;
	}
        
        private List<BigInteger> getFlightIdListBeforeDate(LocalDate localDate)
        {
            List<BigInteger> flightIdList = new ArrayList<>();
            String sqlQuery = " SELECT flight_id from mutable_flight_details where eta_date < :localDate ";
            Query query = em.createNativeQuery(sqlQuery);
            query.setParameter("localDate", Date.valueOf(localDate)); 
            
            flightIdList = query.getResultList();
            
            return flightIdList;
        }
	
	private List<BigInteger> getSomeIdList(String sqlQuery, Collection<BigInteger> otherIdList)
	{
		List<BigInteger> returnIdList = new ArrayList<>();
                
 		if (otherIdList != null && !otherIdList.isEmpty())
                {               
                    Query query = em.createNativeQuery(sqlQuery);
                    query.setParameter("otherIdList", otherIdList);

                    returnIdList = query.getResultList();
                }
		
		return returnIdList;		
	}
	
	private List<BigInteger> getCaseIdList(Set<BigInteger> paxIdSet)
	{
		List<BigInteger> caseIdList = new ArrayList<>();
		if (paxIdSet != null && !paxIdSet.isEmpty())
                {
                    String sqlQuery = " SELECT id from cases where paxId in :paxIdList ";
                    Query query = em.createNativeQuery(sqlQuery);
                    query.setParameter("paxIdList", paxIdSet);

                    caseIdList = query.getResultList();
                }
		
		return caseIdList;		
		
	}
	
	private List<BigInteger> getHitsSummaryIdList(Set<BigInteger> paxIdSet)
	{
		List<BigInteger> hitsSummaryIdList = new ArrayList<>();
		if (paxIdSet != null && !paxIdSet.isEmpty())
                {
                    String sqlQuery = " SELECT id from hits_summary where passenger_id in  :paxIdList ";
                    Query query = em.createNativeQuery(sqlQuery);
                    query.setParameter("paxIdList", paxIdSet);

                    hitsSummaryIdList = query.getResultList();
                }
		
		return hitsSummaryIdList;		
		
	}
	
	private List<BigInteger> getHitDispositionIdList(List<BigInteger> caseIdList)
	{
		List<BigInteger> hitDispositionIdList = new ArrayList<>();
		
 		if (caseIdList != null && !caseIdList.isEmpty())
                {               
                    String sqlQuery = " SELECT id from hits_disposition where case_id in  :caseIdList ";
                    Query query = em.createNativeQuery(sqlQuery);
                    query.setParameter("caseIdList", caseIdList);

                    hitDispositionIdList = query.getResultList();
                }
		return hitDispositionIdList;		

	}
        
        private List<BigInteger> getMessagesWithErrorBeforeDate(LocalDate localDate)
        {
            List<BigInteger> messagesWithError = new ArrayList<>();
            
            String sqlQuery = " SELECT id from message where create_date < :localDate and error is not null ";
            Query query = em.createNativeQuery(sqlQuery);
            query.setParameter("localDate", localDate);
            
            messagesWithError = query.getResultList();

            return messagesWithError;
        }
	
	private void writeActionToAuditLog(LocalDate localDate, User currentUser)
	{
		String auditLogMessage = "Data truncation: all message data before " + localDate.toString() + " was deleted.";
		auditLogPersistenceService.create(AuditActionType.DATA_TRUNCATION,"Data Management", null, auditLogMessage, currentUser);
		
	}

	public Map<String, Collection<BigInteger>> getInClauseIdListsMap() {
		return inClauseIdListsMap;
	}

	public List<List<String>> getSqlDeleteElements() {
		return sqlDeleteElements;
	}

}
