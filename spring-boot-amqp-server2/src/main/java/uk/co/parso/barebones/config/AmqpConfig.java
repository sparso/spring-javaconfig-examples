package uk.co.parso.barebones.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.remoting.service.AmqpInvokerServiceExporter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.parso.barebones.AmqpServiceRouter;
import uk.co.parso.barebones.TestService;
import uk.co.parso.barebones.TestService2;

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
    
    @Autowired
    private TestService2 testService2;

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
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setJsonObjectMapper(new ObjectMapper());
        return converter;
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
        container.setMessageListener(router());
        return container;
    }
       
    public AmqpInvokerServiceExporter createServiceExporter(Class<?> serviceInterface,Object service) {
        AmqpInvokerServiceExporter exporter = new AmqpInvokerServiceExporter();
        exporter.setServiceInterface(serviceInterface);
        exporter.setService(service);
        exporter.setAmqpTemplate(amqpTemplate());
        exporter.setMessageConverter(jsonMessageConverter());
        return exporter;
    }   
    
    public Binding createBinding(Queue queue,String routingKey) {
        log.debug("Binding to queue with routing key {}",routingKey);
        return BindingBuilder.bind(queue).to(new DirectExchange(DIRECT_EXCHANGE)).with(routingKey);
    }
    
    // For each service add a binding and a service exporter entry
    
    @Bean
    public Binding testServiceBinding() {
        return createBinding(queue(),"test_service");
    }
    
    @Bean
    public Binding testService2Binding() {
        return createBinding(queue(),"test_service2");
    }
    
    @Bean
    public AmqpServiceRouter router() {
        AmqpServiceRouter router = new AmqpServiceRouter();
        
        router.addServiceExporter("test_service", createServiceExporter(TestService.class,testService));
        router.addServiceExporter("test_service2", createServiceExporter(TestService2.class,testService2));
        
        return router;
    }
}
