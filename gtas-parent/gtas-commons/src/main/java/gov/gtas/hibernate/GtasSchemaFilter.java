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

//For filtering out custom views on hibernate create.
public class GtasSchemaFilter implements SchemaFilter {

    static final GtasSchemaFilter INSTANCE = new GtasSchemaFilter();
    @Override
    public boolean includeNamespace(Namespace namespace) {
        return true;
    }

    @Override
    public boolean includeTable(Table table) {
        return !table.getName().contains("flight_countdown_view");
    }

    @Override
    public boolean includeSequence(Sequence sequence) {
        return true;
    }
}
