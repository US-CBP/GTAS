package gov.gtas.services;

public class WeightCountDto {
    private Double weight;
    private Integer count;
    public Double getWeight() {
        return weight;
    }

    void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getCount() {
        return count;
    }

    void setCount(Integer count) {
        this.count = count;
    }
    Double average() {
        Double average = 0D;
        if (weight == null || count == null) return average;
        if(weight > 0 && count >0){
            Long averageLong = Math.round(weight/count);
            average = averageLong.doubleValue();
        }
        return average;
    }


}
