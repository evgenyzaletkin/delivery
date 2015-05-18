package com.home.delivery.app.paths.tsp;

import com.home.delivery.app.paths.DistancesProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BruteForceTspSolver<T> implements TspSolver<T>
{

    final T origin;
    private final List<T> source;
    private final DistancesProvider<T> distancesProvider;

    private Tour<T> minTour;

    public BruteForceTspSolver(T origin, List<T> source, DistancesProvider<T> distancesProvider) {
        this.origin = origin;
        this.source = source;
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
            List<T> unvisited = source.stream().filter(t -> !tour.points.contains(t)).collect(Collectors.toList());
            if (unvisited.isEmpty()) minTour = tour;
            T last = tour.getLast();
            for (T p : unvisited) {
                findMinTourRecursively(tour.add(p, distancesProvider.getDistance(p, last)));
            }
        }
    }

    private Tour<T> findMinGreedy() {
        Tour<T> t = new Tour<T>(Collections.singletonList(origin), 0);
        List<T> ps = new ArrayList<>(source);
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
