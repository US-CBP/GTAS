package gov.gtas.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "graph_hit_detail")
public class GraphHitDetail extends BaseEntity {

    @Column(name = "passenger_id", columnDefinition = "bigint unsigned")
    private Long passenger_id;

    @ManyToOne()
    @JoinColumn(name = "passenger_id", updatable = false, insertable = false)
    private Passenger passenger;

    @ManyToOne()
    private GraphRule graphRule;


    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Long getPassenger_id() {
        return passenger_id;
    }

    public void setPassenger_id(Long passenger_id) {
        this.passenger_id = passenger_id;
    }

    public GraphRule getGraphRule() {
        return graphRule;
    }

    public void setGraphRule(GraphRule graphRule) {
        this.graphRule = graphRule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GraphHitDetail)) return false;
        GraphHitDetail that = (GraphHitDetail) o;
        return getPassenger_id().equals(that.getPassenger_id()) &&
                getGraphRule().equals(that.getGraphRule());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPassenger_id(), getGraphRule());
    }
}
