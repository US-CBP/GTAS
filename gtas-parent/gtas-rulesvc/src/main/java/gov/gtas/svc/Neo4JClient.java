package gov.gtas.svc;

import gov.gtas.model.GraphRule;
import org.neo4j.driver.v1.*;

import java.util.*;
import java.util.stream.Collectors;

class Neo4JClient implements AutoCloseable {

	private Driver driver;

	Neo4JClient(String url, String username, String password) {
		driver = GraphDatabase.driver(url, AuthTokens.basic(username, password));
	}

	@Override
	public void close() {
		driver.close();
	}

	Set<String> runQueryAndReturnPassengerIdHits(GraphRule graphRule, Set<String> paxIds) {

		final String cipherQuery = graphRule.getCipherQuery();
		Set<String> paxList;
		try (Session session = driver.session()) {
			List<Record> neo4JResults;
			Map<String, Object> queryParameters = graphRule.getParameterMap(paxIds);
			neo4JResults = session.readTransaction(transaction -> {
				StatementResult result = transaction.run(cipherQuery, queryParameters);
				return result.list();
			});

			paxList = neo4JResults.stream().filter(r -> r.asMap().get("p.id_tag") != null)
					.map(r -> r.asMap().get("p.id_tag").toString()).collect(Collectors.toSet());
		}
		return paxList;
	}

}
