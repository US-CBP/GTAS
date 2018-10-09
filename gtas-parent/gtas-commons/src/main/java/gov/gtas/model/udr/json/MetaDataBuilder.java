package gov.gtas.model.udr.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import gov.gtas.constant.RuleConstants;

import java.util.Date;

public class MetaDataBuilder {

    private String title;
    private String description;
    private Long ruleCat;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = RuleConstants.UDR_DATE_FORMAT)
    private Date startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = RuleConstants.UDR_DATE_FORMAT)
    private Date endDate;
    private String author;
    private boolean enabled;


    public MetaDataBuilder title(String title) {
        this.title = title;
        return this;
    }

    public MetaDataBuilder description(String description) {
        this.description = description;
        return this;
    }

    public MetaDataBuilder ruleCat(Long ruleCat) {
        this.ruleCat = ruleCat;
        return this;
    }

    public MetaDataBuilder startDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public MetaDataBuilder endDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public MetaDataBuilder author(String author) {
        this.author = author;
        return this;
    }

    public MetaDataBuilder enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public MetaData build() {
        MetaData metaData = new MetaData();
        metaData.setTitle(this.title);
        metaData.setDescription(this.description);
        metaData.setRuleCat(this.ruleCat);
        metaData.setStartDate(this.startDate);
        metaData.setEndDate(this.endDate);
        metaData.setAuthor(this.author);
        metaData.setEnabled(this.enabled);
        return metaData;
    }







}
