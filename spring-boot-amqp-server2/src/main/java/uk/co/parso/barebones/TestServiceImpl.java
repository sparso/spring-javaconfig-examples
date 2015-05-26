/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.parso.barebones;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author sam
 */
@Service
public class TestServiceImpl implements TestService {
    
    private static final Logger log = LoggerFactory.getLogger(TestServiceImpl.class);

    @Override
    public String test(TestObject message,TestObject2 message2) {
        log.debug("test1: Received {}, {}, {}, {}",message.getBlah(),message2.getObj().getBlah(),message2.getObj2(),message2.getObj3());
        return "ACK";
    }

    @Override
    public TestObject test2(String param1, int param2) {
        log.debug("test2: Received {},{}",param1,param2);
        return new TestObject("test");
    }

    @Override
    public void test3() throws CustomException {
        throw new CustomException();
    }
    
}
