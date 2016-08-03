/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

import gov.gtas.model.ApisMessage;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.Pnr;
import gov.gtas.util.LobUtils;

@Repository
public class ElasticIndexer {
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
		indexFlights(pnr.getFlights(), raw);
		indexPassengers(pnr.getPassengers(), raw);
	}

	public void indexApis(ApisMessage apis) {
		String raw = LobUtils.convertClobToString(apis.getRaw());		
		indexFlights(apis.getFlights(), raw);
		indexPassengers(apis.getPassengers(), raw);
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
}
