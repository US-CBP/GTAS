package gov.gtas.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("unused") // Unused for getter/setter
@Entity
@Table(name = "graph_rule_parameters")
public class GraphRuleParameter extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "graphRule_id")
	private GraphRule graphRule;

	@Column
	private String ruleParameter;

	@Column
	private String keyValue; // value in string form of neo parameter

	@Column
	private String parameterType;

	public String getParameterType() {
		return parameterType;
	}

	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}

	public GraphRule getGraphRule() {
		return graphRule;
	}

	public void setGraphRule(GraphRule graphRule) {
		this.graphRule = graphRule;
	}

	String getRuleParameter() {
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
