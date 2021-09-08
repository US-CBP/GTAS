package gov.gtas.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "flight_loader", 
uniqueConstraints = {
		@UniqueConstraint(columnNames = {"id_tag"})
		}
)
public class FlightLoader extends BaseEntityAudit {
	
	@Column(name = "id_tag")
	private String idTag;
	
	@Column(name = "loader_name")
	private String loaderName;

	public String getIdTag() {
		return idTag;
	}

	public void setIdTag(String idTag) {
		this.idTag = idTag;
	}

	public String getLoaderName() {
		return loaderName;
	}

	public void setLoaderName(String loaderName) {
		this.loaderName = loaderName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idTag, loaderName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null) 
			return false;
		if (getClass() != obj.getClass())
			return false;
		FlightLoader other = (FlightLoader) obj;
		return Objects.equals(idTag, other.idTag) && Objects.equals(loaderName, other.loaderName);
	}
}
