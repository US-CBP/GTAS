package gov.gtas.model;

import javax.persistence.*;
import java.util.*;

@SuppressWarnings("unused") // This is for getters and setters
@Entity
@Table(name = "graph_rules")
public class GraphRule extends BaseEntityAudit {

    @Column(name = "description")
    private String description;

    @Column(name = "title")
    private String title;

    @Column(name = "cipherQuery", length = 10000)
    private String cipherQuery;

    @OneToMany(mappedBy = "graphRule")
    private Set<GraphRuleParameter> graphParameter = new HashSet<>();

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

    @SuppressWarnings("unchecked") // unchecked cast  - Object to List<String>
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
