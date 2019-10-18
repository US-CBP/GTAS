package gov.gtas.vo.lookup;

public class CountryVo {

	private Long id;
	private Long originId;
	private String iso2;
	private String iso3;
	private String name;
	private String isoNumeric;

	public CountryVo(Long id, Long originId, String iso2, String iso3, String name, String isoNumeric) {
		this.id = id;
		this.originId = originId;
		this.iso2 = iso2;
		this.iso3 = iso3;
		this.name = name;
		this.isoNumeric = isoNumeric;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOriginId() {
		return originId;
	}

	public void setOriginId(Long data) {
		this.originId = data;
	}

	public String getIso2() {
		return iso2;
	}

	public void setIso2(String data) {
		this.iso2 = data;
	}

	public String getIso3() {
		return iso3;
	}

	public void setIso3(String data) {
		this.iso3 = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String data) {
		this.name = data;
	}

	public String getIsoNumeric() {
		return isoNumeric;
	}

	public void setIsoNumeric(String data) {
		this.isoNumeric = data;
	}

}
