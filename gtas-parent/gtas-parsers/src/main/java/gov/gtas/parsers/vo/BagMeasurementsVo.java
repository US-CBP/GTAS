package gov.gtas.parsers.vo;

import gov.gtas.parsers.pnrgov.segment.TBD_BD;


import java.util.List;
import java.util.UUID;

public class BagMeasurementsVo {
    private UUID uuid = UUID.randomUUID();
    private String measurementType;
    private int quantity = 0;
    private double weightInKilos = 0;

    public static BagMeasurementsVo fromTbdBD(List<TBD_BD> baggageDetails) {

        BagMeasurementsVo bagMeasurementsVo = new BagMeasurementsVo();
        if (baggageDetails.isEmpty()) {
            return bagMeasurementsVo;
        }
        // Always use the first baggageDetails provided.
        TBD_BD tbd_bd = baggageDetails.get(0);
        int quantity =  tbd_bd.getQuantityAsInteger();
        double weight = tbd_bd.getWeightInKilos();
        String measurementType = tbd_bd.getMeasureUnitQualifier();
        bagMeasurementsVo.setQuantity(quantity);
        bagMeasurementsVo.setWeight(weight);
        bagMeasurementsVo.setMeasurementType(measurementType);
        return bagMeasurementsVo;
    }

    public String getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(String measurementType) {
        this.measurementType = measurementType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getWeight() {
        return weightInKilos;
    }

    public void setWeight(double weight) {
        this.weightInKilos = weight;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
