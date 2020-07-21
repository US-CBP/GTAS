
package gov.gtas.services;

import gov.gtas.model.lookup.AppConfiguration;
import java.util.Date;

public interface AppConfigurationService {

	public AppConfiguration findByOption(String option);

	public AppConfiguration save(AppConfiguration appConfig);

	public Date offSetTimeZone(Date date);

	public void setRecompileFlag();

}