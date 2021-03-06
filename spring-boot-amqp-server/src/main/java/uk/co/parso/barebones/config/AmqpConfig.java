package uk.co.parso.barebones.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.remoting.service.AmqpInvokerServiceExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.parso.barebones.TestService;

/**
 * Root application context configuration
 *
 * @author sam
 */
@Configuration
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
        //rabbitTemplate.setReplyQueue(replyQueue());
        //rabbitTemplate.setReplyTimeout(10000);
        return rabbitTemplate;
    }

    // Declare a queue on which to receive messages
    @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }
    
    // Message listener container that uses the plain JMS client API's 
    // MessageConsumer.setMessageListener() method to create concurrent MessageConsumers for the specified listeners.
    @Bean
    SimpleMessageListenerContainer container() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueueNames(queueName);
        container.setMessageListener(listener());
        return container;
    }
    
    // Message listener that invokes the service interface on the server-side
    @Bean
    public AmqpInvokerServiceExporter listener() {
        AmqpInvokerServiceExporter exporter = new AmqpInvokerServiceExporter();
        exporter.setServiceInterface(TestService.class);
        exporter.setService(testService);
        exporter.setAmqpTemplate(amqpTemplate());
        return exporter;
    }   
}
