package gov.gtas.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PriorityVettingListStatus implements Serializable {

	@JsonProperty("NEW")
	private Boolean newItems;
	@JsonProperty("DISMISSED")
	private Boolean dismissed;
	@JsonProperty("RE_OPENED")
	private Boolean reOpened;

	PriorityVettingListStatus() {
	}

	public Boolean getNewItems() {
		return newItems;
	}

	public void setNewItems(Boolean newItems) {
		this.newItems = newItems;
	}

	public Boolean getDismissed() {
		return dismissed;
	}

	public void setDismissed(Boolean dismissed) {
		this.dismissed = dismissed;
	}

	public Boolean getReOpened() {
		return reOpened;
	}

	public void setReOpened(Boolean reOpened) {
		this.reOpened = reOpened;
	}

	public List<String> namesOfCheckedBoxes() {
		List<String> checkedBoxes = new ArrayList<>();
		if (newItems) {
			checkedBoxes.add("NEW");
		}
		if (dismissed) {
			checkedBoxes.add("DISMISSED");
		}
		return checkedBoxes;
	}

}
