package com.home.delivery.app.paths.dummy;

import com.home.delivery.app.Delivery;
import com.home.delivery.app.paths.*;
import com.home.delivery.app.paths.tsp.BruteForceTspSolver;
import com.home.delivery.app.paths.tsp.Tour;
import com.home.delivery.app.paths.tsp.TspSolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by evgeny on 16.05.15.
 */
@Named
public class BruteForceRoutingService implements RoutingService
{

    final GoogleMapsService googleMapsService;
    private static final Log log = LogFactory.getLog(BruteForceRoutingService.class);

    @Inject
    public BruteForceRoutingService(GoogleMapsService googleMapsService) {
        this.googleMapsService = googleMapsService;
    }


    @Override
    public List<Delivery> buildRoute(List<Delivery> deliveries) {
        Map<String, List<Delivery>> groupedByAddress = deliveries.stream().collect(Collectors.groupingBy(this::mapToAddress));
        List<String> waypoints = new ArrayList<>(groupedByAddress.keySet());
        waypoints.add(ORIGIN_ADDRESS);
        Map<RouteElement, Integer> distances = googleMapsService.getDistances(waypoints, waypoints);
        DistancesProvider<String> distancesProvider = new SimpleDistanceProvider(distances);
        TspSolver<String> tspSolver = new BruteForceTspSolver<>(ORIGIN_ADDRESS, groupedByAddress.keySet(), distancesProvider);
        Tour<String> optimal = tspSolver.findMinPath();
        log.info(String.format("The found tour is %s", optimal));
        return optimal.getPath().stream().
                filter(s -> !s.equals(ORIGIN_ADDRESS)).
                flatMap(s -> groupedByAddress.get(s).stream()).
                collect(Collectors.toList());
    }

    private int getDistanceForPath(List<String> points, DistancesProvider<String> distancesProvider) {
        if (points.size() == 0) return 0;
        Iterator<String> iterator = points.iterator();
        String previous = iterator.next();
        int distance = distancesProvider.getDistance(ORIGIN_ADDRESS, previous);
        while (iterator.hasNext()) {
            String next = iterator.next();
            distance += distancesProvider.getDistance(previous, next);
            previous = next;
        }
        distance += distancesProvider.getDistance(previous, ORIGIN_ADDRESS);
        return distance;
    }


    private static final String ORIGIN_ADDRESS = "RALEIGH, NC 27603";

    String mapToAddress(Delivery d) {
        return d.getStreet() + ", " + mapToCity(d);
    }

    String mapToCity(Delivery d) {
        return d.getCity() + ", " + d.getState() + " " + d.getZip();
    }

}
