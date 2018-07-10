/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.BookingDetail;

import java.util.List;

public interface BookingDetailService {

    public List<BookingDetail> getBookingDetailsByProcessedFlag();

    public List<BookingDetail> getBookingDetailsByPassengers(Long pax_id) throws Exception;
}
