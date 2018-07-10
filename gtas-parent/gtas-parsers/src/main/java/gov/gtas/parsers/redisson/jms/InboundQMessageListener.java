package gov.gtas.parsers.redisson.jms;

import gov.gtas.parsers.edifact.EdifactLexer;
import gov.gtas.parsers.redisson.RedissonFilter;
import gov.gtas.parsers.redisson.concurrency.MessageFilterExecutorService;
import gov.gtas.parsers.redisson.concurrency.MessageFilterTask;
import gov.gtas.repository.AppConfigurationRepository;
import org.redisson.Redisson;
import org.redisson.api.RLiveObjectService;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@EnableJms
public class InboundQMessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(InboundQMessageListener.class);

    @Value("${redis.connection.string}")
    private String redisConnectionString;

    @Value("${inbound.loader.jms.queue}")
    private static final String INBOUND_QUEUE = "GTAS_INBOUND_Q_REDIS";

    @Autowired
    private InboundQMessageSender sender;

    @Value("${outbound.loader.jms.queue}")
    private String outboundLoaderQueue;

    @Autowired
    private AppConfigurationRepository appConfigRepository;

    private static final String MESSAGE_SEGMENT_BEGIN="UNH";
    private static final String MESSAGE_SEGMENT_END="UNT";

    private static RedissonClient client;
    private Config config = new Config();
    private RedissonFilter filter = new RedissonFilter(client);
    private RLiveObjectService service;
    private Long REDIS_KEYS_TTL=5L; // 5 Days - default
    private String REDIS_KEYS_TTL_TIME_UNIT="MINUTES"; // 5 Days - default

    @PostConstruct
    public void init(){
        LOG.info("++++++++++INIT Called+++++++++++++++++");
        config.useSingleServer().setAddress(redisConnectionString);
        //config.useSingleServer().setAddress(redisConnectionString);
        config.setNettyThreads(0);
        config.setThreads(0);
        client = Redisson.create(config);
        service = client.getLiveObjectService();
        REDIS_KEYS_TTL = Long.parseLong(appConfigRepository.findByOption(appConfigRepository.REDIS_KEYS_TTL).getValue());
        REDIS_KEYS_TTL_TIME_UNIT = appConfigRepository.findByOption(appConfigRepository.REDIS_KEYS_TTL_TIME_UNIT).getValue();
    }

    private MessageFilterExecutorService filterExecutorService = new MessageFilterExecutorService();
    private ExecutorService executor = Executors.newFixedThreadPool(10);


    @JmsListener(destination = INBOUND_QUEUE)
    public void receiveMessage(final Message<?> message) throws JMSException {

        LOG.info("++++++++Message Received++++++++++++");
        MessageHeaders headers =  message.getHeaders();

        try {

            if(client!=null && service!=null) {
                filter.redisObjectLookUpPersist((String)message.getPayload(), new Date(), service ,sender, outboundLoaderQueue,
                        (String)headers.get("filename"), client, REDIS_KEYS_TTL, REDIS_KEYS_TTL_TIME_UNIT);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        LOG.info("+++++++++++++++++++++++++++++");
    }


}
