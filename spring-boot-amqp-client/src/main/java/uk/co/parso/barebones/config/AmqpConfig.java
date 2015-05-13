package uk.co.parso.barebones.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.remoting.client.AmqpProxyFactoryBean;
import org.springframework.amqp.remoting.service.AmqpInvokerServiceExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import uk.co.parso.barebones.TestService;

/**
 * Root application context configuration
 *
 * @author sam
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AmqpConfig {

    private static final Logger log = LoggerFactory.getLogger(AmqpConfig.class);

    final static String queueName = "spring-boot-amqp-server";
    final static String DIRECT_EXCHANGE = "amq.direct";

    @Autowired
    AnnotationConfigApplicationContext context;
    
    @Autowired
    private TestService testService;

    // The connection to the message queue
    @Bean
    public CachingConnectionFactory connectionFactory() {
        return new CachingConnectionFactory("localhost", 5672);
    }
    
    // The amqp template provides basic operations like send/receive
    @Bean
    public AmqpTemplate amqpTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setReplyTimeout(10_000);
        rabbitTemplate.setRoutingKey(queueName);
        return rabbitTemplate;
    }
    
    // Exposes the proxied service for use as a bean reference, using the specified service interface.
    // Calling a method on the proxy will cause an AMQP message being sent according to the configured AmqpTemplate.
    @Bean
    public AmqpProxyFactoryBean client() {
        AmqpProxyFactoryBean factory = new AmqpProxyFactoryBean();
        factory.setServiceInterface(TestService.class);
        factory.setAmqpTemplate(amqpTemplate());
        return factory;
    }
    
    @Scheduled(fixedDelay=10_000)
    public void sendMessage() {
        log.debug("Sending message");
        String response = testService.test("HELLO");
        log.debug("Received response: {}",response);
    }
}
