package com.home.delivery.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by evgeny on 08.05.15.
 */
@Controller
@RequestMapping("/")
public class GreetingController {

    @RequestMapping(method = RequestMethod.GET)
    public String sayHello() {
        return "greeting";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/dispatcher")
    public String dispatcherMenu() {
        return "dispatcher";
    }
}
