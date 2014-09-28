package uk.co.parso.barebones;

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

    @RequestMapping("test")
    @ResponseBody
    public String ack() {
        return "ack";
    }
}
