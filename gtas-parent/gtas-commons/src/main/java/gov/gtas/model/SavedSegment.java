package gov.gtas.model;


import javax.persistence.*;

@Entity
@Table(name = "saved_segment")
public class SavedSegment extends BaseEntityAudit {

	@Column(name="rawSegment")
    private String rawSegment;
	@Column(name="regex")
    private String regex;
	@Column(name="segmentName")
    private String segmentName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pnr_id", referencedColumnName = "id")
    private Pnr pnr;

    public SavedSegment(String segmentName, String segmentText, String regex) {
        this.rawSegment = segmentText;
        this.segmentName = segmentName;
        this.regex = regex;
    }

    public SavedSegment() {

    }

    public String getRawSegment() {
        return rawSegment;
    }

    public void setRawSegment(String rawSegment) {
        this.rawSegment = rawSegment;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getSegmentName() {
        return segmentName;
    }

    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }

    public Pnr getPnr() {
        return pnr;
    }

    public void setPnr(Pnr pnr) {
        this.pnr = pnr;
    }
}
