/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.search;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import gov.gtas.model.ApisMessage;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.Pnr;
import gov.gtas.services.dto.AdhocQueryDto;
import gov.gtas.util.LobUtils;
import gov.gtas.vo.passenger.PassengerVo;

@Service
public class ElasticHelper {
	private static final Logger logger = LoggerFactory.getLogger(ElasticHelper.class);
	public static final String INDEX_NAME = "gtas";
	public static final String FLIGHTPAX_TYPE = "flightpax";

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
		String raw = LobUtils.convertClobToString(pnr.getRaw());
		indexFlightPax(pnr.getFlights(), pnr.getPassengers(), raw);
	}

	public void indexApis(ApisMessage apis) {
		if (isDown()) {
			return;
		}
		String raw = LobUtils.convertClobToString(apis.getRaw());		
		indexFlightPax(apis.getFlights(), apis.getPassengers(), raw);
	}
	
	public AdhocQueryDto searchPassengers(String query, int pageNumber, int pageSize) {
		ArrayList<PassengerVo> rv = new ArrayList<>();
		if (isDown()) { 
			return new AdhocQueryDto(null, 0); 
		}

		SearchHits results = search(query, pageNumber, pageSize);
		SearchHit[] resultsArry = results.getHits();
		for (SearchHit hit : resultsArry) {
			Map<String, Object> result = hit.getSource();
			PassengerVo vo = new PassengerVo();
			rv.add(vo);

			int paxId = (Integer)result.get("passengerId");
			vo.setId(new Long(paxId));
			int flightId = (Integer)result.get("flightId");
			vo.setFlightId(String.valueOf(flightId));
			vo.setPassengerType((String)result.get("passengerType"));
			vo.setFirstName((String)result.get("firstName"));
			vo.setLastName((String)result.get("lastName"));
			vo.setMiddleName((String)result.get("middleName"));
			String flightNumber = (String)result.get("carrier") + (String)result.get("flightNumber");
			vo.setFlightNumber(flightNumber);
			vo.setFlightOrigin((String)result.get("origin"));
			vo.setFlightDestination((String)result.get("destination"));
		}	

		return new AdhocQueryDto(rv, results.getTotalHits());
	}
	
	private SearchHits search(String query, int pageNumber, int pageSize) {
        int startIndex = (pageNumber - 1) * pageSize;
		SearchResponse response = client.prepareSearch(INDEX_NAME)
                .setTypes(FLIGHTPAX_TYPE)
			    .setSearchType(SearchType.QUERY_THEN_FETCH)
			    .setQuery(QueryBuilders.matchPhrasePrefixQuery("raw", query))
			    .setFrom(startIndex)
			    .setSize(pageSize)
			    .setExplain(true)
			    .execute()
			    .actionGet();
		
		return response.getHits();
	}
	
	private void indexFlightPax(Collection<Flight> flights, Collection<Passenger> passengers, String raw) {
		Gson gson = new Gson();
		for (Passenger p : passengers) {
			for (Flight f : flights) {
				String id = String.format("%s-%s", String.valueOf(f.getId()), String.valueOf(p.getId()));
				IndexRequest indexRequest = new IndexRequest(INDEX_NAME, FLIGHTPAX_TYPE, id);		
				FlightPassengerVo vo = new FlightPassengerVo();
				BeanUtils.copyProperties(p, vo);
				vo.setPassengerId(p.getId());
				BeanUtils.copyProperties(f, vo);
				vo.setFlightId(f.getId());
				vo.setRaw(raw);
				indexRequest.source(gson.toJson(vo));
				client.index(indexRequest).actionGet();
			}
		}
	}
}
