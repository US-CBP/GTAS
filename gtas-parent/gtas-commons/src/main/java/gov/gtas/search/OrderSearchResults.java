package gov.gtas.search;
import java.util.HashMap;

import gov.gtas.enumtype.OrderTypeEnum;

public class OrderSearchResults extends HashMap<String, OrderTypeEnum>{
	private static final long serialVersionUID = 1L;
	
	private OrderTypeEnum defaultOrderType = OrderTypeEnum.DESCENDING;
	
	@Override
	public OrderTypeEnum get(Object key) {
		return containsKey(key) ? super.get(key) : defaultOrderType;
	}
	
	
	
}
