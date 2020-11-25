package gov.gtas.search;

import gov.gtas.enumtype.OrderTypeEnum;

public class SearchCriteria {
	private String key;
	private String operation;
	private Object value;
	private boolean orderBy;
	private OrderTypeEnum orderType;

	public SearchCriteria(String key, String operation, Object value) {
		super();
		this.key = key;
		this.operation = operation;
		this.value = value;
	}
	

	public boolean isOrderBy() {
		return orderBy;
	}

	public void setOrderBy(boolean orderBy) {
		this.orderBy = orderBy;
	}

	public OrderTypeEnum getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderTypeEnum orderType) {
		this.orderType = orderType;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
