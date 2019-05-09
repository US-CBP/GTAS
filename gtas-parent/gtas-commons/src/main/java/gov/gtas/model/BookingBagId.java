package gov.gtas.model;

import java.io.Serializable;
import java.util.Objects;

public class BookingBagId implements Serializable {
    private Long bagId;
    private Long bdId;

    public Long getBagId() {
        return bagId;
    }

    public void setBagId(Long bagId) {
        this.bagId = bagId;
    }


    public Long getBdId() {
        return bdId;
    }

    public void setBdId(Long bdId) {
        this.bdId = bdId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingBagId)) return false;
        BookingBagId that = (BookingBagId) o;
        return getBagId().equals(that.getBagId()) &&
                getBdId().equals(that.getBdId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBagId(), getBdId());
    }
}
