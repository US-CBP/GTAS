/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.YTDAirportStatistics;
import gov.gtas.model.YTDRules;
import gov.gtas.repository.YTDAirportStatisticsRepository;
import gov.gtas.repository.YTDStatisticsRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class YTDStatisticsServiceImpl implements YTDStatisticsService {


    @Resource
    private YTDStatisticsRepository ytdStatisticsRepository;

    @Resource
    private YTDAirportStatisticsRepository ytdAirportStatisticsRepository;


    @Override
    @Transactional
    public List<YTDRules> getYTDRules() {
        YTDRules stat = new YTDRules();
        List<YTDRules> statsList = (List<YTDRules>)ytdStatisticsRepository.findAll();
        return statsList;
    }

    @Override
    @Transactional
    public List<YTDAirportStatistics> getYTDAirportStats() {
        YTDAirportStatistics airportStatistics = new YTDAirportStatistics();
        List<YTDAirportStatistics> statsList = (List<YTDAirportStatistics>)ytdAirportStatisticsRepository.findAll();
        return statsList;
    }
}
