/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.AppConfiguration;
import gov.gtas.repository.AppConfigurationRepository;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author gbays
 */
@Service
public class AppConfigurationServiceImpl implements AppConfigurationService {

	@Resource
	private AppConfigurationRepository appConfigurationRepository;

	@Override
	public AppConfiguration findByOption(String option) {
		AppConfiguration appConfig = appConfigurationRepository.findByOption(option);

		return appConfig;

	}

	public void setRecompileFlag() {
		AppConfiguration ap = appConfigurationRepository.findByOption("RECOMPILE_RULES");
		ap.setValue("true");
		appConfigurationRepository.save(ap);
	}

	public Date offSetTimeZone(Date date) {
		if (date == null) {
			return null;
		}
		int hour_offset = Integer
				.parseInt(appConfigurationRepository.findByOption(AppConfigurationRepository.HOURLY_ADJ).getValue());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, hour_offset);
		return calendar.getTime();
	}

	@Override
	public AppConfiguration save(AppConfiguration appConfig) {
		appConfigurationRepository.save(appConfig);
		return appConfig;
	}

}
