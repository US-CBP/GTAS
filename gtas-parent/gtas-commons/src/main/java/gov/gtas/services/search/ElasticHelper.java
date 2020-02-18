/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.search;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PreDestroy;

import gov.gtas.config.ElasticConfig;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.model.Address;
import gov.gtas.model.Document;
import gov.gtas.model.Passenger;
import gov.gtas.services.dto.AdhocQueryDto;
import gov.gtas.services.dto.LinkAnalysisDto;
import gov.gtas.vo.passenger.DocumentVo;
import gov.gtas.vo.passenger.LinkPassengerVo;

/**
 * Methods for interfacing with elastic search engine: indexing and search.
 * Clients are responsible for initializing the client prior to usage.
 */
@Service
public class ElasticHelper {
	private static final Logger logger = LoggerFactory.getLogger(ElasticHelper.class);
	private static final String INDEX_NAME = "flightpax";
	private static final String FLIGHTPAX_TYPE = "doc";
	private static final String CREDENTIALS = "xpack.security.user";
	private static final String NODE_NAME = "node.name";
	private static final String CLUSTER_NAME = "cluster.name";
	private static final String SSL_KEY = "xpack.security.transport.ssl.key";
	private static final String SSL_CERT = "xpack.security.transport.ssl.certificate";
	private static final String SSL_CA = "xpack.security.transport.ssl.certificate_authorities";
	private static final String VERIFICATION_MODE = "xpack.security.transport.ssl.verification_mode";
	private static final String SSL_ENABLED = "xpack.security.transport.ssl.enabled";

	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm";
	private SimpleDateFormat dateParser = new SimpleDateFormat(DATE_FORMAT);

	private TransportClient client;

	@Autowired
	private ElasticConfig elasticConfig;

	void initClient() {
		if (isUp()) {
			return;
		}

		String hostname = elasticConfig.getElasticHost();
		String portStr = elasticConfig.getElasticPort();

		if (hostname == null || portStr == null) {
			logger.info("ElasticSearch configuration not found");
			return;
		}
		System.setProperty("es.set.netty.runtime.available.processors", "false");
		int port = Integer.valueOf(portStr);
		client = new PreBuiltXPackTransportClient(Settings.builder()
				.put(SSL_ENABLED, elasticConfig.getSslEnabled())
				.put(CREDENTIALS, elasticConfig.getElasticCredentials())
				.put(NODE_NAME, elasticConfig.getElasticNodeName())
				.put(CLUSTER_NAME, elasticConfig.getElasticClusterName())
				.put(SSL_KEY, elasticConfig.getElasticSslKey())
				.put(SSL_CERT, elasticConfig.getElasticSslCert())
				.put(SSL_CA, elasticConfig.getElasticSslCa())
				.put(VERIFICATION_MODE, elasticConfig.getElasticSslVerificationMode()).build());

		logger.info("ElasticSearch Client Init: " + hostname + ":" + port);
		try {
			client.addTransportAddress(new TransportAddress(InetAddress.getByName(hostname), port));
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
	private void closeClient() {
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

	AdhocQueryDto searchPassengers(String query, int pageNumber, int pageSize, String column, String dir)
			throws ParseException {
		ArrayList<FlightPassengerVo> rv = new ArrayList<>();
		SearchHits results = search(query, pageNumber, pageSize, column, dir);
		SearchHit[] resultsArry = results.getHits();
		for (SearchHit hit : resultsArry) {
			Map<String, Object> result = hit.getSourceAsMap();
			FlightPassengerVo vo = new FlightPassengerVo();
			rv.add(vo);

			int paxId = (Integer) result.get("passengerId");
			vo.setPassengerId(new Long(paxId));
			int flightId = (Integer) result.get("flightId");
			vo.setFlightId(new Long(flightId));
			vo.setPassengerType((String) result.get("passengerType"));
			vo.setFirstName((String) result.get("firstName"));
			vo.setLastName((String) result.get("lastName"));
			vo.setMiddleName((String) result.get("middleName"));
			String flightNumber = (String) result.get("carrier") + (String) result.get("flightNumber");
			vo.setFlightNumber(flightNumber);
			vo.setOrigin((String) result.get("origin"));
			vo.setDestination((String) result.get("destination"));
			try {
				vo.setEta(dateParser.parse((String) result.get("eta")));
				vo.setEtd(dateParser.parse((String) result.get("etd")));
			} catch (ParseException e) {
				logger.error("Failed to parse date searching for passengers", e);
			}
			if (result.get("addresses") != null) {
				Set<Address> addresses = new HashSet<>();
				for (HashMap m : ((ArrayList<HashMap>) result.get("addresses"))) {
					Address temp = new Address();
					temp.setLine1((String) m.get("line1"));
					temp.setLine2((String) m.get("line2"));
					temp.setLine3((String) m.get("line3"));
					temp.setCity((String) m.get("city"));
					temp.setCountry((String) m.get("country"));
					temp.setPostalCode((String) m.get("postalCode"));
					temp.setState((String) m.get("state"));
					addresses.add(temp);
				}
				vo.setAddresses(addresses);
			}
			if (result.get("documents") != null) {
				Set<DocumentVo> documents = new HashSet<>();
				for (HashMap m : ((ArrayList<HashMap>) result.get("documents"))) {
					DocumentVo temp = new DocumentVo();
					temp.setDocumentNumber((String) m.get("documentNumber"));
					temp.setDocumentType((String) m.get("documentType"));
					if (m.get("expirationDate") != null) {
						temp.setExpirationDate(dateParser.parse((String) m.get("expirationDate")));
					}
					temp.setFirstName((String) m.get("firstName"));
					temp.setLastName((String) m.get("lastName"));
					temp.setIssuanceCountry((String) m.get("issuanceCountry"));
					if (m.get("issuanceDate") != null) {
						temp.setIssuanceDate(dateParser.parse((String) m.get("issuanceDate")));
					}
					documents.add(temp);
				}
				vo.setDocuments(documents);
			}
		}
		rv.sort((o1, o2) -> o1.getEta().compareTo(o2.getEta()) * -1);
	
		return new AdhocQueryDto(rv, results.getHits().length);
	}

	LinkAnalysisDto findPaxLinks(Passenger pax, int pageNumber, int pageSize, String column, String dir)
			throws ParseException {
		if (pax == null) {
			return new LinkAnalysisDto(new ArrayList(), 0);
		}
		SortOrder sortOrder = ("asc".equals(dir.toLowerCase())) ? SortOrder.ASC : SortOrder.DESC;
		int startIndex = (pageNumber - 1) * pageSize;

		List<String> docNumbers = new ArrayList<>();
		for (Document d : pax.getDocuments()) {
			docNumbers.add(d.getDocumentNumber());
		}

		QueryBuilder qb = QueryBuilders.boolQuery()
				.should(QueryBuilders.boolQuery().must(matchQuery("pnr", pax.getPassengerDetails().getFirstName()))
						.must(matchQuery("pnr", pax.getPassengerDetails().getLastName())))
				.should(termsQuery("documents.documentNumber", docNumbers)).should(termsQuery("pnr", docNumbers));

		// upgrade to 5.6.0
		HighlightBuilder builder = new HighlightBuilder();
		builder.field("documents.documentNumber").field("pnr").preTags();
		SearchResponse response = client.prepareSearch(INDEX_NAME).setTypes(FLIGHTPAX_TYPE)
				.setSearchType(SearchType.QUERY_THEN_FETCH).setQuery(qb)
				// .addHighlightedField("documents.documentNumber")
				// .addHighlightedField("pnr")
				.highlighter(builder).setFrom(startIndex).setSize(pageSize).addSort(column, sortOrder).setExplain(true)
				.execute().actionGet();

		SearchHits hits = response.getHits();
		SearchHit[] resultsArry = hits.getHits();

		List<LinkPassengerVo> lp = new ArrayList<>();
		for (SearchHit hit : resultsArry) {
			Map<String, Object> result = hit.getSourceAsMap();
			if (!((String) result.get("firstName")).equals(pax.getPassengerDetails().getFirstName())
					|| !((String) result.get("lastName")).equals(pax.getPassengerDetails().getLastName())) {
				LinkPassengerVo lpVo = new LinkPassengerVo();
				lpVo.setPassengerId((Integer) result.get("passengerId"));
				lpVo.setFlightId((Integer) result.get("flightId"));
				lpVo.setFirstName((String) result.get("firstName"));
				lpVo.setMiddleName((String) result.get("middleName"));
				lpVo.setLastName((String) result.get("lastName"));
				lpVo.setFlightNumber((String) result.get("flightNumber"));
				lpVo.setHighlightMatch(converHighlightsTable(hit.getHighlightFields().entrySet()));
				// Need to normalize for intuitive consideration
				lpVo.setScore(hit.getExplanation().getValue().floatValue());
				lp.add(lpVo);
			}
		}
		return new LinkAnalysisDto(lp, hits.getHits().length);
	}

	private Hashtable<String, List<String>> converHighlightsTable(Set<Entry<String, HighlightField>> highlights) {
		Hashtable<String, List<String>> highlightStrings = new Hashtable<String, List<String>>();
		for (Map.Entry<String, HighlightField> highlight : highlights) {
			List<String> fragments = new ArrayList<String>();
			for (Text text : highlight.getValue().fragments()) {
				fragments.add(text.string());
			}
			highlightStrings.put(highlight.getKey(), fragments);
		}
		return highlightStrings;
	}

	private SearchHits search(String query, int pageNumber, int pageSize, String column, String dir) {
		SortOrder sortOrder = ("asc".equals(dir.toLowerCase())) ? SortOrder.ASC : SortOrder.DESC;

		final String[] searchFields = { "apis", "pnr", "firstName", "lastName", "carrier", "flightNumber", "origin",
				"destination", "addresses", "documents" };

		int startIndex = (pageNumber - 1) * pageSize;
		QueryBuilder qb = QueryBuilders.multiMatchQuery(query, searchFields)
				.type(MultiMatchQueryBuilder.Type.MOST_FIELDS);
		SearchResponse response = client.prepareSearch(INDEX_NAME)
				.setSearchType(SearchType.QUERY_THEN_FETCH).setQuery(qb).setFrom(startIndex).setSize(pageSize)
				.addSort(column, sortOrder).setExplain(true).execute().actionGet();
		return response.getHits();
	}

}