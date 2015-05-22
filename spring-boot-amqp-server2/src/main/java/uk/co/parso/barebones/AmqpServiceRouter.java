/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.parso.barebones;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.remoting.service.AmqpInvokerServiceExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.support.RemoteInvocation;

/**
 *
 * @author sam
 */
public class AmqpServiceRouter implements MessageListener {
    
    private static final Logger log = LoggerFactory.getLogger(AmqpServiceRouter.class);
    
    private Map<String,AmqpInvokerServiceExporter> exporters;
    
    @Autowired
    private TestService testService;
    
    public AmqpServiceRouter() {
        exporters = new HashMap<>();
    }
    
    public void addServiceExporter(String routingKey, AmqpInvokerServiceExporter exporter) {
        exporters.put(routingKey, exporter);
    }

    @Override
    public void onMessage(Message msg) {
        AmqpInvokerServiceExporter exporter = exporters.get(msg.getMessageProperties().getReceivedRoutingKey());
        
        if( exporter != null ) {
            Object content = exporter.getMessageConverter().fromMessage(msg);
            
	    /*if (content instanceof RemoteInvocation) {
		RemoteInvocation inv = (RemoteInvocation) content;
                log.debug("Got remote invocation {}",inv.toString());
                for(Object arg: inv.getArguments()) {
                    log.debug("ARG {}:{}",arg.getClass().getName(),arg.toString());
                }
                try {
                    inv.invoke(testService);
                } catch (        NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                    log.debug("ERROR EXECUTING",ex);
                }
	    }*/
                
            exporter.onMessage(msg);
        } else {
            log.error("Unknown routing key " + msg.getMessageProperties().getReceivedRoutingKey());
        }
    }
    
}
