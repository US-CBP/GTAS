package gov.gtas.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "bag_bd_join")
@IdClass(BookingBagId.class)
public class BookingBag {

	@Id
	@Column(name = "bag_id", nullable = false, columnDefinition = "bigint unsigned")
	private Long bagId;

	@Id
	@Column(name = "bd_id", nullable = false, columnDefinition = "bigint unsigned")
	private Long bdId;

	public BookingBag() {
	}

	public BookingBag(Long bagId, Long bdId) {
		this.bagId = bagId;
		this.bdId = bdId;
	}

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
		if (this == o)
			return true;
		if (!(o instanceof BookingBag))
			return false;
		BookingBag that = (BookingBag) o;
		return getBagId().equals(that.getBagId()) && getBdId().equals(that.getBdId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getBagId(), getBdId());
	}
}
