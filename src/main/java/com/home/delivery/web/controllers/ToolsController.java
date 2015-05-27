package com.home.delivery.web.controllers;

import com.home.delivery.app.DeliveriesService;
import com.home.delivery.app.LoadsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;

/**
 * Created by evgeny on 27.05.15.
 */
@Controller
@RequestMapping
public class ToolsController {

    private final DeliveriesService deliveriesService;
    private final LoadsService loadsService;

    @Inject
    public ToolsController(DeliveriesService deliveriesService, LoadsService loadsService) {
        this.deliveriesService = deliveriesService;
        this.loadsService = loadsService;
    }

    @RequestMapping("/reset")
    public String clearAllData() {
        deliveriesService.reset();
        loadsService.reset();
        return "redirect:deliveries";
    }
}
