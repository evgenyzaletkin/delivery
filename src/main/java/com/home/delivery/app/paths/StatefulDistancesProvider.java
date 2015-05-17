package com.home.delivery.app.paths;

import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;

@Named
@Scope("prototype")
public class StatefulDistancesProvider implements DistancesProvider{

    private final GoogleMapsService mapsService;
    private Map<RouteElement, Integer> distances;

    @Inject
    public StatefulDistancesProvider(GoogleMapsService mapsService) {
        this.mapsService = mapsService;
    }

    public void setPoints(List<String> points) {
        distances = mapsService.getDistances(points, points);
    }

    @Override
    public Integer getDistance(String from, String to){
        return distances.get(new RouteElement(from, to));
    }
}
