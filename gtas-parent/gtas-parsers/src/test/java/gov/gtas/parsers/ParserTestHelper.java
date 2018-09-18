package gov.gtas.parsers;

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

//Class to help unit test parsers.
public class ParserTestHelper {

    public static LocalDate getLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static LocalDateTime getLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static String getMessageText(String messageRelativePath) throws IOException, URISyntaxException {
        ClassPathResource resource = new ClassPathResource(messageRelativePath);
        URL url = resource.getURL();
        java.nio.file.Path resPath = Paths.get(url.toURI());
        return new String(Files.readAllBytes(resPath), StandardCharsets.UTF_8);
    }
}