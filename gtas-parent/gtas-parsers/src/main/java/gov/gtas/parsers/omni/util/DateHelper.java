/*
 * All GTAS code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni.util;

public class DateHelper {
    public static String convertIntegerToAgeBin(Integer inputAge) {
        // NOTE: The ageBin range is in the form: "lowerBound - upperBound" with the
        // lowerBound inclusive and the upperBound excluded
        String ageBin = "";

        int age = inputAge.intValue();

        if (age < 20) {
            ageBin = "0-20";
        } else if (age >= 20 && age < 25) {
            ageBin = "20-25";
        } else if (age >= 25 && age < 30) {
            ageBin = "25-30";
        } else if (age >= 30 && age < 35) {
            ageBin = "30-35";
        } else if (age >= 35 && age < 40) {
            ageBin = "35-40";
        } else if (age >= 40 && age < 45) {
            ageBin = "40-45";
        } else if (age >= 45 && age < 50) {
            ageBin = "45-50";
        } else if (age >= 50 && age < 55) {
            ageBin = "50-55";
        } else if (age >= 55 && age < 60) {
            ageBin = "55-60";
        } else {
            ageBin = "60-100";
        }

        return ageBin;
    }
}
