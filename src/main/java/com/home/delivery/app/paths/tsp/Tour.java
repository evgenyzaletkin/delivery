package com.home.delivery.app.paths.tsp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Tour<T>
{
    final List<T> points;
    final int distance;

    public Tour(List<T> points, int distance) {
        this.points = points;
        this.distance = distance;
    }

    public T getLast() {
        return points.get(points.size() - 1);
    }

    public Tour<T> add(T point, int distanceToPoint) {
        List<T> newPoints = new ArrayList<>(points);
        newPoints.add(point);
        int newDistance = distance + distanceToPoint;
        return new Tour<>(newPoints, newDistance);
    }

    public List<T> getPath() {
        return Collections.unmodifiableList(points);
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public String toString()
    {
        return "Tour{Distance:" + distance + ", waypoints:" +
                points.stream().map(T::toString).collect(Collectors.joining("->")) + "}";
    }
}
