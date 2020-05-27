import gov.gtas.job.scheduler.DefaultShareConstraint;
import gov.gtas.job.scheduler.GTASShareConstraint;
import gov.gtas.job.scheduler.PassengerDeletionResult;
import gov.gtas.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashSet;

public class PassengerDeletionResultTest {
    GTASShareConstraint gtasShareConstraint = new DefaultShareConstraint();
    Date oneHourAgo = getDate(1);
    Date oneDayAgo = getDate(24);
    Date twoDaysAgo = getDate(48);

    @Test
    public void smokeTest() {
        PassengerDeletionResult pdr = PassengerDeletionResult.processApisPassengers(new HashSet<>(), oneDayAgo, oneDayAgo, gtasShareConstraint);
    }

    @Test
    public void pnrDeleteTest() {
        HashSet<Passenger> passengers = new HashSet<>();
        addPnrMessage(passengers);
        PassengerDeletionResult pdr = PassengerDeletionResult.processPnrPassengers(passengers, oneDayAgo, oneHourAgo, gtasShareConstraint);
        Assert.assertEquals(1, pdr.getPassengerDetails().size());
        Assert.assertEquals(1, pdr.getPassengerDetailRetentionPolicyAudits().size());

    }

    @Test
    public void pnrNoDeleteTest() {
        HashSet<Passenger> passengers = new HashSet<>();
        addPnrMessage(passengers);
        PassengerDeletionResult pdr = PassengerDeletionResult.processPnrPassengers(passengers, oneHourAgo, twoDaysAgo, gtasShareConstraint);
        Assert.assertEquals(0, pdr.getPassengerDetails().size());
        Assert.assertEquals(1, pdr.getPassengerDetailRetentionPolicyAudits().size());

    }

    @Test
    public void apisDeleteTest() {
        HashSet<Passenger> passengers = new HashSet<>();
        addApisMessage(passengers);
        PassengerDeletionResult pdr = PassengerDeletionResult.processApisPassengers(passengers, oneHourAgo, twoDaysAgo, gtasShareConstraint);
        Assert.assertEquals(1, pdr.getPassengerDetails().size());
        Assert.assertEquals(1, pdr.getPassengerDetailRetentionPolicyAudits().size());

    }

    @Test
    public void apisNoDeleteTest() {
        HashSet<Passenger> passengers = new HashSet<>();
        addApisMessage(passengers);
        PassengerDeletionResult pdr = PassengerDeletionResult.processApisPassengers(passengers, twoDaysAgo, oneHourAgo, gtasShareConstraint);
        Assert.assertEquals(0, pdr.getPassengerDetails().size());
        Assert.assertEquals(1, pdr.getPassengerDetailRetentionPolicyAudits().size());

    }

    private Date getDate(int hourLimit) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pnrLdtCutOff = now.minusHours(hourLimit);
        return new Date(pnrLdtCutOff.toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    private void addPnrMessage(HashSet<Passenger> pax) {
        Passenger p = new Passenger();
        PassengerDetails pd = new PassengerDetails();
        p.setPassengerDetails(pd);
        Document d = new Document();
        d.setDocumentType("P");
        d.setDocumentNumber("12341234");
        Pnr pnr = new Pnr();
        pnr.setCreateDate(oneDayAgo);
        d.setPassenger(p);
        d.getMessages().add(pnr);
        p.getPnrs().add(pnr);
        pax.add(p);
    }

    private void addApisMessage(HashSet<Passenger> pax) {
        Passenger p = new Passenger();
        PassengerDetails pd = new PassengerDetails();
        p.setPassengerDetails(pd);
        Document d = new Document();
        d.setDocumentType("P");
        d.setDocumentNumber("12341234");
        ApisMessage apisMessage = new ApisMessage();
        apisMessage.setCreateDate(oneDayAgo);
        d.setPassenger(p);
        d.getMessages().add(apisMessage);
        p.getApisMessage().add(apisMessage);
        pax.add(p);
    }
}
