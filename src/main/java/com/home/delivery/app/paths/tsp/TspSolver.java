package com.home.delivery.app.paths.tsp;

public interface TspSolver<T> {
    Tour<T> findMinPath();
}
