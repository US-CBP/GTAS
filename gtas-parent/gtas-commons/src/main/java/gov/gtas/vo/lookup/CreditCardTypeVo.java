package gov.gtas.vo.lookup;

import java.io.Serializable;

public class CreditCardTypeVo implements Serializable  {
	private Long id;
	private Long originId;
	private String code;
	private String description;

	public CreditCardTypeVo(Long id, Long originId, String code, String description) {
		this.id = id;
		this.originId = originId;
		this.code = code;
		this.description = description;
	}

	public CreditCardTypeVo() {
	}

	public Long getId() { return id; }

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOriginId() { return originId; }

	public void setOriginId(Long originId) {
		this.originId = originId;
	}

	public String getCode() { return code; }

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() { return description; }

	public void setDescription(String description) {
		this.description = description;
	}
}
