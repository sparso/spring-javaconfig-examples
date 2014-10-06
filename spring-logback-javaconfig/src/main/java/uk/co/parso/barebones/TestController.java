package uk.co.parso.barebones;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Basic REST controller
 * 
 * @author sam
 */
@Controller
@RequestMapping("/")
public class TestController {
    
    private final Logger logger = LoggerFactory.getLogger(TestController.class);

    @RequestMapping("test")
    @ResponseBody
    public String ack() {
        logger.debug("This is some debug");
        logger.warn("This is a warning");
        
        Exception ex = new Exception("test exception");
        logger.error("This is an error",ex);
        
        return "ack";
    }
}
