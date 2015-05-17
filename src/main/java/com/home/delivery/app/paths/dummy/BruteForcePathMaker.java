package com.home.delivery.app.paths.dummy;

import com.google.common.collect.Collections2;
import com.home.delivery.app.Delivery;
import com.home.delivery.app.paths.DistancesProvider;
import com.home.delivery.app.paths.PathMaker;
import com.home.delivery.app.paths.StatefulDistancesProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by evgeny on 16.05.15.
 */
public class BruteForcePathMaker implements PathMaker {

    final Provider<StatefulDistancesProvider> distancesProviderProvider;


    @Inject
    public BruteForcePathMaker(Provider<StatefulDistancesProvider> distancesProviderProvider) {
        this.distancesProviderProvider = distancesProviderProvider;
    }


    @Override
    public List<Delivery> makeTheShortestPath(List<Delivery> deliveries) {
        Map<String, List<Delivery>> groupedByAddress = deliveries.stream().collect(Collectors.groupingBy(this::mapToAddress));
        List<String> possiblePoints = new ArrayList<>(groupedByAddress.keySet());
        possiblePoints.add(ORIGIN_ADDRESS);
        StatefulDistancesProvider provider = distancesProviderProvider.get();
        long time = System.currentTimeMillis();
        provider.setPoints(possiblePoints);
        System.out.printf("Time to get distances %d", System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        Optional<List<String>> opt = Collections2.permutations(deliveries.stream().map(this::mapToAddress).collect(Collectors.toList())).
                stream().
                collect(Collectors.minBy((xs1, xs2) ->
                Integer.compare(getDistanceForPath(xs1, provider), getDistanceForPath(xs2, provider))));
        System.out.printf("Time to find min path %d", System.currentTimeMillis() - time);
        List<String> minPoints = opt.get();
        List<Delivery> minDeliveries = new ArrayList<>(minPoints.size());
        for (String point : minPoints) {
            minDeliveries.add(groupedByAddress.get(point).get(0));
        }
        return minDeliveries;
    }

    private int getDistanceForPath(List<String> points, DistancesProvider distancesProvider) {
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
        return d.getStreet() + ", " + d.getCity() + ", " + d.getState() + " " + d.getZip();
    }
}
