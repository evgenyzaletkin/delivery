package com.home.delivery.app.paths;

import com.google.common.collect.Lists;
import com.home.delivery.app.Delivery;
import com.home.delivery.app.paths.tsp.BranchAndBoundTspSolver;
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
public class DefaultRoutingService implements RoutingService
{

    final GoogleMapsService googleMapsService;
    private static final Log log = LogFactory.getLog(DefaultRoutingService.class);

    @Inject
    public DefaultRoutingService(GoogleMapsService googleMapsService) {
        this.googleMapsService = googleMapsService;
    }


    @Override
    public Tour<String> buildRoute(List<Delivery> deliveries) {
        Map<String, List<Delivery>> groupedByAddress = deliveries.stream().collect(Collectors.groupingBy(Utils::mapToAddress));
        List<String> waypoints = new ArrayList<>(groupedByAddress.keySet());
        waypoints.add(Utils.ORIGIN_ADDRESS);
        long l = System.currentTimeMillis();
        Map<RouteElement, Integer> distances = googleMapsService.getDistances(waypoints, waypoints);
        log.info("Time to get distances " + (System.currentTimeMillis() - l));
        l = System.currentTimeMillis();
        DistancesProvider<String> distancesProvider = new SimpleDistanceProvider(distances);
        TspSolver<String> tspSolver = new BranchAndBoundTspSolver<>(Utils.ORIGIN_ADDRESS,
                Lists.newArrayList(groupedByAddress.keySet()), distancesProvider);
        Tour<String> optimal = tspSolver.findMinPath();
        log.info("Time to get path " + (System.currentTimeMillis() - l));
        log.info(String.format("The found tour is %s", optimal));
        return optimal;
    }

    private int getDistanceForPath(List<String> points, DistancesProvider<String> distancesProvider) {
        if (points.size() == 0) return 0;
        Iterator<String> iterator = points.iterator();
        String previous = iterator.next();
        int distance = distancesProvider.getDistance(Utils.ORIGIN_ADDRESS, previous);
        while (iterator.hasNext()) {
            String next = iterator.next();
            distance += distancesProvider.getDistance(previous, next);
            previous = next;
        }
        distance += distancesProvider.getDistance(previous, Utils.ORIGIN_ADDRESS);
        return distance;
    }






}
