package com.home.delivery.app.paths.tsp;

import com.home.delivery.app.paths.DistancesProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by evgeny on 24.05.15.
 */
public abstract class AbstractTspSolver<T> implements TspSolver<T> {

    protected final T origin;
    protected final List<T> waypoints;
    protected final DistancesProvider<T> distancesProvider;

    protected Tour<T> minTour;

    public AbstractTspSolver(T origin, List<T> waypoints, DistancesProvider<T> distancesProvider) {
        this.origin = origin;
        this.waypoints = waypoints;
        this.distancesProvider = distancesProvider;
    }


    protected Tour<T> findMinGreedy() {
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
