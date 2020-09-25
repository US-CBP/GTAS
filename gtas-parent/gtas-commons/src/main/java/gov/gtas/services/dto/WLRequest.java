package gov.gtas.services.dto;

import java.util.List;

public class WLRequest<T> {
	private Long id;
	private String action;
	private List<T> wlItems;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<T> getWlItems() {
		return wlItems;
	}

	public void setWlItems(List<T> wlItems) {
		this.wlItems = wlItems;
	}
	
	
	

}
