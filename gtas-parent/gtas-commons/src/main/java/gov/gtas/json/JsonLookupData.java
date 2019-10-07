/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.json;

public class JsonLookupData {

	private Long id;
	private String label;
	private String description;

	public JsonLookupData(Long id, String label, String description) {
		super();
		this.id = id;
		this.label = label;
		this.description = description;
	}

	@SuppressWarnings("unused") // Used to marshal object.
	public JsonLookupData() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
