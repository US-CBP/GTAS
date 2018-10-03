/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.passenger;

public class BagVo {
    private String bagId;
    private String data_source;
    private String destination;
    private double average_bag_weight;
    private double bag_weight;
    private int bag_count = 0;
    private String passFirstName;
    private String passLastName;

    public BagVo() {
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getBagId() {
        return bagId;
    }

    public void setBagId(String bagId) {
        this.bagId = bagId;
    }

    public String getData_source() {
        return data_source;
    }

    public void setData_source(String data_source) {
        this.data_source = data_source;
    }

    public void setAverage_bag_weight(double average_bag_weight) {
        this.average_bag_weight = average_bag_weight;
    }

    public void setBag_weight(double bag_weight) {
        this.bag_weight = bag_weight;
    }

    public void setBag_count(int bag_count) {
        this.bag_count = bag_count;
    }

    public void setPassFirstName(String passFirstName) {
        this.passFirstName = passFirstName;
    }

    public int getBag_count() {
        return bag_count;
    }

    public String getPassFirstName() {
        return passFirstName;
    }

    public String getPassLastName() {
        return passLastName;
    }

    public void setPassLastName(String passLastName) {
        this.passLastName = passLastName;
    }

    public double getBag_weight() {
        return bag_weight;
    }

    public double getAverage_bag_weight() {
        return average_bag_weight;
    }
}
