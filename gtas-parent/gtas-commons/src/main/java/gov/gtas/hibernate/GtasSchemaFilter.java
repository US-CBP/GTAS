/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.hibernate;

import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.spi.SchemaFilter;

import java.util.HashSet;
import java.util.Set;

//For filtering out custom views on hibernate create.
public class GtasSchemaFilter implements SchemaFilter {

	static final GtasSchemaFilter INSTANCE = new GtasSchemaFilter();
	private static final Set<String>  whiteList = new HashSet<String>() {{
		add("flight_countdown_view");
		add("hits_summary_view");
		add("flight_passenger_count_view");
		add("flight_hits_wl_view");
		add("flight_hits_rule_view");
		add("flight_hits_fuzzy_view");
		add("flight_hits_graph_view");
	}};
	@Override
	public boolean includeNamespace(Namespace namespace) {
		return true;
	}

	@Override
	public boolean includeTable(Table table) {
		return !whiteList.contains(table.getName());
	}

	@Override
	public boolean includeSequence(Sequence sequence) {
		return true;
	}
}
