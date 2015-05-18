package com.home.delivery.app.paths;

import java.util.Map;

public class SimpleDistanceProvider implements DistancesProvider<String>{

    private final Map<RouteElement, Integer> distances;

    public SimpleDistanceProvider(Map<RouteElement, Integer> distances) {
        this.distances = distances;
    }

    @Override
    public Integer getDistance(String from, String to) {
        RouteElement re = new RouteElement(from, to);
        return distances.get(re);
    }


}
