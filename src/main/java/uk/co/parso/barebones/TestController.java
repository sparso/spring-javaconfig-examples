/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.parso.barebones;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author serverteam
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
