package gov.gtas.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.gtas.enumtype.CaseDispositionStatusEnum;

import java.util.ArrayList;
import java.util.List;

public class CaseDispositionStatusCheckbox {

	@JsonProperty("NEW")
	private Boolean newCases;
	@JsonProperty("OPEN")
	private Boolean openCases;
	@JsonProperty("CLOSED")
	private Boolean closedCases;
	@JsonProperty("REOPEN")
	private Boolean reopenedCases;
	@JsonProperty("PENDINGCLOSURE")
	private Boolean pendingClosure;

	public Boolean getNewCases() {
		return newCases;
	}

	public void setNewCases(Boolean newCases) {
		this.newCases = newCases;
	}

	public Boolean getOpenCases() {
		return openCases;
	}

	public void setOpenCases(Boolean openCases) {
		this.openCases = openCases;
	}

	public Boolean getClosedCases() {
		return closedCases;
	}

	public void setClosedCases(Boolean closedCases) {
		this.closedCases = closedCases;
	}

	public Boolean getReopenedCases() {
		return reopenedCases;
	}

	public void setReopenedCases(Boolean reopenedCases) {
		this.reopenedCases = reopenedCases;
	}

	public Boolean getPendingClosure() {
		return pendingClosure;
	}

	public void setPendingClosure(Boolean pendingClosure) {
		this.pendingClosure = pendingClosure;
	}

	public List<String> namesOfCheckedBoxes() {
		List<String> checkedBoxes = new ArrayList<>();
		if (newCases) {
			checkedBoxes.add(CaseDispositionStatusEnum.NEW.getType());
		}
		if (openCases) {
			checkedBoxes.add(CaseDispositionStatusEnum.OPEN.getType());
		}
		if (closedCases) {
			checkedBoxes.add(CaseDispositionStatusEnum.CLOSED.getType());
		}
		if (reopenedCases) {
			checkedBoxes.add(CaseDispositionStatusEnum.REOPEN.getType());
		}
		if (pendingClosure) {
			checkedBoxes.add(CaseDispositionStatusEnum.PENDINGCLOSURE.getType());
		}
		return checkedBoxes;
	}

}
