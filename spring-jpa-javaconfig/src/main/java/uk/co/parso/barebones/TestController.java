package uk.co.parso.barebones;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.co.parso.barebones.entities.Test;
import uk.co.parso.barebones.repositories.TestRepository;

/**
 * Basic REST controller
 * 
 * @author sam
 */
@Controller
@RequestMapping("/")
public class TestController {
    
    private final Logger logger = LoggerFactory.getLogger(TestController.class);
    
    @Autowired
    private TestService testService;

    @RequestMapping("test")
    @ResponseStatus(HttpStatus.OK)
    public void test() {
        testService.test();
    }
}
