package gov.gtas.services.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class  CaseRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;

    // e.g. 2015-10-02T18:33:03.412Z
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private int pageNumber;

    private int pageSize;

    private Long flightId;

    private String flightNumber;
    
    private Long caseId;

    private CaseDispositionStatusCheckbox displayStatusCheckBoxes;

    private Long paxId;

    private Long hitId;

    private String caseComments;

    private String status;

    private String validHit;

    private File file;

    private String paxName;

    private String lastName;

    private Long ruleCatId;
    
    private Boolean oneDayLookoutFlag;

    private MultipartFile multipartFile;
    
    private String caseDisposition;
    
    private String userLocation;

    @JsonProperty("withTimeLeft")
    private Boolean withTimeLeft;

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

    public String getValidHit() {
        return validHit;
    }

    public void setValidHit(String validHit) {
        this.validHit = validHit;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public Long getRuleCatId() {
        return ruleCatId;
    }

    public void setRuleCatId(Long ruleCatId) {
        this.ruleCatId = ruleCatId;
    }

    public String getPaxName() {
        return paxName;
    }

    public void setPaxName(String paxName) {
        this.paxName = paxName;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getOneDayLookoutFlag() {
		return oneDayLookoutFlag;
	}

	public void setOneDayLookoutFlag(Boolean oneDayLookoutFlag) {
		this.oneDayLookoutFlag = oneDayLookoutFlag;
	}

    public String getCaseDisposition() {
		return caseDisposition;
	}

	public void setCaseDisposition(String caseDisposition) {
		this.caseDisposition = caseDisposition;
	}

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.MULTI_LINE_STYLE);
    }

    public CaseDispositionStatusCheckbox getDisplayStatusCheckBoxes() {
        return displayStatusCheckBoxes;
    }

    public void setDisplayStatusCheckBoxes(CaseDispositionStatusCheckbox displayStatusCheckBoxes) {
        this.displayStatusCheckBoxes = displayStatusCheckBoxes;
    }

	public Long getCaseId() {
		return caseId;
	}

	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}


	public String getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(String userLocation) {
		this.userLocation = userLocation;
	}


    public Boolean getWithTimeLeft() {
        return withTimeLeft;
    }

    public void setWithTimeLeft(Boolean withTimeLeft) {
        this.withTimeLeft = withTimeLeft;
    }
}
