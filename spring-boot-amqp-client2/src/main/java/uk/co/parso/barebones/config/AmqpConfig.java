package uk.co.parso.barebones.config;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.remoting.client.AmqpProxyFactoryBean;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.RemoteProxyFailureException;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import uk.co.parso.barebones.TestObject;
import uk.co.parso.barebones.TestObject2;
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
        rabbitTemplate.setExchange("amq.direct");
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    public static Jackson2JsonMessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setClassMapper(new DefaultJackson2JavaTypeMapper());
        return converter;
    }

    // Exposes the proxied service for use as a bean reference, using the specified service interface.
    // Calling a method on the proxy will cause an AMQP message being sent according to the configured AmqpTemplate.
    private AmqpProxyFactoryBean makeProxyFactoryBean(Class<?> serviceInterface, String routingKey) {
        AmqpProxyFactoryBean factory = new AmqpProxyFactoryBean();
        factory.setServiceInterface(serviceInterface);
        factory.setAmqpTemplate(amqpTemplate());
        factory.setRoutingKey(routingKey);
        return factory;
    }

    // Create proxies for remote services
    @Bean
    public AmqpProxyFactoryBean testServiceProxy() {
        return makeProxyFactoryBean(TestService.class, "test_service");
    }

    @Scheduled(fixedDelay = 10_000)
    public void sendMessage() {
        log.debug("Sending message");
        try {
            Map<String, String> msg = new HashMap<>();
            msg.put("TEST", "TEST");
            TestObject obj = new TestObject();
            obj.setBlah("blah blah");
            TestObject2 obj2 = new TestObject2(obj, 5L, -2);
            String response = testService.test(obj, obj2);
            log.debug("Received response: {}", response);
            int response2 = testService.test2("aaaa", 1111);
            log.debug("Received response2: {}", response2);
        } catch (RemoteProxyFailureException e) {
            log.error("Error sending message",e);
        }
    }
}
