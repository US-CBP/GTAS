/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.model;

import gov.gtas.model.udr.json.QueryObject;
import gov.gtas.services.dto.SortOptionsDto;

public class QueryRequest {
	private int pageNumber;
	private int pageSize;
	private SortOptionsDto sort;
	private QueryObject query;

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public SortOptionsDto getSort() {
		return sort;
	}

	public void setSort(SortOptionsDto sort) {
		this.sort = sort;
	}

	public QueryObject getQuery() {
		return query;
	}

	public void setQuery(QueryObject query) {
		this.query = query;
	}
}
