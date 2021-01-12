package gov.gtas.parsers.vo;

import java.util.Objects;

public class SavedSegmentVo {

    private String segmentName;
    private String segmentText;
    private String regex;

    public SavedSegmentVo(String segmentName, String segmentText, String pattern) {
        this.segmentName = segmentName;
        this.segmentText = segmentText;
        this.regex = pattern;
    }

    public String getSegmentName() {
        return segmentName;
    }

    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }

    public String getSegmentText() {
        return segmentText;
    }

    public void setSegmentText(String segmentText) {
        this.segmentText = segmentText;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SavedSegmentVo that = (SavedSegmentVo) o;
        return Objects.equals(segmentName, that.segmentName) &&
                Objects.equals(segmentText, that.segmentText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(segmentName, segmentText);
    }
}
