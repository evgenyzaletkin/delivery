package com.home.delivery.app.paths;

import com.home.delivery.app.Load;
import com.home.delivery.app.Utils;
import com.home.delivery.app.Waypoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

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
//        List<String> addresses = load.getWaypoints().stream().map(Waypoint::getAddress).collect(Collectors.toList());
//        addresses.add(Utils.ORIGIN_ADDRESS);
//        long l = System.currentTimeMillis();
//        Map<RouteElement, Integer> distances = googleMapsService.getDistances(addresses, addresses);
//        log.info("Time to get distances " + (System.currentTimeMillis() - l));
//        l = System.currentTimeMillis();
//        DistancesProvider<String> distancesProvider = new SimpleDistanceProvider(distances);
//        TspSolver<String> tspSolver = new BranchAndBoundTspSolver<>(Utils.ORIGIN_ADDRESS,
//                waypoints.stream().map(Waypoint::getAddress).collect(Collectors.toList()), distancesProvider);
//        Tour<String> optimal = tspSolver.findMinPath();
//        log.info("Time to get path " + (System.currentTimeMillis() - l));
//        log.info(String.format("The found tour is %s", optimal));
    }

    public List<Waypoint> getWaypointsForRouting(Load load) {
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







}
