package com.home.delivery.app.paths.tsp;

import com.home.delivery.app.paths.DistancesProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BruteForceTspSolver<T> implements TspSolver<T>
{

    final T origin;
    private final Collection<T> waypoints;
    private final DistancesProvider<T> distancesProvider;

    private Tour<T> minTour;

    public BruteForceTspSolver(T origin, Collection<T> waypoints, DistancesProvider<T> distancesProvider) {
        this.origin = origin;
        this.waypoints = waypoints;
        this.distancesProvider = distancesProvider;
    }

    @Override
    public Tour<T> findMinPath() {
        minTour = findMinGreedy();
        findMinTourRecursively(new Tour<T>(Collections.singletonList(origin), 0));
        return minTour;
    }

    private void findMinTourRecursively(Tour<T> tour) {
        if (tour.distance < minTour.distance) {
            List<T> unvisited = waypoints.stream().filter(t -> !tour.points.contains(t)).collect(Collectors.toList());
            T last = tour.getLast();
            if (unvisited.isEmpty()) {
                Tour<T> newTour = tour.add(origin, distancesProvider.getDistance(last, origin));
                if (newTour.distance < minTour.distance) minTour = newTour;
            } else {
                unvisited.forEach(p -> findMinTourRecursively(tour.add(p, distancesProvider.getDistance(last, p))));
            }
        }
    }

    private Tour<T> findMinGreedy() {
        Tour<T> t = new Tour<T>(Collections.singletonList(origin), 0);
        List<T> ps = new ArrayList<>(waypoints);
        while (!ps.isEmpty()) {
            T last = t.getLast();
            int min = Integer.MAX_VALUE;
            T closest = null;
            for (T p : ps) {
                int newMin = distancesProvider.getDistance(last, p);
                if (newMin < min) {
                    min = newMin;
                    closest = p;
                }
            }
            ps.remove(closest);
            t = t.add(closest, min);
        }
        int distanceToLast = distancesProvider.getDistance(t.getLast(), origin);
        return t.add(origin, distanceToLast);
    }
}
