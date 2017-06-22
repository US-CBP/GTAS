/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.perf.test;

import gov.gtas.model.udr.json.UdrSpecification;
import gov.gtas.svc.UdrService;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ConfigurableApplicationContext;

public class FetchUdrTest implements PerformanceTestFactory {
    private UdrService udrService;
    private int parallelRequestCount = 1000;
    private int poolTimeoutSeconds = 500;

    public FetchUdrTest(final int parallelRequestCount, final int poolTimeout){
        this.parallelRequestCount =  parallelRequestCount;
        this.poolTimeoutSeconds = poolTimeout;
    }
    @Override
    public PerformanceTest createTest(ConfigurableApplicationContext ctx) {
        this.udrService = (UdrService) ctx.getBean("udrServiceImpl");
        return new PerformanceTest() {          
            @Override
            public List<String> runTest() {
                return runFetchUdrTest();
            }
        };
    }
    private List<String> runFetchUdrTest(){
        ExecutorService exec = Executors.newWorkStealingPool(10);
        List<String> ret = new LinkedList<String>();
        
        long udrStart = System.currentTimeMillis();

        for(int i = 0; i < parallelRequestCount; i++){
            exec.execute(createCommand((i%300)+1,udrService));
        }
        try{
          exec.shutdown();
          exec.awaitTermination(poolTimeoutSeconds, TimeUnit.SECONDS);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        long udrElapsed = System.currentTimeMillis() - udrStart;
        ret.add("Total Time = " + udrElapsed
                + ", Average Time = " + (udrElapsed / 300));
        return ret;     
    }
    private static Runnable createCommand(final int udrNum, final UdrService udrService){
        final String title = "PerfTestUdr"+udrNum;
        final String userId = "bstygar";
        return new Runnable(){

            @Override
            public void run() {
                UdrSpecification spec = udrService.fetchUdr(userId, title);
                if(spec == null){
                    System.out.println(">>>>>>>> ERROR cannot find UDR:"+title);
                } 
            }
            
        };
    }
}
