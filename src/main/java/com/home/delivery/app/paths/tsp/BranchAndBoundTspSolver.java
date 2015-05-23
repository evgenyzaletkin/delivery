package com.home.delivery.app.paths.tsp;

import com.home.delivery.app.paths.DistancesProvider;

import java.util.List;

public class BranchAndBoundTspSolver<T> implements TspSolver<T>
{

    private final T origin;
    private final List<T> waypoints;
    private final DistancesProvider<T> distancesProvider;
    final int[][] c;

    public BranchAndBoundTspSolver(T origin, List<T> waypoints, DistancesProvider<T> distancesProvider) {
        this.origin = origin;
        this.waypoints = waypoints;
        this.distancesProvider = distancesProvider;
        c = makeMatrix();
    }


    @Override
    public Tour<T> findMinPath() {
        return null;
    }

    int[][] makeMatrix() {
        int length = waypoints.size() + 1;
        int[][] c = new int[length][length];
        int i = 1;
        c[0][0] = Integer.MAX_VALUE;
        for (T waypoint : waypoints) {
            c[0][i] = distancesProvider.getDistance(origin, waypoint);
            c[i][0] = distancesProvider.getDistance(waypoint, origin);
            i++;
        }

        i = 1;
        int j = 1;
        for (T start : waypoints) {
            for (T end : waypoints) {
                c[i][j] = i == j ? Integer.MAX_VALUE : distancesProvider.getDistance(start, end);
                j++;
            }
            i++;
            j = 1;
        }
        return c;
    }

    int calcCost(Node node) {

    }

    int[] getReductionByRows(Node node) {

        int[] result = new int[c.length];
        for (int i = 0; i < c.length; i++) {
            if (node.containsLine(i)) {
                    result[i] = Integer.MAX_VALUE;
                    for (int element : c[i]) {
                        if (element < result[i]) result[i] = element;
                    }
            }
        }
        return result;
    }

    int[] getReductionByColumns() {
        int[] result = new int[c.length];
        for (int j = 0; j < c.length; j++) {
            result[j] = Integer.MAX_VALUE;
            for (int i = 0; i < c.length; i++)
                if (c[i][j] < result[j]) result[j] = c[i][j];
        }
        return result;

    }

    abstract class Node {
        int cost;
        Node left;
        Node right;

        int getCost(){
            return cost;
        }

        Node getLeftNode() {
            return left;
        }

        Node getRightNode() {
            return right;
        }

        abstract Node getParent();
        abstract boolean containsLine(int i);
        abstract int getValue(int i, int j);


    }

    class Root extends Node {

        @Override
        Node getParent() {
            return null;
        }

        @Override
        boolean containsLine(int i) {
            return true;
        }

        @Override
        int getValue(int i, int j) {
            return c[i][j];
        }


    }

    class Left extends Node {

        Node parent;
        int i;
        int j;

        Left(Node parent, int i, int j) {
            this.parent = parent;
            this.i = i;
            this.j = j;
        }

        @Override
        Node getParent() {
            return parent;
        }

        @Override
        boolean containsLine(int i) {
            return this.i != i && parent.containsLine(i);
        }

        @Override
        int getValue(int i, int j) {
            return this.i == j && this.j == i ? Integer.MAX_VALUE : parent.getValue(i, j);
        }
    }

    class Right extends Node {

        Node parent;
        int i;
        int j;

        Right(Node parent, int i, int j) {
            this.parent = parent;
            this.i = i;
            this.j = j;
        }

        @Override
        Node getParent() {
            return parent;
        }

        @Override
        boolean containsLine(int i) {
            return parent.containsLine(i);
        }

        @Override
        int getValue(int i, int j) {
            return this.i == i && this.j == j ? Integer.MAX_VALUE : parent.getValue(i, j);
        }
    }


}
