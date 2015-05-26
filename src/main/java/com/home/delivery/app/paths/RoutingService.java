package com.home.delivery.app.paths;

import com.home.delivery.app.Delivery;
import com.home.delivery.app.paths.tsp.Tour;

import java.util.List;

/**
 * Created by evgeny on 16.05.15.
 */
public interface RoutingService {
    public Tour<String> buildRoute(List<Delivery> deliveries);
}
