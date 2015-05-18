/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.parso.barebones;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author sam
 */
@Service
public class TestServiceImpl2 implements TestService2 {
    
    private static final Logger log = LoggerFactory.getLogger(TestServiceImpl2.class);

    @Override
    public String test2(String message) {
        log.debug("Received {}",message);
        return "ACK";
    }
    
}
