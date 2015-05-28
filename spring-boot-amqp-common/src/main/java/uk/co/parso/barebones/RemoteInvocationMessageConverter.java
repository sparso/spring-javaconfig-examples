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
import java.lang.reflect.InvocationTargetException;
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
    public static final String TARGET_EXCEPTION_CLASSID_FIELD_NAME = "__TargetExceptionTypeId__";

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        log.debug("Converting from message to object. message: {}", message.toString());

        Object obj = super.fromMessage(message);

        log.debug("Converted to object type {}", obj.getClass().getCanonicalName());
        log.debug("Obj: {}", obj);

        // Received RMI request
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
        } else if( obj instanceof RemoteInvocationResult ) { // Received RMI response
            log.debug("Instance of RemoteInvocationResult");
            RemoteInvocationResult res = (RemoteInvocationResult) obj;
            
            // There's a value/exception in the response and we set a header to define what 
            // java type to convert it to. So let's go ahead and do that..
            if( (res.getValue() != null || res.hasException() || res.hasInvocationTargetException())
                    && message.getMessageProperties().getHeaders().get(RESPONSE_CLASSID_FIELD_NAME) != null ) {
                String objTypeStr = message.getMessageProperties().getHeaders().get(RESPONSE_CLASSID_FIELD_NAME).toString();
                log.debug("Converting to {}",objTypeStr);
                
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Class<?> clazz = Class.forName(objTypeStr);

                    if( res.getValue() != null ) {
                        // Convert the method response value
                        log.debug("Converting value");
                        res.setValue(mapper.convertValue(res.getValue(), clazz));
                    } else if (objTypeStr.equals("java.lang.reflect.InvocationTargetException")) {
                        // Special case - anything thrown from the actual service is wrapped in an InvocationTargetException 
                        // which doesn't have the necessary setters to be unmarshalled from JSON. So we just create a new one 
                        // containing the original throwable..
                        log.debug("Converting to InvocationTargetException");
                        res.setException(new InvocationTargetException(res.getException()));
                    } else {
                        log.debug("Converting exception");
                        res.setException((Throwable)mapper.convertValue(res.getException(), clazz));
                    }
                } catch (ClassNotFoundException ex) {
                    log.error("Unable to convert RemoteInvocationResult object to {}, class not found",objTypeStr);
                }
            }
            
            // The response contains an exception which is an instanceof InvocationTargetException. The exception inside this class 
            // is the one actually thrown by the target service, so we have to convert that to the right java object as well!
            if( res.hasInvocationTargetException() && message.getMessageProperties().getHeaders().get(TARGET_EXCEPTION_CLASSID_FIELD_NAME) != null ) {
                String objTypeStr = message.getMessageProperties().getHeaders().get(TARGET_EXCEPTION_CLASSID_FIELD_NAME).toString();
                log.debug("Converting target exception to {}",objTypeStr);
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Class<?> clazz = Class.forName(objTypeStr);
                    InvocationTargetException targetEx = (InvocationTargetException)res.getException();
                    res.setException(new InvocationTargetException((Throwable)mapper.convertValue(targetEx.getTargetException(), clazz)));
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
            } else if(res.hasException() || res.hasInvocationTargetException()) {
                log.debug("Instance of RemoteInvocationResult with exception of type {}",res.getException().getClass().getCanonicalName());
                msg.getMessageProperties().setHeader(RESPONSE_CLASSID_FIELD_NAME, res.getException().getClass().getCanonicalName());
            }
            
            if(res.hasInvocationTargetException()) {
                InvocationTargetException e = (InvocationTargetException)res.getException();
                log.debug("Invocation target exception with target type {}",e.getTargetException().getClass().getCanonicalName());
                msg.getMessageProperties().setHeader(TARGET_EXCEPTION_CLASSID_FIELD_NAME, e.getTargetException().getClass().getCanonicalName());
            }
        }

        return msg;
    }
}
