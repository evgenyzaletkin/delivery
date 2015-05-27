package com.home.delivery.web.controllers;

import com.home.delivery.app.ToolsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;

/**
 * Created by evgeny on 27.05.15.
 */
@Controller
@RequestMapping
public class ToolsController {


    private final ToolsService toolsService;

    @Inject
    public ToolsController(ToolsService toolsService) {
        this.toolsService = toolsService;
    }


    @RequestMapping("/reset")
    public String clearAllData() {
        toolsService.resetAll();
        return "redirect:deliveries";
    }
}
