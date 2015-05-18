package com.home.delivery.app.paths;

import com.home.delivery.app.Delivery;

import java.util.List;

/**
 * Created by evgeny on 16.05.15.
 */
public interface RoutingService {
    public List<Delivery> buildRoute(List<Delivery> deliveries);
}
