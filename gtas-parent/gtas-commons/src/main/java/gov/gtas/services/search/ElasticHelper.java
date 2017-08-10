/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.search;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PreDestroy;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
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
import gov.gtas.util.LobUtils;
import gov.gtas.vo.passenger.DocumentVo;

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
		indexFlightPax(pnr.getFlights(), pnr.getPassengers(), null, pnrRaw, null, pnr);
	}

	public void indexApis(ApisMessage apis) {
		String apisRaw = LobUtils.convertClobToString(apis.getRaw());		
		indexFlightPax(apis.getFlights(), apis.getPassengers(), apisRaw, null,apis,null);
	}
	
	public AdhocQueryDto searchPassengers(String query, int pageNumber, int pageSize, String column, String dir) {	
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
			vo.setAddresses((Set<Address>) result.get("addresses"));
			List<DocumentVo> documents = new ArrayList<>();
			for(Document d: ((Set<Document>) result.get("documents"))) {
				DocumentVo temp = new DocumentVo();
				temp.setDocumentNumber(d.getDocumentNumber());
				temp.setDocumentType(d.getDocumentType());
				temp.setExpirationDate(d.getExpirationDate());
				temp.setFirstName(d.getPassenger().getFirstName());
				temp.setLastName(d.getPassenger().getLastName());
				temp.setIssuanceCountry(d.getIssuanceCountry());
				temp.setIssuanceDate(d.getIssuanceDate());
				vo.addDocument(temp);
			}
			try {
				Date etd = dateParser.parse((String)result.get("etd"));
				vo.setEtd(etd);
				Date eta = dateParser.parse((String)result.get("eta"));
				vo.setEta(eta);
			} catch (java.text.ParseException e) {
				logger.error("date parsing error", e);
			}
		}	

		return new AdhocQueryDto(rv, results.getTotalHits());
	}
	
	////////////////////////////////////////////////////////////////////
	// helpers
	
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
						temp.setDocumentNumber(d.getDocumentNumber());
						temp.setDocumentType(d.getDocumentType());
						temp.setExpirationDate(d.getExpirationDate());
						temp.setFirstName(d.getPassenger().getFirstName());
						temp.setLastName(d.getPassenger().getLastName());
						temp.setIssuanceCountry(d.getIssuanceCountry());
						temp.setIssuanceDate(d.getIssuanceDate());
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
