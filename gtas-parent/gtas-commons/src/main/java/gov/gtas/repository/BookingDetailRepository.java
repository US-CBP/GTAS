/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.BookingDetail;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookingDetailRepository extends CrudRepository<BookingDetail, Long>{

    @Query("SELECT bd FROM BookingDetail bd WHERE bd.processed = FALSE")
    public List<BookingDetail> getBookingDetailByProcessedFlag();
}
