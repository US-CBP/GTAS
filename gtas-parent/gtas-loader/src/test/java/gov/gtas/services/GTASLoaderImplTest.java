/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;


import gov.gtas.model.Message;
import gov.gtas.repository.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GTASLoaderImplTest {

    @Mock
    PassengerRepository passengerDao;

    @Mock
    ReportingPartyRepository rpDao;

    @Mock
    LoaderServices loaderServices;

    @Mock
    FlightRepository flightDao;

    @Mock
    DocumentRepository docDao;

    @Mock
    PhoneRepository phoneDao;

    @Mock
    PaymentFormRepository paymentFormDao;

    @Mock
    CreditCardRepository creditDao;

    @Mock
    FlightPassengerCountRepository flightPassengerCountRepository;

    @Mock
    AddressRepository addressDao;

    @Mock
    AgencyRepository agencyDao;

    @Mock
    MessageRepository<Message> messageDao;

    @Mock
    PassengerIDTagRepository passengerIdTagDao;

    @Mock
    FlightPassengerRepository flightPassengerRepository;

    @Mock
    FrequentFlyerRepository ffdao;

    @Mock
    LoaderUtils utils;

    @Mock
    BookingDetailRepository bookingDetailDao;

    @Mock
    MutableFlightDetailsRepository mutableFlightDetailsRepository;

    @Mock
    BagMeasurementsRepository bagMeasurementsRepository;

    @InjectMocks
    GtasLoaderImpl gtasLoader;


    @Before
    public void before() {
    }

    @Test
    public void uniqueMessage() throws LoaderException {
        gtasLoader.checkHashCode("pew pew");
    }

    @Test(expected = DuplicateHashCodeException.class)
    public void duplicateMessageException() throws LoaderException {
        String foo = "foo";
        Mockito.when(messageDao.findByHashCode(foo)).thenReturn(new Message());
        gtasLoader.checkHashCode(foo);
    }


}
