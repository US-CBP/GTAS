/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */


package gov.gtas.vo;

import gov.gtas.model.Bag;
import gov.gtas.model.BagMeasurements;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.vo.passenger.BagVo;
import org.junit.Test;
import org.springframework.util.Assert;

public class BagVoTest {

    @Test
    public void testBagWithNullMeasurementValues() {
        Bag bag = new Bag();
        Passenger pax = new Passenger();
        pax.setId(-1L);
        Flight flight = new Flight();
        flight.setId(-1L);
        bag.setPassenger(pax);
        bag.setFlight(flight);
        BagMeasurements bagMeasurements = new BagMeasurements();
        bag.setBagMeasurements(bagMeasurements);
        BagVo bagVo = BagVo.fromBag(bag);
        Assert.notNull(bagVo, "no exception thrown");
    }
}
