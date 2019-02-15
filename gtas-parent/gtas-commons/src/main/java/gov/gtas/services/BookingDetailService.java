/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.BookingDetail;

import java.util.List;

public interface BookingDetailService {

    List<BookingDetail> getBookingDetailsByProcessedFlag();

    List<BookingDetail> getBookingDetailsByPassengers(Long pax_id) throws Exception;

    BookingDetail saveAndGetBookingDetail(BookingDetail bookingDetail);

    List<BookingDetail> deDuplicateBookingDetails(List<BookingDetail> listContainingDuplicates);

    BookingDetail mergeBookingDetails(BookingDetail newBD, BookingDetail oldBD);

}
