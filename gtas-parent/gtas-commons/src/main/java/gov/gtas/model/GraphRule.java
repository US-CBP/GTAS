package gov.gtas.model;

import gov.gtas.enumtype.HitTypeEnum;

import javax.persistence.*;
import java.util.*;

@SuppressWarnings("unused") // This is for getters and setters
@Entity
@Table(name = "graph_rules")
public class GraphRule extends HitMaker {

	public GraphRule() {
		setHitTypeEnum(HitTypeEnum.GRAPH_HIT);
	}

	@Column(name = "description")
	private String description;

	@Column(name = "title")
	private String title;

	@Column(name = "cipherQuery", length = 10000)
	private String cipherQuery;

	@Column(name = "displayCondition")
	public String getDisplayCondition() {
		return displayCondition;
	}

	public void setDisplayCondition(String displayCondition) {
		this.displayCondition = displayCondition;
	}

	@Column(name = "displayCondition")
	private String displayCondition;


	@Column(name = "enabled")
	private boolean enabled = true;

	@OneToMany(mappedBy = "graphRule")
	private Set<GraphRuleParameter> graphParameter = new HashSet<>();

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getCipherQuery() {
		return cipherQuery;
	}

	public void setCipherQuery(String cipherQuery) {
		this.cipherQuery = cipherQuery;
	}

	private Set<GraphRuleParameter> getGraphParameter() {
		return graphParameter;
	}

	public void setGraphParameter(Set<GraphRuleParameter> graphParameter) {
		this.graphParameter = graphParameter;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@SuppressWarnings("unchecked") // unchecked cast - Object to List<String>
	public Map<String, Object> getParameterMap(Set<String> paxIds) {
		Map<String, Object> queryParameters = new HashMap<>(this.getGraphParameter().size() / 2);
		List<String> paxList = new ArrayList<>(paxIds);
		queryParameters.put("id_tag", paxList);
		for (GraphRuleParameter graphRuleParameter : this.getGraphParameter()) {
			String key = graphRuleParameter.getKeyValue();
			if (queryParameters.containsKey(key)) {
				List<String> valueList = (List<String>) queryParameters.get(key);
				valueList.add(graphRuleParameter.getRuleParameter());
			} else {
				List<String> value = new ArrayList<>();
				value.add(graphRuleParameter.getRuleParameter());
				queryParameters.put(key, value);
			}
		}
		return queryParameters;
	}
}
