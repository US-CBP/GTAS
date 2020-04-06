package gov.gtas.common;

import gov.gtas.model.Bag;
import gov.gtas.model.BagMeasurements;
import gov.gtas.model.Passenger;

import java.util.HashSet;
import java.util.Set;

public class BagStatisticCalculator {
    private int bagCount;
    private double bagWeight;
    private Set<Bag> bagsSet;

    public BagStatisticCalculator(Passenger passenger) {
        this.bagsSet = passenger.getBags();
        this.bagCount = 0;
        this.bagWeight = 0;
    }

    public BagStatisticCalculator(Set<Bag> bagsSet) {
        this.bagsSet = bagsSet;
        this.bagCount = 0;
        this.bagWeight = 0;
    }

    public int getBagCount() {
        return bagCount;
    }

    public double getBagWeight() {
        return bagWeight;
    }

    public BagStatisticCalculator invoke(String bagType) {
        Set<BagMeasurements> bagMeasurementsSet = new HashSet<>();
        for (Bag paxBag : bagsSet) {
            if (bagType.equalsIgnoreCase(paxBag.getData_source()))
                if (paxBag.getBagMeasurements() != null) {
                    BagMeasurements bagMeasurements = paxBag.getBagMeasurements();
                    if (bagMeasurementsSet.add(bagMeasurements)) {
                        if (bagMeasurements.getBagCount() != null) {
                            bagCount += bagMeasurements.getBagCount();
                        }
                        if (bagMeasurements.getWeight() != null) {
                            bagWeight += bagMeasurements.getWeight();
                        }
                    }
                }
        }
        return this;
    }
}
