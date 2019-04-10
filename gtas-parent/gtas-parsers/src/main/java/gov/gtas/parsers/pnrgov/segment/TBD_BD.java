package gov.gtas.parsers.pnrgov.segment;

import com.google.common.collect.Iterables;
import gov.gtas.parsers.pnrgov.enums.MeasurementQualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/*
*
* This corresponds to BaggageDetails Data Composite in TBD (5.24)
* */
public class TBD_BD {
    private TBD tbd;
    private String quantity;
    private String measurementAndValue;
    private String allowanceOrChargeQualifier;
    private String measureUnitQualifier;
    private String processingIndicatorCoded;
    private Logger logger = LoggerFactory.getLogger(TBD_BD.class);
    TBD_BD(TBD tbd, List<String> dataElements) {

        this.tbd = tbd;
        String [] splitIntoComponenets = Iterables.toArray(dataElements, String.class);

        final int QUANT = 0;
        final int MEAS_AND_VAL = 1;
        final int ALLOWANCE_CHANGE = 2;
        final int MEASURE_QAUL = 3;
        final int PROCESS_IND = 4;
        for (int i = 0 ; i < splitIntoComponenets.length; i++) {
            switch (i) {
                case QUANT:
                    quantity = splitIntoComponenets[QUANT];
                    break;
                case MEAS_AND_VAL:
                    measurementAndValue = splitIntoComponenets[MEAS_AND_VAL];
                    break;
                case ALLOWANCE_CHANGE:
                    allowanceOrChargeQualifier = splitIntoComponenets[ALLOWANCE_CHANGE];
                    break;
                case MEASURE_QAUL:
                    measureUnitQualifier = splitIntoComponenets[MEASURE_QAUL];
                    break;
                case PROCESS_IND:
                    processingIndicatorCoded = splitIntoComponenets[PROCESS_IND];
                    break;
                 default:
                     logger.warn("Do not have field " + i + "on segment. Potential bad parse / bad data!");
                     break;
            }
        }
    }

    public String getUnitQualifierAsKgsOrLbs() {
        if ("701".equals(allowanceOrChargeQualifier)) {
            return "Lbs";
        } else {
            return "Kgs";
        }
    }

    public Integer getQuantityAsInteger() {
        Integer quantityOfBags = null;
        if (quantity != null) {
            try {
                quantityOfBags = Integer.parseInt(quantity);
            } catch (Exception e) {
                logger.warn("Failed to parse bag count with string : " + quantity );
            }
        }
        return quantityOfBags;
    }

    public Double getRawWeight() {
        Double weight = null;
        if (measurementAndValue != null) {
            try {
                weight = Double.parseDouble(measurementAndValue);
            } catch (Exception e) {
                logger.warn("Failed to parse double" + weight);
            }
        }
        return weight;
    }

    public Double getWeightInKilos() {
        Double weight = null;
        if (measurementAndValue != null) {
            try {
                weight = Double.parseDouble(measurementAndValue);
                if ("701".equals(allowanceOrChargeQualifier)) {
                    weight = weight * 0.45359237;
                }
            } catch (Exception e) {
                logger.warn("Failed to parse double" + weight);
            }
        }
        return weight;
    }
    public TBD getTbd() {
        return tbd;
    }

    public void setTbd(TBD tbd) {
        this.tbd = tbd;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMeasurementAndValue() {
        return measurementAndValue;
    }

    public void setMeasurementAndValue(String measurementAndValue) {
        this.measurementAndValue = measurementAndValue;
    }

    public String getAllowanceOrChargeQualifier() {
        return allowanceOrChargeQualifier;
    }

    public void setAllowanceOrChargeQualifier(String allowanceOrChargeQualifier) {
        this.allowanceOrChargeQualifier = allowanceOrChargeQualifier;
    }

    public String getMeasureUnitQualifier() {
        return measureUnitQualifier;
    }

    public MeasurementQualifier getMeasurementQualifierEnum() {
        return MeasurementQualifier.fromString(allowanceOrChargeQualifier).orElse(MeasurementQualifier.UNKNOWN);
    }

    public void setMeasureUnitQualifier(String measureUnitQualifier) {
        this.measureUnitQualifier = measureUnitQualifier;
    }

    public String getProcessingIndicatorCoded() {
        return processingIndicatorCoded;
    }

    public void setProcessingIndicatorCoded(String processingIndicatorCoded) {
        this.processingIndicatorCoded = processingIndicatorCoded;
    }

}
