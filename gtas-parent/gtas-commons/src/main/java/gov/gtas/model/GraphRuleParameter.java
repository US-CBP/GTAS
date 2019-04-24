package gov.gtas.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("unused") //Unused for getter/setter
@Entity
@Table(name = "graph_rule_parameters")
public class GraphRuleParameter extends BaseEntity {

    @ManyToOne
    private GraphRule graphRule;

    @Column
    private String ruleParameter;

    @Column
    private String keyValue;


    public GraphRule getGraphRule() {
        return graphRule;
    }

    public void setGraphRule(GraphRule graphRule) {
        this.graphRule = graphRule;
    }

    public String getRuleParameter() {
        return ruleParameter;
    }

    public void setRuleParameter(String ruleParameter) {
        this.ruleParameter = ruleParameter;
    }


    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

}
