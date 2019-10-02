package gov.gtas.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bag_measurements")
public class BagMeasurements extends BaseEntity {

	@Column(name = "bag_count")
	private Integer bagCount;

	@Column(name = "bag_weight_in_kilos")
	private Double weight;

	@Column(name = "initial_measurement_in")
	private String measurementIn;

	@OneToMany(mappedBy = "bagMeasurements")
	private List<Bag> bagList = new ArrayList<>();

	@Column(name = "raw_weight_from_message")
	private Double rawWeight;

	@Transient
	private UUID parserUUID;

	public Integer getBagCount() {
		return bagCount;
	}

	public Double getWeight() {
		return weight;
	}

	public Double getRawWeight() {
		return rawWeight;
	}

	public void setRawWeight(Double rawWeight) {
		this.rawWeight = rawWeight;
	}

	public void setBagCount(Integer bagCount) {
		this.bagCount = bagCount;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public String getMeasurementIn() {
		return measurementIn;
	}

	public void setMeasurementIn(String measurementIn) {
		this.measurementIn = measurementIn;
	}

	public List<Bag> getBagList() {
		return bagList;
	}

	public void setBagList(List<Bag> bagList) {
		this.bagList = bagList;
	}

	public UUID getParserUUID() {
		return parserUUID;
	}

	public void setParserUUID(UUID parserUUID) {
		this.parserUUID = parserUUID;
	}
}
