package gov.gtas.parsers.pnrgov.segment;

import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TBD_BagTagDetails {
    private TBD tbd;

    private String companyId;
    private String bagIdNumber;
    private String totalNumItems;
    private String threeLetterCityCode;
    private String compIdNumber;
    private String dataInd;
    private String codedIdtemCharacteristic;
    private String ssrType;
    private String measurementValue;
    private String measureUnitQualifier;
    private String freeText;

    /*
    * Input is expected to be a **single** component within the BagTag Details data element.
    * */
    TBD_BagTagDetails(TBD tbd, List<String> composites) {
        Logger logger = LoggerFactory.getLogger(TBD_BagTagDetails.class);

        this.tbd = tbd;
        String [] splitIntoComponenets = Iterables.toArray(composites, String.class);

        final int COMPANY_IDENTIFICATION = 0;
        final int ITEM_NUMBER = 1;
        final int NUMBER_OF_ITEMS = 2;
        final int LOCATION_IDENTIFIER = 3;
        final int COMP_ID_NUMBER = 4;
        final int DATA_IND = 5;
        final int CODED_ITEM_CHAR =6;
        final int SSR_TYPE = 7;
        final int MEASUREMENT_VAL = 8;
        final int MEASURE_UNIT_QUAL = 9;
        final int FREE_TEXT = 10;


        for (int i = 0 ; i < splitIntoComponenets.length; i++) {
            switch (i) {
                case COMPANY_IDENTIFICATION:
                    companyId = splitIntoComponenets[COMPANY_IDENTIFICATION];
                    break;
                case ITEM_NUMBER:
                    bagIdNumber = splitIntoComponenets[ITEM_NUMBER];
                    break;
                case NUMBER_OF_ITEMS:
                    totalNumItems = splitIntoComponenets[NUMBER_OF_ITEMS];
                    break;
                case LOCATION_IDENTIFIER:
                    threeLetterCityCode = splitIntoComponenets[LOCATION_IDENTIFIER];
                    break;
                case COMP_ID_NUMBER:
                    compIdNumber = splitIntoComponenets[COMP_ID_NUMBER];
                    break;
                case DATA_IND:
                    dataInd = splitIntoComponenets[DATA_IND];
                    break;
                case CODED_ITEM_CHAR:
                    codedIdtemCharacteristic =splitIntoComponenets[CODED_ITEM_CHAR];
                    break;
                case SSR_TYPE:
                    ssrType = splitIntoComponenets[SSR_TYPE];
                    break;
                case MEASUREMENT_VAL:
                    measurementValue = splitIntoComponenets[MEASUREMENT_VAL];
                    break;
                case MEASURE_UNIT_QUAL:
                    measureUnitQualifier = splitIntoComponenets[MEASURE_UNIT_QUAL];
                    break;
                case FREE_TEXT:
                    freeText = splitIntoComponenets[FREE_TEXT];
                    break;
                default:
                    logger.warn("NOT IMPLEMENTED FIELD. CHECK DATA INTEGREITY for ID " + i);
                    break;
            }
        }
    }

    public String getTotalNumItems() {
        return totalNumItems;
    }

    public void setTotalNumItems(String totalNumItems) {
        this.totalNumItems = totalNumItems;
    }

    public String getBagIdNumber() {
        return bagIdNumber;
    }

    public void setBagIdNumber(String bagIdNumber) {
        this.bagIdNumber = bagIdNumber;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getThreeLetterCityCode() {
        return threeLetterCityCode;
    }

    public void setThreeLetterCityCode(String threeLetterCityCode) {
        this.threeLetterCityCode = threeLetterCityCode;
    }

    public String getCompIdNumber() {
        return compIdNumber;
    }

    public void setCompIdNumber(String compIdNumber) {
        this.compIdNumber = compIdNumber;
    }

    public String getDataInd() {
        return dataInd;
    }

    public void setDataInd(String dataInd) {
        this.dataInd = dataInd;
    }

    public String getCodedIdtemCharacteristic() {
        return codedIdtemCharacteristic;
    }

    public void setCodedIdtemCharacteristic(String codedIdtemCharacteristic) {
        this.codedIdtemCharacteristic = codedIdtemCharacteristic;
    }

    public String getSsrType() {
        return ssrType;
    }

    public void setSsrType(String ssrType) {
        this.ssrType = ssrType;
    }

    public String getMeasurementValue() {
        return measurementValue;
    }

    public void setMeasurementValue(String measurementValue) {
        this.measurementValue = measurementValue;
    }

    public String getMeasureUnitQualifier() {
        return measureUnitQualifier;
    }

    public void setMeasureUnitQualifier(String measureUnitQualifier) {
        this.measureUnitQualifier = measureUnitQualifier;
    }

    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    public TBD getTbd() {
        return tbd;
    }
}
