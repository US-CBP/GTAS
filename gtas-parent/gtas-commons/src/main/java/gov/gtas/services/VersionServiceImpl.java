package gov.gtas.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "webapp.services", name = "enabled")
public class VersionServiceImpl implements VersionService {

	@Value("${application.version:2.0}")
	private String applicationVersionNumber;

	public String getApplicationVersionNumber() {

		return applicationVersionNumber;
	}

}
