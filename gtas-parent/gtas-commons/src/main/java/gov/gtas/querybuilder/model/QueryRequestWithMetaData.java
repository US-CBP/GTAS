package gov.gtas.querybuilder.model;

import gov.gtas.model.udr.json.QueryConditionEnum;
import gov.gtas.model.udr.json.QueryEntity;
import gov.gtas.model.udr.json.QueryObject;
import gov.gtas.querybuilder.constants.Constants;
import gov.gtas.services.dto.SortOptionsDto;

public class QueryRequestWithMetaData {
	
	private QueryRequest queryRequest;
	private boolean onlyAnd = false;
	private String joinCondition;
	
	private QueryRequestWithMetaData() {};
	
	public static QueryRequestWithMetaData generate(QueryRequest queryRequest) {
		
		QueryRequestWithMetaData qrwm = new QueryRequestWithMetaData();
		qrwm.setOnlyAnd(recursivelyFindIfOnlyAndConditions(queryRequest.getQuery()));
		if (qrwm.isOnlyAnd()) {
			qrwm.setJoinCondition(Constants.JOIN);
		} else {
			qrwm.setJoinCondition(Constants.LEFT_JOIN);			
		}
		qrwm.setQueryRequest(queryRequest);
	
		return qrwm;
		
	} 

	private static boolean recursivelyFindIfOnlyAndConditions(QueryObject query) {
		
		final QueryConditionEnum condOp = QueryConditionEnum.valueOf(query.getCondition());
		if (condOp != QueryConditionEnum.AND) {
			return false;
		} else {
			for (QueryEntity qe : query.getRules()) {
				if (qe instanceof QueryObject) {
					recursivelyFindIfOnlyAndConditions((QueryObject)qe);
				}
			}
		}
		return true;
	}

	public String getJoinCondition() {
		return joinCondition;
	}

	private void setJoinCondition(String joinCondition) {
		this.joinCondition = joinCondition;
	}

	private boolean isOnlyAnd() {
		return onlyAnd;
	}

	private void setOnlyAnd(boolean onlyAnd) {
		this.onlyAnd = onlyAnd;
	}

	private QueryRequest getQueryRequest() {
		return queryRequest;
	}

	private void setQueryRequest(QueryRequest queryRequest) {
		this.queryRequest = queryRequest;
	}
	
	public int getPageNumber() {
		return this.queryRequest.getPageNumber();
	}

	public void setPageNumber(int pageNumber) {
		this.queryRequest.setPageNumber(pageNumber);
	}

	public int getPageSize() {
		return this.queryRequest.getPageSize();
	}

	public void setPageSize(int pageSize) {
		this.queryRequest.setPageSize(pageSize);
	}

	public SortOptionsDto getSort() {
		return this.queryRequest.getSort();
	}

	public void setSort(SortOptionsDto sort) {
		this.queryRequest.setSort(sort);
	}

	public QueryObject getQuery() {
		return this.queryRequest.getQuery();
	}

	public void setQuery(QueryObject query) {
		this.queryRequest.setQuery(query);
	}

	public int getUtcMinuteOffset() {
		return this.queryRequest.getUtcMinuteOffset();
	}

	public void setUtcMinuteOffset(int utcMinuteOffset) {
		this.queryRequest.setUtcMinuteOffset(utcMinuteOffset);
	}

}
