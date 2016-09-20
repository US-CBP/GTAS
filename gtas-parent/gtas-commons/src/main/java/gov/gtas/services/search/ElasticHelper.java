/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.search;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gov.gtas.model.ApisMessage;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.Pnr;
import gov.gtas.services.dto.AdhocQueryDto;
import gov.gtas.util.LobUtils;

@Service
public class ElasticHelper {
	private static final Logger logger = LoggerFactory.getLogger(ElasticHelper.class);
	public static final String INDEX_NAME = "gtas";
	public static final String FLIGHTPAX_TYPE = "flightpax";

	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm";
	private static SimpleDateFormat dateParser = new SimpleDateFormat(DATE_FORMAT);

	private TransportClient client;
	
	@PostConstruct
	public void initClient() throws Exception {
		logger.info("ElasticSearch Client Init");		
		this.client = TransportClient.builder().build();
		this.client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		List<DiscoveryNode> nodes = this.client.connectedNodes();
		if (nodes.isEmpty()) {
			logger.warn("Init failed: ElasticSearch not available");
			closeClient();
			this.client = null;
		}
	}

	@PreDestroy
	public void closeClient() {
		logger.info("Closing ElasticSearch client");		
		if (isDown()) { 
			return; 
		}
		this.client.close();
	}
	
	public boolean isDown() {
		boolean isDown = this.client == null;
		if (isDown) {
			logger.warn("ElasticSearch not available");
		}
		return isDown;
	}

	public void indexPnr(Pnr pnr) {
		if (isDown()) { 
			return; 
		}
		String pnrRaw = LobUtils.convertClobToString(pnr.getRaw());
		indexFlightPax(pnr.getFlights(), pnr.getPassengers(), null, pnrRaw);
	}

	public void indexApis(ApisMessage apis) {
		if (isDown()) {
			return;
		}
		String apisRaw = LobUtils.convertClobToString(apis.getRaw());		
		indexFlightPax(apis.getFlights(), apis.getPassengers(), apisRaw, null);
	}
	
	public AdhocQueryDto searchPassengers(String query, int pageNumber, int pageSize, String column, String dir) {
		ArrayList<FlightPassengerVo> rv = new ArrayList<>();
		if (isDown()) { 
			return new AdhocQueryDto(null, 0); 
		}
		
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
				Date etd = dateParser.parse((String)result.get("etd"));
				vo.setEtd(etd);
				Date eta = dateParser.parse((String)result.get("eta"));
				vo.setEta(eta);
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
		}	

		return new AdhocQueryDto(rv, results.getTotalHits());
	}
	
	private SearchHits search(String query, int pageNumber, int pageSize, String column, String dir) {
		SortOrder sortOrder = ("asc".equals(dir.toLowerCase())) ? SortOrder.ASC : SortOrder.DESC;
		
        int startIndex = (pageNumber - 1) * pageSize;
        QueryBuilder qb = QueryBuilders
        		.multiMatchQuery(query, "apis", "pnr")
        		.type(MatchQueryBuilder.Type.PHRASE_PREFIX);
        
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
	
	private void indexFlightPax(Collection<Flight> flights, Collection<Passenger> passengers, String apis, String pnr) {
		Gson gson = new GsonBuilder()
				.setDateFormat(DATE_FORMAT)
				.create();
		for (Passenger p : passengers) {
			for (Flight f : flights) {
				String id = String.format("%s-%s", String.valueOf(f.getId()), String.valueOf(p.getId()));
				IndexRequest indexRequest = new IndexRequest(INDEX_NAME, FLIGHTPAX_TYPE, id);		
				FlightPassengerVo vo = new FlightPassengerVo();
				BeanUtils.copyProperties(p, vo);
				vo.setPassengerId(p.getId());
				BeanUtils.copyProperties(f, vo);
				vo.setFlightId(f.getId());
				if (apis != null) {
					vo.setApis(apis);
				}
				if (pnr != null) {
					vo.setPnr(pnr);
				}
				indexRequest.source(gson.toJson(vo));
				client.index(indexRequest).actionGet();
			}
		}
	}
}
