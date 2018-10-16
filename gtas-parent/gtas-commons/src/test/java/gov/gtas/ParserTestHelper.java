package gov.gtas;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

//Interface to help unit test parsers.
public interface ParserTestHelper {

    default LocalDate getLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    default Date getDateFromLocalDate(LocalDate localDate) {
        return java.sql.Date.valueOf(localDate);
    }

    default LocalDateTime getLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    default String getMessageText(String messageRelativePath) throws IOException, URISyntaxException {
        ClassPathResource resource = new ClassPathResource(messageRelativePath);
        URL url = resource.getURL();
        java.nio.file.Path resPath = Paths.get(url.toURI());
        return new String(Files.readAllBytes(resPath), StandardCharsets.UTF_8);
    }
}