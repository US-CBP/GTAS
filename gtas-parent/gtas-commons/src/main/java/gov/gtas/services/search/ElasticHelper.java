/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.search;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PreDestroy;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gov.gtas.model.Address;
import gov.gtas.model.ApisMessage;
import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.Pnr;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.repository.LookUpRepository;
import gov.gtas.services.dto.AdhocQueryDto;
import gov.gtas.services.dto.LinkAnalysisDto;
import gov.gtas.util.LobUtils;
import gov.gtas.vo.passenger.AddressVo;
import gov.gtas.vo.passenger.DocumentVo;
import gov.gtas.vo.passenger.LinkPassengerVo;
import gov.gtas.vo.passenger.PassengerVo;

/**
 * Methods for interfacing with elastic search engine: indexing
 * and search.  Clients are responsible for initializing the client
 * prior to usage.  
 */
@Service
public class ElasticHelper {
	private static final Logger logger = LoggerFactory.getLogger(ElasticHelper.class);
	public static final String INDEX_NAME = "gtas";
	public static final String FLIGHTPAX_TYPE = "flightpax";

	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm";
	private static SimpleDateFormat dateParser = new SimpleDateFormat(DATE_FORMAT);

	private TransportClient client;
	
    @Autowired
    private LookUpRepository lookupRepo;

	////////////////////////////////////////////////////////////////////
	// init methods
	
    public void initClient() {
		if (isUp()) {
			return;
		}

        String hostname = lookupRepo.getAppConfigOption(AppConfigurationRepository.ELASTIC_HOSTNAME);
        String portStr = lookupRepo.getAppConfigOption(AppConfigurationRepository.ELASTIC_PORT);
        if (hostname == null || portStr == null) {
    		logger.info("ElasticSearch configuration not found");
        	return;
        }

		int port = Integer.valueOf(portStr);
		client = TransportClient.builder().build();
		logger.info("ElasticSearch Client Init: " + hostname + ":" + port);
		try {
			client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostname), port));
		} catch (UnknownHostException e) {
			logger.error("unknown elastic host", e);
			closeClient();
			return;
		}

		List<DiscoveryNode> nodes = client.connectedNodes();
		if (nodes.isEmpty()) {
			logger.warn("Init failed: ElasticSearch not available");
			closeClient();
			return;
		}
		
		// initialize index
		boolean indexExists = client.admin().indices().prepareExists(INDEX_NAME).execute().actionGet().isExists();
	    if (!indexExists) {
			logger.info("ElasticSearch: Initializing index " + INDEX_NAME);
	    	client.admin().indices().prepareCreate(INDEX_NAME).execute().actionGet();
	    }
	}

	@PreDestroy
	public void closeClient() {
		logger.info("Closing ElasticSearch client");		
		if (isDown()) { 
			return; 
		}
		client.close();
		client = null;
	}
	
	
	public boolean isUp() {
		return !isDown();
	}

	public boolean isDown() {
		return client == null;
	}		

	////////////////////////////////////////////////////////////////////
	// index, search

	public void indexPnr(Pnr pnr) {
		String pnrRaw = LobUtils.convertClobToString(pnr.getRaw());
		if(!(pnr == null || pnr.getFlights() == null || pnr.getPassengers() == null )){
			indexFlightPax(pnr.getFlights(), pnr.getPassengers(), null, pnrRaw, null, pnr);
		}
		
	}

	public void indexApis(ApisMessage apis) {
		String apisRaw = LobUtils.convertClobToString(apis.getRaw());
		if(!(apis == null || apis.getFlights() == null || apis.getPassengers() == null )){
			indexFlightPax(apis.getFlights(), apis.getPassengers(), apisRaw, null,apis,null);
		}
		
	}
	
	public AdhocQueryDto searchPassengers(String query, int pageNumber, int pageSize, String column, String dir) throws ParseException {
		ArrayList<FlightPassengerVo> rv = new ArrayList<>();
		SearchHits results = search(query, pageNumber, pageSize, column, dir);
		SearchHit[] resultsArry = results.getHits();
		for (SearchHit hit : resultsArry) {
			Map<String, Object> result = hit.getSource();
			FlightPassengerVo vo = new FlightPassengerVo();
			rv.add(vo);

			int paxId = (Integer)result.get("passengerId");
			vo.setPassengerId(new Long(paxId));
			int flightId = (Integer)result.get("flightId");
			vo.setFlightId(new Long(flightId));
			vo.setPassengerType((String)result.get("passengerType"));
			vo.setFirstName((String)result.get("firstName"));
			vo.setLastName((String)result.get("lastName"));
			vo.setMiddleName((String)result.get("middleName"));
			String flightNumber = (String)result.get("carrier") + (String)result.get("flightNumber");
			vo.setFlightNumber(flightNumber);
			vo.setOrigin((String)result.get("origin"));
			vo.setDestination((String)result.get("destination"));
			try {
				vo.setEta(dateParser.parse((String)result.get("eta")));
				vo.setEtd(dateParser.parse((String)result.get("etd")));
			} catch (ParseException e) {
				logger.error("Failed to parse date searching for passengers", e);
			}
			if(result.get("addresses")!=null) {
				Set<Address> addresses = new HashSet<>();
				for(HashMap m: ((ArrayList<HashMap>) result.get("addresses"))) {
					Address temp = new Address();
					temp.setLine1((String)m.get("line1"));
					temp.setLine2((String)m.get("line2"));
					temp.setLine3((String)m.get("line3"));
					temp.setCity((String)m.get("city"));
					temp.setCountry((String)m.get("country"));
					temp.setPostalCode((String)m.get("postalCode"));
					temp.setState((String)m.get("state"));
					addresses.add(temp);
				}
				vo.setAddresses(addresses);
			}
			if(result.get("documents") != null) {
				Set<DocumentVo> documents = new HashSet<>();
				for(HashMap m: ((ArrayList<HashMap>) result.get("documents"))) {
					DocumentVo temp = new DocumentVo();
					temp.setDocumentNumber((String) m.get("documentNumber"));
					temp.setDocumentType((String) m.get("documentType"));
					if(m.get("expirationDate")!=null) {
						temp.setExpirationDate(dateParser.parse((String) m.get("expirationDate")));
					}
					temp.setFirstName((String) m.get("firstName"));
					temp.setLastName((String) m.get("lastName"));
					temp.setIssuanceCountry((String) m.get("issuanceCountry"));
					if(m.get("issuanceDate")!=null) {
						temp.setIssuanceDate(dateParser.parse((String) m.get("issuanceDate")));
					}
					documents.add(temp);
				}
				vo.setDocuments(documents);
			}
		}	
		rv.sort((o1,o2)->o1.getEta().compareTo(o2.getEta())*-1);
		return new AdhocQueryDto(rv, results.getTotalHits());
	}
	
	public LinkAnalysisDto findPaxLinks(Passenger pax, int pageNumber, int pageSize, String column, String dir) throws ParseException {	
		if(pax==null) {
			return new LinkAnalysisDto(new ArrayList(), 0);
		}
		SortOrder sortOrder = ("asc".equals(dir.toLowerCase())) ? SortOrder.ASC : SortOrder.DESC;
		int startIndex = (pageNumber - 1) * pageSize;
		
		List<String> docNumbers = new ArrayList<>();
		for(Document d:pax.getDocuments()) {
			docNumbers.add(d.getDocumentNumber());
		}
		
		QueryBuilder qb = QueryBuilders
				.boolQuery()
				.should(QueryBuilders.boolQuery()
						.must(matchQuery("pnr",pax.getPassengerDetails().getFirstName()))
						.must(matchQuery("pnr",pax.getPassengerDetails().getLastName())))
				.should(termsQuery("documents.documentNumber", docNumbers))
				.should(termsQuery("pnr", docNumbers));
		
		SearchResponse response = client.prepareSearch(INDEX_NAME)
                .setTypes(FLIGHTPAX_TYPE)
			    .setSearchType(SearchType.QUERY_THEN_FETCH)
			    .setQuery(qb)
			    .addHighlightedField("documents.documentNumber")
			    .addHighlightedField("pnr")
			    .setFrom(startIndex)
			    .setSize(pageSize)
			    .addSort(column, sortOrder)
			    .setExplain(true)
			    .execute()
			    .actionGet();
		
	    SearchHits hits = response.getHits();
	    SearchHit[] resultsArry = hits.getHits();

	    List<LinkPassengerVo> lp = new ArrayList<>();
		for (SearchHit hit : resultsArry) {
			Map<String, Object> result = hit.getSource();
			if(!((String)result.get("firstName")).equals(pax.getPassengerDetails().getFirstName()) || !((String)result.get("lastName")).equals(pax.getPassengerDetails().getLastName())) {
				LinkPassengerVo lpVo = new LinkPassengerVo();
				lpVo.setPassengerId((Integer)result.get("passengerId"));
				lpVo.setFlightId((Integer)result.get("flightId"));
				lpVo.setFirstName((String)result.get("firstName"));
				lpVo.setMiddleName((String)result.get("middleName"));
				lpVo.setLastName((String)result.get("lastName"));
				lpVo.setFlightNumber((String)result.get("flightNumber"));
				lpVo.setHighlightMatch(converHighlightsTable(hit.getHighlightFields().entrySet()));
				//Need to normalize for intuitive consideration
				lpVo.setScore(hit.getExplanation().getValue());
				lp.add(lpVo);
			}
		}
		return new LinkAnalysisDto(lp, hits.totalHits());
	}
	////////////////////////////////////////////////////////////////////
	// helpers,
	private Hashtable<String, List<String>> converHighlightsTable(Set<Entry<String,HighlightField>> highlights) {
		Hashtable<String,List<String>> highlightStrings = new Hashtable<String,List<String>>();
        for (Map.Entry<String, HighlightField> highlight : highlights) {
        	List<String> fragments = new ArrayList<String>();
            for (Text text : highlight.getValue().fragments()) {
            	fragments.add(text.string());
            }
        	highlightStrings.put(highlight.getKey(), fragments);
        }
        return highlightStrings;
	}
	private String convertHighlights(Set<Entry<String,HighlightField>> highlights){
		StringBuilder highlightString = new StringBuilder();
        for (Map.Entry<String, HighlightField> highlight : highlights) {
        	highlightString.append("Field: ");
        	highlightString.append(highlight.getKey());
        	highlightString.append("\n");
            for (Text text : highlight.getValue().fragments()) {
            	highlightString.append("\n");
            	highlightString.append("Fragment: ");
                highlightString.append(text.string());
                highlightString.append("... ");
            	highlightString.append("\n");
            }
        	highlightString.append("\n");
            highlightString.append("---- ");
            highlightString.append("\n");
        }
        return highlightString.toString();
	}
	private SearchHits search(String query, int pageNumber, int pageSize, String column, String dir) {
		SortOrder sortOrder = ("asc".equals(dir.toLowerCase())) ? SortOrder.ASC : SortOrder.DESC;
		
		final String[] searchFields = {"apis", "pnr", "firstName", "lastName", "carrier", "flightNumber", "origin", "destination","addresses","documents"};
		
        int startIndex = (pageNumber - 1) * pageSize;
        QueryBuilder qb = QueryBuilders
        		.multiMatchQuery(query, searchFields)
        		.type(MultiMatchQueryBuilder.Type.MOST_FIELDS);
        SearchResponse response = client.prepareSearch(INDEX_NAME)
                    .setTypes(FLIGHTPAX_TYPE)
    			    .setSearchType(SearchType.QUERY_THEN_FETCH)
    			    .setQuery(qb)
    			    .setFrom(startIndex)
    			    .setSize(pageSize)
    			    .addSort(column, sortOrder)
    			    .setExplain(true)
    			    .execute()
    			    .actionGet();		
		return response.getHits();
	}
	
	private void indexFlightPax(Collection<Flight> flights, Collection<Passenger> passengers, String apis, String pnr, ApisMessage am, Pnr pm) {
		Gson gson = new GsonBuilder()
				.setDateFormat(DATE_FORMAT)
				.create();
		for (Passenger p : passengers) {
			for (Flight f : flights) {
				String id = createElasticId(f.getId(), p.getId());
				
				GetRequest getRequest = new GetRequest(INDEX_NAME, FLIGHTPAX_TYPE, id);
				GetResponse response = client.get(getRequest).actionGet();
				if (response.isExists()) {
					// update
					UpdateRequest updateRequest = new UpdateRequest(INDEX_NAME, FLIGHTPAX_TYPE, id);
					String field;
					String val;
					if (apis != null) {
						field = "apis";
						val = apis;
					} else {
						field = "pnr";
						val = pnr;
					}
					
					try {
						XContentBuilder builder = XContentFactory.jsonBuilder()
							.startObject()
				            .field(field, val)
					        .endObject();
						updateRequest.doc(builder);
						client.update(updateRequest).get();
					} catch (Exception e) {
						logger.error("error: ", e);
					}
					
				} else {
					// index new
					IndexRequest indexRequest = new IndexRequest(INDEX_NAME, FLIGHTPAX_TYPE, id);		
					FlightPassengerVo vo = new FlightPassengerVo();
					BeanUtils.copyProperties(p, vo);
					//Need to manually add documents to vo since it contains passengers
					Set<DocumentVo> documents = new HashSet<DocumentVo>();
					for(Document d: p.getDocuments()) {
						DocumentVo temp = new DocumentVo();
						temp.setDocumentNumber(d.getDocumentNumber()!=null?d.getDocumentNumber():"");
						temp.setDocumentType(d.getDocumentType()!=null?d.getDocumentType():"");
						temp.setExpirationDate(d.getExpirationDate()!=null?d.getExpirationDate():new Date());
						temp.setFirstName(d.getPassenger().getPassengerDetails().getFirstName()!=null?d.getPassenger().getPassengerDetails().getFirstName():"");
						temp.setLastName(d.getPassenger().getPassengerDetails().getLastName()!=null?d.getPassenger().getPassengerDetails().getLastName():"");
						temp.setIssuanceCountry(d.getIssuanceCountry()!=null?d.getIssuanceCountry():"");
						temp.setIssuanceDate(d.getIssuanceDate()!=null?d.getIssuanceDate():new Date());
						documents.add(temp);
					}
					vo.setDocuments(documents);
					vo.setPassengerId(p.getId());
					BeanUtils.copyProperties(f, vo);
					vo.setFlightId(f.getId());
					if (apis != null) {
						vo.setApis(apis);
					} else {
						vo.setPnr(pnr);
						vo.setAddresses(pm.getAddresses());
					}
					indexRequest.source(gson.toJson(vo));
					client.index(indexRequest).actionGet();
				}
			}
		}
	}

	private String createElasticId(long flightId, long paxId) {
		return String.format("%d-%d", flightId, paxId);
	}
}
