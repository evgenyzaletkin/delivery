package com.home.delivery.app.paths.tsp;

import com.home.delivery.app.paths.DistancesProvider;

import java.util.*;

public class BranchAndBoundTspSolver<T> implements TspSolver<T> {

    private final T origin;
    private final List<T> waypoints;
    private final DistancesProvider<T> distancesProvider;
    final int[][] c;

    private static final Integer THRESHOLD = Integer.MAX_VALUE / 2;

    public BranchAndBoundTspSolver(T origin, List<T> waypoints, DistancesProvider<T> distancesProvider) {
        this.origin = origin;
        this.waypoints = waypoints;
        this.distancesProvider = distancesProvider;
        c = makeMatrix();
    }


    @Override
    public Tour<T> findMinPath() {
        int[][] initial = cloneArray(c);
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




    List<Element> findKeysInMatrix(int[][] a) {
        TreeMap<Integer, List<Element>> elementsByCosts = new TreeMap<>();
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                //Here we need additional check that this element doesn't create cycle.
                if (a[i][j] == 0) {
                    a[i][j] = Integer.MAX_VALUE;
                    int x = minInRow(a, i) + minInColumn(a, j);
                    a[i][j] = 0;
                    List<Element> elements = elementsByCosts.get(x);
                    if (elements == null) {
                        elements = new ArrayList<>();
                        elementsByCosts.put(x, elements);
                    }
                    elements.add(new Element(i, j));
                }
            }
        }
        return elementsByCosts.lastEntry().getValue();
    }


    /**
     * Left node for the current node will be created.
     * <ol>
     *     <li>The given element will have the infinite value in the new node's array</li>
     *     <li>The new array will be reduced</li>
     *     <li>The cost of the new node will be the cost of the current node plus reduction cost</li>
     * </ol>
     * @param parent
     * @param e
     * @return
     */
    Node leftNode(Node parent, Element e) {
        int[][] a = cloneArray(parent.a);
        a[e.i][e.j] = Integer.MAX_VALUE;
        return createNode(a, parent.cost);
    }

    /**
     * Right node for the current node will be created
     * <ol>
     *     <li>All elements from the same row and same column as the given element will be infinite</li>
     *     <li>The new array will be reduced</li>
     *     <li>The cost of the new node will be the cost of the current node plus reduction cost</li>
     * </ol>
     * @param parent
     * @param e
     * @return
     */
    Node rightNode(Node parent, Element e) {
        int[][] a = cloneArray(parent.a);
        for (int i = 0; i < a.length; i++) {
            a[e.i][i] = Integer.MAX_VALUE;
            a[i][e.j] = Integer.MAX_VALUE;
        }
        Node right = createNode(a, parent.cost);
        right.e = e;
        return right;
    }

    Node createNode(int[][] a, int currentCost) {
        int newCost = reduceAndGetCost(a) + currentCost;
        Node newNode = new Node();
        newNode.a = a;
        newNode.cost = newCost;
        return newNode;
    }

    int reduceAndGetCost(int[][] a) {
        return reduceRows(a, getReductionByRows(a)) + reduceColumns(a, getReductionByColumns(a));
    }



    int[] getReductionByRows(int[][] c) {

        int[] result = new int[c.length];
        for (int i = 0; i < c.length; i++) {
            result[i] = minInRow(c, i);
        }
        return result;
    }

    int[] getReductionByColumns(int[][] c) {
        int[] result = new int[c.length];
        for (int j = 0; j < c.length; j++) {
            result[j] = minInColumn(c, j);
        }
        return result;

    }

    int reduceRows(int[][] a, int[] reduceKoefs) {
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += reduceKoefs[i];
            for (int j = 0; j < a.length; j++)
                a[i][j] -= reduceKoefs[i];
        }
        return sum;
    }

    int reduceColumns(int[][] a, int[] reduceKoefs) {
        int sum = 0;
        for (int j = 0; j < a.length; j++) {
            sum += reduceKoefs[j];
            for (int i = 0; i < a.length; i++)
                a[i][j] -= reduceKoefs[j];
        }
        return sum;
    }

    int[][] cloneArray(int[][] a) {
        int[][] clone = new int[a.length][a.length];
        for (int i = 0; i < a.length; i++)
            clone[i] = Arrays.copyOf(a[i], a[i].length);
        return clone;
    }


    int minInRow(int[][] a, int i) {
        int min = THRESHOLD;
        for (int el : a[i]) {
            min = el < min ? el : min;
        }
        return min == THRESHOLD ? 0 : min;
    }

    int minInColumn(int[][] a, int j) {
        int min = THRESHOLD;
        for (int i = 0; i < a.length; i++)
            min = a[i][j] < min ? a[i][j] : min;
        return min == THRESHOLD ? 0 : min;
    }




    class Node {
        int[][] a;
        List<Node> children;
        int cost;
        Element e;
    }

    class Element {
        int i;
        int j;

        public Element(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Element element = (Element) o;
            return Objects.equals(i, element.i) &&
                    Objects.equals(j, element.j);
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, j);
        }
    }


}
