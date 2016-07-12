/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.ApisStatistics;
import gov.gtas.model.DashboardMessageStats;
import gov.gtas.model.PnrStatistics;
import gov.gtas.repository.ApisStatisticsRepository;
import gov.gtas.repository.DashboardMessageStatsRepository;
import gov.gtas.repository.PnrStatisticsRepository;
import java.util.List;
import javax.annotation.Resource;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class MessageStatisticsServiceImpl implements MessageStatisticsService {

    @Resource
    private PnrStatisticsRepository pnrStatisticsRepository;
    
    @Resource
    private ApisStatisticsRepository apisStatisticsRepository;

    @Resource
    private DashboardMessageStatsRepository dashboardMessageStatsRepository;

    private String api_message_string = "API";
    private String pnr_message_string = "PNR";


    @Override
    @Transactional
    public DashboardMessageStats getDashboardAPIMessageStats() {
        DashboardMessageStats stat=new DashboardMessageStats();
        List<DashboardMessageStats> statsList= (List<DashboardMessageStats>)dashboardMessageStatsRepository.getMessages(api_message_string);
        if(statsList != null && statsList.size() >0){
            stat=statsList.get(0);
        }
        return stat;
    }

    @Override
    @Transactional
    public DashboardMessageStats getDashboardPNRMessageStats() {
        DashboardMessageStats stat=new DashboardMessageStats();
        List<DashboardMessageStats> statsList= (List<DashboardMessageStats>)dashboardMessageStatsRepository.getMessages(pnr_message_string);
        if(statsList != null && statsList.size() >0){
            stat=statsList.get(0);
        }
        return stat;
    }

    @Override
    @Transactional
    public PnrStatistics getPnrStatistics() {
        PnrStatistics stat=new PnrStatistics();
        List<PnrStatistics> statsList=(List<PnrStatistics>)pnrStatisticsRepository.findAll();
        if(statsList != null && statsList.size() >0){
            stat=statsList.get(0);
        }
        return stat ;
    }

    @Override
    @Transactional
    public ApisStatistics getApisStatistics() {
        ApisStatistics stat = new ApisStatistics();
        List<ApisStatistics> statsList = (List<ApisStatistics>)apisStatisticsRepository.findAll();
        if(statsList != null && statsList.size() >0){
            stat=statsList.get(0);
        }
        return stat;
    }

}
