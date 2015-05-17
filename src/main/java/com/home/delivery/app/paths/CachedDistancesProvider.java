package com.home.delivery.app.paths;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Named
public class CachedDistancesProvider implements DistancesProvider{

    private final GoogleMapsService mapsService;

    private final ConcurrentMap<RouteElement, Integer> cachedDistances = new ConcurrentHashMap<>();

    @Inject
    public CachedDistancesProvider(GoogleMapsService mapsService) {
        this.mapsService = mapsService;
    }

    @Override
    public Integer getDistance(String from, String to) {
        return cachedDistances.computeIfAbsent(new RouteElement(from, to),
                key -> mapsService.getDistances(Collections.singletonList(from), Collections.singletonList(to)).get(key));
    }
}
