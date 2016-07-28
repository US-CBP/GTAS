/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

import gov.gtas.parsers.vo.MessageVo;

@Repository
public class ElasticIndexer {
	public static final String INDEX_NAME = "gtas";

	private Client client;

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

	public void indexRaw(MessageVo parsedMessage) {
		IndexRequest indexRequest = new IndexRequest(INDEX_NAME, "raw");
		IndexedRaw raw = new IndexedRaw();
		raw.setType(parsedMessage.getMessageType());
		raw.setRaw(parsedMessage.getRaw());

		String json = new Gson().toJson(raw);
		indexRequest.source(json);
		IndexResponse response = this.client.index(indexRequest).actionGet();
	}
}
