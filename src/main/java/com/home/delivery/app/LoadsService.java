package com.home.delivery.app;

import com.home.delivery.app.paths.RoutingService;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Named("loadsService")
public class LoadsService {

    private final ExecutorService executorService;
    private final RoutingService routingService;

    @Inject
    public LoadsService(ExecutorService executorService,
                        RoutingService routingService) {
        this.executorService = executorService;
        this.routingService = routingService;
    }

    public List<Load> findAll() {
        return Collections.emptyList();
    }

    public List<Load> getLoad(LocalDate date, DeliveryShift shift) {
        return null;
    }

    public void addLoad(Load load) {
        executorService.submit(() -> {
            List<Delivery> deliveries = load.getDeliveries();
            List<Delivery> sorted = routingService.buildRoute(deliveries);
        });
    }
}
