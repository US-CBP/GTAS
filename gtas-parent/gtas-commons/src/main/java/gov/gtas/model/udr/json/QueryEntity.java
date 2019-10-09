/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.udr.json;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Query detail content marker interface.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes({ @JsonSubTypes.Type(QueryTerm.class), @JsonSubTypes.Type(QueryObject.class) })
public interface QueryEntity extends Serializable {
	/**
	 * Creates a canonical form of the query expression as sum of "minterms". Each
	 * "minterm" is a list of QueryTerm which can be converted to a Drools rule.
	 * 
	 * @return
	 */
	List<List<QueryTerm>> createFlattenedList();
}
