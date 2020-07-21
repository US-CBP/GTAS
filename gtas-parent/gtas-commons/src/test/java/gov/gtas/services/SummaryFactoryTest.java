package gov.gtas.services;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SummaryFactoryTest {

    @Test
    public void testEventIdentifier() {
        List<String> identifierList =  new ArrayList<>();
        identifierList.add("PNH");
        identifierList.add("MNL");
        identifierList.add("KME");
        identifierList.add("0741");
        identifierList.add("1591228800000");
        identifierList.add("1591314000000");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(identifierList.get(i));
        }
        Assert.assertEquals("PNHMNLKME07411591228800000", sb.toString());
    }
}
