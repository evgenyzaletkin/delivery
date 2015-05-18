package com.home.delivery.app.paths;

public interface DistancesProvider<T> {
    Integer getDistance(T from, T to);
}
