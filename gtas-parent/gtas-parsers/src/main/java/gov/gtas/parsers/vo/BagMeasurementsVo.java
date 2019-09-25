package gov.gtas.parsers.vo;

import gov.gtas.parsers.pnrgov.enums.MeasurementQualifier;
import gov.gtas.parsers.pnrgov.segment.TBD_BD;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class BagMeasurementsVo {
	private Logger logger = LoggerFactory.getLogger(BagMeasurementsVo.class);
	private UUID uuid = UUID.randomUUID();
	private String measurementType;
	private Integer quantity;
	private Double weightInKilos;
	private Double weight;

	public static BagMeasurementsVo fromTbdBD(List<TBD_BD> baggageDetails) {

		BagMeasurementsVo bagMeasurementsVo = new BagMeasurementsVo();
		if (baggageDetails.isEmpty()) {
			return bagMeasurementsVo;
		}
		// Always use the first baggageDetails provided.
		TBD_BD tbd_bd = baggageDetails.get(0);
		Integer quantity = tbd_bd.getQuantityAsInteger();
		Double weightInKilos = tbd_bd.getWeightInKilos();
		Double rawWeight = tbd_bd.getRawWeight();
		bagMeasurementsVo.setQuantity(quantity);
		bagMeasurementsVo.setRawWeight(rawWeight);
		bagMeasurementsVo.setWeightInKilos(weightInKilos);
		MeasurementQualifier measurementType = tbd_bd.getMeasurementQualifierEnum();
		bagMeasurementsVo.setMeasurementType(measurementType.getEnglishName());
		return bagMeasurementsVo;
	}

	public Double getWeightInKilos() {
		return weightInKilos;
	}

	public void setWeightInKilos(Double weightInKilos) {
		this.weightInKilos = weightInKilos;
	}

	public String getMeasurementType() {
		return measurementType;
	}

	public void setMeasurementType(String measurementType) {
		this.measurementType = measurementType;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getRawWeight() {
		return weight;
	}

	public void setRawWeight(Double weight) {
		this.weight = weight;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public Integer getBagCountFromString(String numBagsAsString) {
		if (StringUtils.isBlank(numBagsAsString)) {
			return null;
		}
		Integer bagCount = null;
		try {
			bagCount = Integer.parseInt(numBagsAsString);
		} catch (Exception e) {
			logger.warn("failed to parse bag from string " + numBagsAsString);
		}
		return bagCount;
	}

	public Double getBagWeightFromString(String bagWeight) {
		if (StringUtils.isBlank(bagWeight)) {
			return null;
		}
		Double weight = null;
		try {
			weight = Double.parseDouble(bagWeight);
		} catch (Exception e) {
			logger.warn("failed to parse bag weight from string " + bagWeight);
		}
		return weight;
	}
}
