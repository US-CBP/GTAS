package gov.gtas.search;

import java.util.HashMap;

import gov.gtas.enumtype.FilterOperationsEnum;

public class FilterOperations extends HashMap<String, String>{
	
	private static final long serialVersionUID = 1L;
	
	private FilterOperationsEnum defaultOperation = FilterOperationsEnum.EQUAL;

	@Override
	public String get(Object key) {
		return containsKey(key) ? super.get(key) : defaultOperation.toString();
	}

}
