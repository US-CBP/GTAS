/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.model;

import org.junit.Assert;
import org.junit.Test;

public class DocumentTest {


    @Test
    public void testEquality() {

        Passenger p = new Passenger();
        p.setId(2L);
        Document doc1 = new Document();
        Document doc2 = new Document();
        doc1.setPassenger(p);
        doc1.setPaxId(2L);
        doc1.setDocumentNumber("1234");
        doc1.setIssuanceCountry("foo");
        doc1.setDocumentType("bar");
        doc2.setPassenger(p);
        doc2.setPaxId(2L);
        doc2.setDocumentNumber("1234");
        doc2.setIssuanceCountry("foo");
        doc2.setDocumentType("bar");
        Assert.assertEquals(doc1, doc2);
        Assert.assertEquals(doc1.hashCode(), doc2.hashCode());

    }

    @Test
    public void testInequality() {

        Passenger p = new Passenger();
        p.setId(2L);
        Document doc1 = new Document();
        Document doc2 = new Document();
        doc1.setPassenger(p);
        doc1.setPaxId(2L);
        doc1.setDocumentNumber("1234");
        doc1.setIssuanceCountry("foo");
        doc1.setDocumentType("bar");
        doc2.setPassenger(p);
        doc2.setPaxId(2L);
        doc2.setDocumentNumber("5678");
        doc2.setIssuanceCountry("foo");
        doc2.setDocumentType("bar");
        Assert.assertNotEquals(doc1, doc2);
        Assert.assertNotEquals(doc1.hashCode(), doc2.hashCode());

    }

}
