package gov.gtas.email;

import gov.gtas.config.TestCommonServicesConfig;
import gov.gtas.email.dto.CategoryDTO;
import gov.gtas.email.dto.DocumentDTO;
import gov.gtas.email.dto.HitEmailDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestCommonServicesConfig.class })
public class EmailTemplateLoaderSampleGenerator {

    @Autowired
    private EmailTemplateLoader emailTemplateLoader;

    @Test
    public void generateTemplate() throws Exception {
        HitEmailDTO hitEmailDTO = generateSampleHitEmailDTO();

        String templateName = "highProfileHitNotification.ftl";
        String htmlContent = emailTemplateLoader.generateHtmlString(templateName, Collections.singleton(hitEmailDTO));
        writeToFile(htmlContent);
    }

    public static void writeToFile(String fileContent) throws IOException {
        File file = new File("../gtas-commons/src/test/resources/generated-html/sample.html");
        FileWriter writer = new FileWriter(file);
        writer.write(fileContent);
        writer.close();
    }

    private HitEmailDTO generateSampleHitEmailDTO() {
        HitEmailDTO hitEmailDTO = new HitEmailDTO();

        hitEmailDTO.setFirstName("John");
        hitEmailDTO.setLastName("Doe");
        hitEmailDTO.setFlightOrigin("BWI");
        hitEmailDTO.setFlightDestination("LHR");
        hitEmailDTO.setCarrier("Spirit");

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setRule("R");
        categoryDTO.setSeverity("Top");
        categoryDTO.setCategoryName("Terrorism");
        categoryDTO.setType("Age Rule");
        categoryDTO.setStatus("NEW");

        CategoryDTO categoryDTO2 = new CategoryDTO();
        categoryDTO2.setRule("R");
        categoryDTO2.setSeverity("High");
        categoryDTO2.setCategoryName("Terrorism");
        categoryDTO2.setType("Age Rule");
        categoryDTO2.setStatus("NEW");

        hitEmailDTO.setCategoryDTOs(Arrays.asList(categoryDTO, categoryDTO2));

        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setDocumentType("P");
        documentDTO.setDocumentNumber("798421317");

        DocumentDTO documentDTO2 = new DocumentDTO();
        documentDTO2.setDocumentType("A");
        documentDTO2.setDocumentNumber("3425425345");
        hitEmailDTO.setDocumentDTOs(Arrays.asList(documentDTO, documentDTO2));

        hitEmailDTO.setDob(java.sql.Date.valueOf(LocalDate.of(2000,1,1)));
        hitEmailDTO.setGender("MALE");
        hitEmailDTO.setFlightNumber("A12345");
        hitEmailDTO.setTimeRemaining("1d:2h:30m");

        return hitEmailDTO;

    }

}
