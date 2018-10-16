package gov.gtas.svc;

import gov.gtas.model.udr.json.QueryTerm;

import java.util.List;

public class RuleDescription {
    private List<QueryTerm> queryTerms;


    public RuleDescription() {}

    public List<QueryTerm> getQueryTerms() {
        return queryTerms;
    }

    public void setQueryTerms(List<QueryTerm> queryTerms) {
        this.queryTerms = queryTerms;
    }
}
