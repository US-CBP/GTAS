package gov.gtas.services.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class CaseRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;

    // e.g. 2015-10-02T18:33:03.412Z
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private int pageNumber;

    private int pageSize;

    private Long flightId;

    private Long paxId;

    private Long hitId;

    private String caseComments;

    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private Date etaStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private Date etaEnd;

    private transient List<SortOptionsDto> sort;

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Long getHitId() {
        return hitId;
    }

    public void setHitId(Long hitId) {
        this.hitId = hitId;
    }

    public String getCaseComments() {
        return caseComments;
    }

    public void setCaseComments(String caseComments) {
        this.caseComments = caseComments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getPaxId() {
        return paxId;
    }

    public void setPaxId(Long paxId) {
        this.paxId = paxId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Date getEtaStart() {
        return etaStart;
    }

    public void setEtaStart(Date etaStart) {
        this.etaStart = etaStart;
    }

    public Date getEtaEnd() {
        return etaEnd;
    }

    public void setEtaEnd(Date etaEnd) {
        this.etaEnd = etaEnd;
    }

    public List<SortOptionsDto> getSort() {
        return sort;
    }

    public void setSort(List<SortOptionsDto> sort) {
        this.sort = sort;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.MULTI_LINE_STYLE);
    }
}
