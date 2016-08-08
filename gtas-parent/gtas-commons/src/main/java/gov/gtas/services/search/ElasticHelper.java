/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.search;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

import gov.gtas.model.ApisMessage;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.Pnr;
import gov.gtas.util.LobUtils;

@Repository
public class ElasticHelper {
	public static final String INDEX_NAME = "gtas";

	private Client client;
	
	private Gson gson = new Gson();

	@PostConstruct
	public void initIt() throws Exception {
		try {
			this.client = TransportClient.builder().build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		} catch (UnknownHostException | NoNodeAvailableException e) {
			e.printStackTrace();
		}
	}

	@PreDestroy
	public void shutdown() {
		if (this.client != null) {
			this.client.close();
		}
	}

	public void indexPnr(Pnr pnr) {
		String raw = LobUtils.convertClobToString(pnr.getRaw());
		indexFlightPax(pnr.getFlights(), pnr.getPassengers(), raw);
	}

	public void indexApis(ApisMessage apis) {
		String raw = LobUtils.convertClobToString(apis.getRaw());		
		indexFlights(apis.getFlights(), raw);
		indexPassengers(apis.getPassengers(), raw);
	}
	
	public void search(String query, int pageNumber) {
		final int PAGE_SIZE = 10;
        int startIndex = (pageNumber - 1) * PAGE_SIZE;

		SearchResponse response = client.prepareSearch(INDEX_NAME)
                .setTypes("flightpax")
			    .setSearchType(SearchType.QUERY_AND_FETCH)
			    .setQuery(QueryBuilders.matchPhrasePrefixQuery("raw", query))
			    .setFrom(startIndex)
			    .setSize(PAGE_SIZE)
			    .setExplain(true)
			    .execute()
			    .actionGet();
		
		SearchHit[] results = response.getHits().getHits();
		for (SearchHit hit : results) {
			System.out.println(hit.getId());
			Map<String, Object> result = hit.getSource();
		}	
	}
	
	private void indexFlights(Collection<Flight> flights, String raw) {
		for (Flight f : flights) {
			IndexRequest indexRequest = new IndexRequest(INDEX_NAME, "flight", String.valueOf(f.getId()));		
			IndexedFlightVo vo = new IndexedFlightVo();
			BeanUtils.copyProperties(f, vo);
			vo.setRaw(raw);
			indexRequest.source(gson.toJson(vo));
			IndexResponse response = this.client.index(indexRequest).actionGet();
		}
	}

	private void indexPassengers(Collection<Passenger> passengers, String raw) {
		for (Passenger p : passengers) {
			IndexRequest indexRequest = new IndexRequest(INDEX_NAME, "passenger", String.valueOf(p.getId()));		
			IndexedPassengerVo vo = new IndexedPassengerVo();
			BeanUtils.copyProperties(p, vo);
			vo.setRaw(raw);
			indexRequest.source(gson.toJson(vo));
			IndexResponse response = this.client.index(indexRequest).actionGet();
		}
	}

	private void indexFlightPax(Collection<Flight> flights, Collection<Passenger> passengers, String raw) {
		for (Passenger p : passengers) {
			for (Flight f : flights) {
				String id = String.format("%s-%s", String.valueOf(p.getId()), String.valueOf(f.getId()));
				IndexRequest indexRequest = new IndexRequest(INDEX_NAME, "flightpax", id);		
				FlightPassengerVo vo = new FlightPassengerVo();
				BeanUtils.copyProperties(p, vo);
				vo.setPassengerId(p.getId());
				BeanUtils.copyProperties(f, vo);
				vo.setFlightId(f.getId());
				vo.setRaw(raw);
				indexRequest.source(gson.toJson(vo));
				IndexResponse response = this.client.index(indexRequest).actionGet();
			}
		}
	}


}
