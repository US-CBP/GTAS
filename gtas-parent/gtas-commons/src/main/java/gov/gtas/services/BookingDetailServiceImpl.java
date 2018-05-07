/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.BookingDetail;
import gov.gtas.repository.BookingDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookingDetailServiceImpl implements BookingDetailService{

    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    @Override
    public List<BookingDetail> getBookingDetailsByProcessedFlag() {
        return null;
    }

    @Override
    public List<BookingDetail> getBookingDetailsByPassengers(Long pax_id) throws Exception {
        List<BookingDetail> _bd = new ArrayList<>();
        _bd = bookingDetailRepository.getBookingDetailsByPassengers(pax_id);
        return _bd;
    }
}
