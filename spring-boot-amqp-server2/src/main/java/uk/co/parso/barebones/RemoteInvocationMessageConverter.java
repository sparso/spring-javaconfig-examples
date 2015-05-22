/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.parso.barebones;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.remoting.support.RemoteInvocation;

/**
 *
 * @author sam
 */
public class RemoteInvocationMessageConverter extends Jackson2JsonMessageConverter {

    private static final Logger log = LoggerFactory.getLogger(RemoteInvocationMessageConverter.class);

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        log.debug("Converting from message to object");

        Object obj = super.fromMessage(message);

        if (obj instanceof RemoteInvocation) {
            log.debug("Instance of RemoteInvocation");
            RemoteInvocation inv = (RemoteInvocation) obj;
            log.debug("Got remote invocation {}", inv.toString());
            ObjectMapper mapper = new ObjectMapper();
            
            List<Object> convertedObjects = new ArrayList<>();
            
            for( int i=0; i<inv.getParameterTypes().length; ++i) {
                Class<?> clazz = inv.getParameterTypes()[i];
                Object arg = inv.getArguments()[i];
                Object converted = mapper.convertValue(arg, clazz);
                convertedObjects.add(converted);
            }
            
            inv.setArguments(convertedObjects.toArray());
            
            /*for (Object arg : inv.getArguments()) {
                log.debug("ARG {}:{}", arg.getClass().getName(), arg.toString());
            }*/
        }

        return obj;
    }
}
