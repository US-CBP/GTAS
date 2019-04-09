package gov.gtas.model;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bag_measurements")
public class BagMeasurements extends BaseEntity {

    @Column(name = "bag_count")
    private int bagCount = 0;
    /*
    * All weight is in Kilos.
    * */
    @Column(name = "bag_weight")
    private double weight = 0;

    @OneToMany(mappedBy = "bagMeasurements")
    private List<Bag> bagList = new ArrayList<>();


    @Transient
    private UUID parserUUID;

    public List<Bag> getBagList() {
        return bagList;
    }

    public void setBagList(List<Bag> bagList) {
        this.bagList = bagList;
    }

    public int getBagCount() {
        return bagCount;
    }

    public void setBagCount(int bagCount) {
        this.bagCount = bagCount;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public UUID getParserUUID() {
        return parserUUID;
    }

    public void setParserUUID(UUID parserUUID) {
        this.parserUUID = parserUUID;
    }
}
