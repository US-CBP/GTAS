package gov.gtas.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VersionServiceImpl implements VersionService{

	@Value("${application.version}")
	private String applicationVersionNumber;
	  
	public String getApplicationVersionNumber() {
		
		return applicationVersionNumber;
	}

	
	
	
	

}
