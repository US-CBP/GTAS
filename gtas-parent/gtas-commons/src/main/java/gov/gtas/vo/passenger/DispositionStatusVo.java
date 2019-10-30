package gov.gtas.vo.passenger;

public class DispositionStatusVo {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private String description;

	public DispositionStatusVo(Long id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
