import gov.gtas.job.scheduler.DefaultShareConstraint;
import gov.gtas.job.scheduler.DocumentDeletionResult;
import gov.gtas.job.scheduler.GTASShareConstraint;
import gov.gtas.model.ApisMessage;
import gov.gtas.model.Document;
import gov.gtas.model.Passenger;
import gov.gtas.model.Pnr;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class DocumentDeletionResultTest {

    GTASShareConstraint gtasShareConstraint = new DefaultShareConstraint();
    Set<Document> documentSet = new HashSet<>();
    Date oneHourAgo = getDate(1);
    Date oneDayAgo = getDate(24);

    @Before
    public void before() {

    }

    @Test
    public void smokeTest() {
        DocumentDeletionResult ddr = DocumentDeletionResult.processApisPassengers(new HashSet<>(), new Date(), new Date(), gtasShareConstraint);
    }

    @Test
    public void simpleDeleteAllApis() {
        addApisMessage();
        DocumentDeletionResult ddr = DocumentDeletionResult.processApisPassengers(documentSet, oneHourAgo, oneDayAgo, gtasShareConstraint);
        Assert.assertEquals(1, ddr.getDocuments().size());
        Assert.assertEquals(1, ddr.getDocumentRetentionPolicyAudits().size());
    }

    @Test
    public void noDeleteRelevantMessageApis() {
        addApisMessage();
        DocumentDeletionResult ddr = DocumentDeletionResult.processApisPassengers(documentSet, oneDayAgo, oneHourAgo, gtasShareConstraint);
        Assert.assertEquals(0, ddr.getDocuments().size());
        Assert.assertEquals(1, ddr.getDocumentRetentionPolicyAudits().size());
    }

    @Test
    public void simpleDeleteAllPnr() {
        addPnrMessage();
        DocumentDeletionResult ddr = DocumentDeletionResult.processPnrPassengers(documentSet, oneDayAgo , oneHourAgo, gtasShareConstraint);
        Assert.assertEquals(1, ddr.getDocuments().size());
        Assert.assertEquals(1, ddr.getDocumentRetentionPolicyAudits().size());
    }

    @Test
    public void noDeleteRelevantMessagePnr() {
        addPnrMessage();
        DocumentDeletionResult ddr = DocumentDeletionResult.processPnrPassengers(documentSet, oneHourAgo, oneDayAgo, gtasShareConstraint);
        Assert.assertEquals(0, ddr.getDocuments().size());
        Assert.assertEquals(1, ddr.getDocumentRetentionPolicyAudits().size());
    }

    private void addApisMessage() {
        Passenger p = new Passenger();
        Document d = new Document();
        d.setDocumentType("P");
        d.setDocumentNumber("12341234");
        ApisMessage apisMessage = new ApisMessage();
        apisMessage.setCreateDate(oneDayAgo);
        d.setPassenger(p);
        d.getMessages().add(apisMessage);
        documentSet.add(d);
    }

    private void addPnrMessage() {
        Passenger p = new Passenger();
        Document d = new Document();
        d.setDocumentType("P");
        d.setDocumentNumber("12341234");
        Pnr pnr = new Pnr();
        pnr.setCreateDate(oneDayAgo);
        d.setPassenger(p);
        d.getMessages().add(pnr);
        documentSet.add(d);
    }


    private Date getDate(int hourLimit) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pnrLdtCutOff = now.minusHours(hourLimit);
        return new Date(pnrLdtCutOff.toInstant(ZoneOffset.UTC).toEpochMilli());
    }
}
