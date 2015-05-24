package com.home.delivery.app.paths.tsp;

import com.home.delivery.app.paths.DistancesProvider;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BruteForceTspSolver<T> extends AbstractTspSolver<T>
{



    public BruteForceTspSolver(T origin, List<T> waypoints, DistancesProvider<T> distancesProvider) {
        super(origin, waypoints, distancesProvider);
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


}
