package gov.gtas.summary;

import java.util.ArrayList;
import java.util.List;

public class SummaryMetaData {
    public String summary;
    public List<String> countryList = new ArrayList<>();
    public String countryGroupName;

    public SummaryMetaData(){}

    public List<String> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<String> countryList) {
        this.countryList = countryList;
    }

    public String getCountryGroupName() {
        return countryGroupName;
    }

    public void setCountryGroupName(String countryGroupName) {
        this.countryGroupName = countryGroupName;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
