/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RuleCatFilterCheckbox {

	@JsonProperty("name")
	private String name;

	@JsonProperty("value")
	private Boolean value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}
}
