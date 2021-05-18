package gov.gtas.parserconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "loader", name = "enabled")
public class ParserConfig {

    private Boolean enabled;

    private String defaultDocType;

    @Autowired
    public ParserConfig(@Value("${looseparse.enabled}") Boolean enabled, @Value("${looseparse.defaultDocType}") String defaultDocType) {
        this.enabled = enabled;
        this.defaultDocType = defaultDocType;
    }

    public String getDefaultDocType() {
        return defaultDocType;
    }

    public void setDefaultDocType(String defaultDocType) {
        this.defaultDocType = defaultDocType;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
