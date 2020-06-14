package gov.gtas.services.dto;

import gov.gtas.model.HitDetail;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MappedGroups {
    Map<String, Set<HitDetail>> countryMap = new HashMap<>();
    Map<String, Set<HitDetail>> intraOrgMap = new HashMap<>();

    public Map<String, Set<HitDetail>> getCountryMap() {
        return countryMap;
    }

    public void setCountryMap(Map<String, Set<HitDetail>> countryMap) {
        this.countryMap = countryMap;
    }

    public Map<String, Set<HitDetail>> getIntraOrgMap() {
        return intraOrgMap;
    }

    public void setIntraOrgMap(Map<String, Set<HitDetail>> intraOrgMap) {
        this.intraOrgMap = intraOrgMap;
    }
}
