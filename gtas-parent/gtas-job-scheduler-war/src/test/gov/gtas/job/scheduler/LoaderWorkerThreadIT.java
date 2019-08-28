/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.job.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;


import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
//TECHNICALLY NOT AN IT. This takes a very long time to run and is therefore classified as an IT.
public class LoaderWorkerThreadIT {

    @Mock
    LoaderScheduler loaderScheduler;

    @InjectMocks
    LoaderWorkerThread loaderWorkerThread;

    private Semaphore semaphore = new Semaphore(0); //0 permits because loader queue thread manager will add acquire a permit.

    @Before
    public void before() {
        initMocks(this);
        // Default behavior will be sufficent for test here.
        BlockingQueue<Message<?>> queue = new ArrayBlockingQueue<>(1024);
        Message<?> message = new Message<Object>() {
            @Override
            public Object getPayload() {
                return "testing payload.";
            }

            @Override
            public MessageHeaders getHeaders() {
                return new MessageHeaders(new HashMap<>());
            }
        };
        queue.add(message);
        loaderWorkerThread.setQueue(queue);
        loaderWorkerThread.setMap(new ConcurrentHashMap<>());
        loaderWorkerThread.setPrimeFlightKey("testQueue");
        loaderWorkerThread.setSemaphore(semaphore);

    }

    @Test
    public void testSemaphore() throws InterruptedException {
        loaderWorkerThread.run();
        Thread.sleep(6000);
        assert semaphore.availablePermits() == 1;
    }

}
