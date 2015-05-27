package com.home.delivery.app;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by evgeny on 28.05.15.
 */
@Named
public class ToolsService {

    private final DeliveriesService deliveriesService;
    private final LoadsService loadsService;

    @Inject
    public ToolsService(DeliveriesService deliveriesService, LoadsService loadsService) {
        this.deliveriesService = deliveriesService;
        this.loadsService = loadsService;
    }

    public void resetAll() {
        deliveriesService.reset();
        loadsService.reset();
    }
}
