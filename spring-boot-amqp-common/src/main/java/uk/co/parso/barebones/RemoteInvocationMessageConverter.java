/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.parso.barebones;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.util.ClassUtils;

/**
 *
 * @author sam
 */
public class RemoteInvocationMessageConverter extends Jackson2JsonMessageConverter {

    private static final Logger log = LoggerFactory.getLogger(RemoteInvocationMessageConverter.class);

    public static final String RESPONSE_CLASSID_FIELD_NAME = "__ResponseTypeId__";

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        log.debug("Converting from message to object. message: {}", message.toString());

        Object obj = super.fromMessage(message);

        log.debug("Converted to object type {}", obj.getClass().getCanonicalName());
        log.debug("Obj: {}", obj);

        if (obj instanceof RemoteInvocation) {
            log.debug("Instance of RemoteInvocation");
            RemoteInvocation inv = (RemoteInvocation) obj;
            log.debug("Got remote invocation {}", inv.toString());
            ObjectMapper mapper = new ObjectMapper();

            List<Object> convertedObjects = new ArrayList<>();

            for (int i = 0; i < inv.getParameterTypes().length; ++i) {
                Class<?> clazz = inv.getParameterTypes()[i];
                Object arg = inv.getArguments()[i];
                Object converted = mapper.convertValue(arg, clazz);
                convertedObjects.add(converted);
            }

            inv.setArguments(convertedObjects.toArray());
        } else if( obj instanceof RemoteInvocationResult ) {
            log.debug("Instance of RemoteInvocationResult");
            RemoteInvocationResult res = (RemoteInvocationResult) obj;
            
            if( res.getValue() != null && message.getMessageProperties().getHeaders().get(RESPONSE_CLASSID_FIELD_NAME) != null ) {
                String objTypeStr = message.getMessageProperties().getHeaders().get(RESPONSE_CLASSID_FIELD_NAME).toString();
                log.debug("Converting to {}",objTypeStr);
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Class<?> clazz = Class.forName(objTypeStr);
                    res.setValue(mapper.convertValue(res.getValue(), clazz));
                } catch (ClassNotFoundException ex) {
                    log.error("Unable to convert RemoteInvocationResult object to {}, class not found",objTypeStr);
                }
            }
        }

        return obj;
    }

    @Override
    protected Message createMessage(Object objectToConvert, MessageProperties messageProperties) throws MessageConversionException {
        log.debug("Converting from object type {} to message", objectToConvert.getClass().getCanonicalName());

        Message msg = super.createMessage(objectToConvert, messageProperties);

        if (objectToConvert instanceof RemoteInvocationResult) {
            RemoteInvocationResult res = (RemoteInvocationResult) objectToConvert;

            if (res.getValue() != null) {
                log.debug("Instance of RemoteInvocationResult with value type {}", res.getValue().getClass().getCanonicalName());
                msg.getMessageProperties().setHeader(RESPONSE_CLASSID_FIELD_NAME, res.getValue().getClass().getCanonicalName());
            }
        }

        return msg;
    }
}
