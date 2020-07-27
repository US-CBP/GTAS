package gov.gtas.search;

import java.util.HashMap;

public class FilterOperations extends HashMap<String, String>{
	
	private static final long serialVersionUID = 1L;
	
	private String defaultOperation = ":";

	@Override
	public String get(Object key) {
		return containsKey(key) ? super.get(key) : defaultOperation;
	}

}
