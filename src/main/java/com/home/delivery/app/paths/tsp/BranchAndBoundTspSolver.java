package com.home.delivery.app.paths.tsp;

import com.home.delivery.app.paths.DistancesProvider;

import java.util.*;
import java.util.stream.Collectors;

public class BranchAndBoundTspSolver<T> extends AbstractTspSolver<T> {

    final int[][] c;

    private static final Integer THRESHOLD = Integer.MAX_VALUE / 2;

    public BranchAndBoundTspSolver(T origin, List<T> waypoints, DistancesProvider<T> distancesProvider) {
        super(origin, waypoints, distancesProvider);
        c = makeMatrix();
    }




    @Override
    public Tour<T> findMinPath() {
//        System.out.println(waypoints);
//        System.out.println(origin);
//        System.out.println(printArray(c));
        minTour = findMinGreedy();
        Node root = createRootNode();
        processNode(root);
        return minTour;
    }



    void processNode(Node node) {
//        System.out.println(node);
        if (node.size > 2) {
            List<Element> keys = findKeysInMatrix(node.a);
            PriorityQueue<Node> queue = new PriorityQueue<>((n1, n2) -> Integer.compare(n1.cost, n2.cost));
            for (Element key : keys) {
                queue.offer(leftNode(node, key));
                if (!isCycle(node, key))
                    queue.offer(rightNode(node, key));
            }
            while (!queue.isEmpty()) {
                Node child = queue.poll();
                if (!checkCost(child)) break;
                processNode(child);
            }
        } else processSmallNode(node);
    }

    void processSmallNode(Node node) {
        List<Element> possibleElements = new ArrayList<>();
        for (int i = 0; i < node.a.length; i++)
            for (int j = 0; j < node.a.length; j++)
                if (node.a[i][j] < THRESHOLD) possibleElements.add(new Element(i, j));

        for (int i = 0; i < possibleElements.size(); i++) {
            for (int j = i + 1; j < possibleElements.size(); j++) {
                List<Element> elements = getPathForNode(node);
                elements.add(possibleElements.get(i));
                elements.add(possibleElements.get(j));
                List<Element> path = makePathFromElements(elements);
                if (path != null) {
                    Tour<T> newTour = convertPathToTour(path);
                    if (newTour.distance < minTour.distance)
                        minTour = newTour;
                }
            }
        }
    }

    private Tour<T> convertPathToTour(List<Element> path) {
        Integer distance = 0;
        List<T> points = new ArrayList<>(path.size());
        for (Element element : path) {
            distance += c[element.i][element.j];
            points.add(element.i == 0 ? origin : waypoints.get(element.i - 1));
        }
        points.add(origin);
        return new Tour<>(points, distance);
    }


    List<Element> getPathForNode(Node n) {
        List<Element> path = new ArrayList<>();
        while (n != null) {
            if (n.e != null) path.add(n.e);
            n = n.parent;
        }
        return path;
    }

    List<Element> makePathFromElements(Collection<Element> elements) {
        int target = 0;
        List<Element> path = new ArrayList<>();
        while (true) {
            Element found = null;
            for (Element e : elements)
                if (e.i == target) found = e;
            if (found == null) return null;
            for (Element e : path) {
                if (e.j == found.j) return null;
            }
            path.add(found);
            if (path.size() == elements.size())
                return found.j == 0 ? path : null;
            target = found.j;
        }
    }


    private String printArray(int[][] a) {
        return Arrays.stream(a).map(Arrays::toString).map(s -> "{" + s + "}").collect(Collectors.joining(",\n"));
    }

    boolean checkCost(Node node) {
        return node.cost < minTour.distance;
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
        return elementsByCosts.isEmpty() ? Collections.emptyList() : elementsByCosts.lastEntry().getValue();
    }

    Node createRootNode() {
        int[][] initial = cloneArray(c);
        int cost = reduceAndGetCost(initial);
        Node root = new Node();
        root.a = initial;
        root.cost = cost;
        root.size = initial.length;
        return root;
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
        Node left = createNode(a, parent);
        left.size = parent.size;
        return left;
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
        a[e.j][e.i] = Integer.MAX_VALUE;
        forbidPreviousElements(parent, e, a);
        Node right = createNode(a, parent);
        right.e = e;
        right.size = parent.size - 1;
        return right;
    }

    boolean isCycle(Node node, Element e) {
        int target = e.j;
        boolean keep = true;
        List<Element> pathForNode = getPathForNode(node);
        while (keep) {
            keep = false;
            for (Element pathElement : pathForNode) {
                if (pathElement.i == target) {
                    if (pathElement.j == e.i)
                        return true;
                    target = pathElement.j;
                    keep = true;
                    break;
                }
            }
        }
        return false;
    }

    void forbidPreviousElements (Node node, Element curElement, int[][] a) {
        int target = curElement.i;
        List<Element> pathForNode = getPathForNode(node);
        boolean chainFound = true;
        while (chainFound) {
            chainFound = false;
            for (Element pathElement : pathForNode) {
                if (pathElement.j == target) {
                    chainFound = true;
                    a[curElement.j][pathElement.i] = Integer.MAX_VALUE;
                    target = pathElement.i;
                    break;
                }
            }
        }
    }

    Node createNode(int[][] a, Node parent) {
        int newCost = reduceAndGetCost(a) + parent.cost;
        Node newNode = new Node();
        newNode.a = a;
        newNode.cost = newCost;
        newNode.parent = parent;
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
        int size;
        Node parent;

        @Override
        public String toString() {
            return "Node{" +
                    "size=" + size +
                    ", cost=" + cost +
                    ", e=" + e +
                    ",a=" + printArray(a) +
                    '}';
        }
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

        @Override
        public String toString() {
            return "Element{" +
                    "i=" + i +
                    ", j=" + j +
                    '}';
        }
    }


}
