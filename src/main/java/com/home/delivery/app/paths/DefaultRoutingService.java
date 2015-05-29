package com.home.delivery.app.paths;

import com.home.delivery.app.Load;
import com.home.delivery.app.Utils;
import com.home.delivery.app.Waypoint;
import com.home.delivery.app.paths.tsp.BranchAndBoundTspSolver;
import com.home.delivery.app.paths.tsp.Tour;
import com.home.delivery.app.paths.tsp.TspSolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
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
    public void buildRoute(Load load) {
        List<String> addresses = load.getWaypoints().stream().map(Waypoint::getAddress).collect(Collectors.toList());
        addresses.add(Utils.ORIGIN_ADDRESS);
        long l = System.currentTimeMillis();
        Map<RouteElement, Integer> distances = googleMapsService.getDistances(addresses, addresses);
        log.info("Time to get distances " + (System.currentTimeMillis() - l));
        l = System.currentTimeMillis();
        DistancesProvider<String> distancesProvider = new SimpleDistanceProvider(distances);
        List<Waypoint> waypointsForRouting = getWaypointsForRouting(load);
        List<Waypoint> rest = load.getWaypoints().stream().filter(w -> !waypointsForRouting.contains(w)).collect(Collectors.toList());
        TspSolver<String> tspSolver = new BranchAndBoundTspSolver<>(Utils.ORIGIN_ADDRESS,
                waypointsForRouting.stream().map(Waypoint::getAddress).collect(Collectors.toList()), distancesProvider);
        Tour<String> optimal = tspSolver.findMinPath();
        List<String> path = optimal.getPath();
        waypointsForRouting.sort((w1, w2) -> Integer.compare(path.indexOf(w1.getAddress()), path.indexOf(w2.getAddress())));
        pushNotRoutedWaypoints(waypointsForRouting, rest, distancesProvider);
        load.setWaypoints(waypointsForRouting);
        log.info("Time to get path " + (System.currentTimeMillis() - l));
        log.info(String.format("The found tour is %s", optimal));
    }

    List<Waypoint> getWaypointsForRouting(Load load) {
        double availableVolume = Utils.MAX_VOLUME - load.getWaypoints().stream().mapToDouble(Waypoint::getDownloadVolume).sum();
        List<Waypoint> waypointsForRouting = new ArrayList<>();
        for (Waypoint waypoint : load.getWaypoints()) {
            double delta = waypoint.getUploadVolume() - waypoint.getDownloadVolume();
            if (delta <= availableVolume) {
                waypointsForRouting.add(waypoint);
                availableVolume -= delta;
            }
        }
        return waypointsForRouting;
    }

    void pushNotRoutedWaypoints(List<Waypoint> routed, List<Waypoint> rest, DistancesProvider<String> provider) {
        for (Waypoint w : rest) {
            Integer index = routed.stream().min((w1, w2) -> Integer.compare(provider.getDistance(w1.getAddress(), w.getAddress()),
                    provider.getDistance(w2.getAddress(), w.getAddress()))).
                    map(routed::indexOf).orElse(routed.size());
            routed.add(index, w);

        }
    }











}
