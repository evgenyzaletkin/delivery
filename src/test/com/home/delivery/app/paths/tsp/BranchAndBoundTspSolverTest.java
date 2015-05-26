package com.home.delivery.app.paths.tsp;

import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.home.delivery.app.paths.DistancesProvider;
import com.home.delivery.app.paths.RouteElement;
import com.home.delivery.app.paths.SimpleDistanceProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class BranchAndBoundTspSolverTest {

    String p1 = "1";
    String p2 = "2";
    String p3 = "3";
    String p4 = "4";
    Map<RouteElement, Integer> distances = new HashMap<>();
    BranchAndBoundTspSolver<String> solver;

    @Before
    public void setUp() throws Exception {
        distances.put(new RouteElement(p1, p1), Integer.MAX_VALUE);
        distances.put(new RouteElement(p1, p2), 5);
        distances.put(new RouteElement(p1, p3), 0);
        distances.put(new RouteElement(p1, p4), 2);

        distances.put(new RouteElement(p2, p1), 5);
        distances.put(new RouteElement(p2, p2), Integer.MAX_VALUE);
        distances.put(new RouteElement(p2, p3), 1);
        distances.put(new RouteElement(p2, p4), 6);

        distances.put(new RouteElement(p3, p1), 8);
        distances.put(new RouteElement(p3, p2), 1);
        distances.put(new RouteElement(p3, p3), Integer.MAX_VALUE);
        distances.put(new RouteElement(p3, p4), 9);

        distances.put(new RouteElement(p4, p1), 2);
        distances.put(new RouteElement(p4, p2), 0);
        distances.put(new RouteElement(p4, p3), 9);
        distances.put(new RouteElement(p4, p4), Integer.MAX_VALUE);
        System.out.println(distances);

        DistancesProvider<String> provider = new SimpleDistanceProvider(distances);
        List<String> waypoints = Arrays.asList(p2, p3, p4);
        solver = new BranchAndBoundTspSolver<>(p1, waypoints, provider);
    }

    @Test
    public void makeMatrixFromProvider() throws Exception {
        int[][] c = solver.makeMatrix();
        System.out.println(Arrays.deepToString(c));

        Assert.assertEquals(c[0][0], Integer.MAX_VALUE);
        Assert.assertEquals(c[0][1], 5);
        Assert.assertEquals(c[0][2], 0);
        Assert.assertEquals(c[0][3], 2);

        Assert.assertEquals(c[1][0], 5);
        Assert.assertEquals(c[1][1], Integer.MAX_VALUE);
        Assert.assertEquals(c[1][2], 1);
        Assert.assertEquals(c[1][3], 6);

        Assert.assertEquals(c[2][0], 8);
        Assert.assertEquals(c[2][1], 1);
        Assert.assertEquals(c[2][2], Integer.MAX_VALUE);
        Assert.assertEquals(c[2][3], 9);

        Assert.assertEquals(c[3][0], 2);
        Assert.assertEquals(c[3][1], 0);
        Assert.assertEquals(c[3][2], 9);
        Assert.assertEquals(c[3][3], Integer.MAX_VALUE);


    }

    @Test
    public void reductionByRowsShouldReturnArrayWithTheSmallestElementsInEachRow() throws Exception {
        int[][] c = checkNotNull(solver.c);
        int[] reductionByRows = solver.getReductionByRows(c);
        Assert.assertArrayEquals(new int[]{0, 1, 1, 0}, reductionByRows);

    }

    @Test
    public void reductionByColumnsShouldReturnArrayWithTheSmallestElementInEachColumn() throws Exception {
        int[][] c = checkNotNull(solver.c);
        int[] reductionByColumns = solver.getReductionByColumns(c);
        Assert.assertArrayEquals(new int[]{2, 0, 0, 2}, reductionByColumns);
    }

    @Test
    public void cloneShouldReturnArrayWithTheSameElements() throws Exception {
        int[][] c = checkNotNull(solver.c);
        int[][] b = solver.cloneArray(c);
        Assert.assertNotEquals(c, b);
        Assert.assertTrue(Arrays.deepEquals(c, b));
    }


    @Test
    public void reduceRowsShouldSubtractElementTheSameElementFromEachRow() throws Exception {
        int[][] c = checkNotNull(solver.c);
        int[] reductionByRows = solver.getReductionByRows(c);
        int[][] a = solver.cloneArray(c);
        int sum = solver.reduceRows(a, reductionByRows);
        Assert.assertEquals(2, sum);
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++)
                Assert.assertEquals(c[i][j] - a[i][j], reductionByRows[i]);
        }

    }

    @Test
    public void reduceRowsShouldSubtractElementTheSameElementFromEachColumn() throws Exception {
        int[][] c = checkNotNull(solver.c);
        int[] reductionByColumns = solver.getReductionByColumns(c);
        int[][] a = solver.cloneArray(c);
        int sum = solver.reduceColumns(a, reductionByColumns);
        Assert.assertEquals(4, sum);
        for (int j = 0; j < a.length; j++) {
            for (int i = 0; i < a.length; i++)
                Assert.assertEquals(c[i][j] - a[i][j], reductionByColumns[j]);
        }

    }

    @Test
    public void fullReduction() throws Exception {
        int[][] expected = new int[][]{
                {Integer.MAX_VALUE, 5, 0, 0},
                {2, Integer.MAX_VALUE, 0, 3},
                {5, 0, Integer.MAX_VALUE, 6},
                {0, 0, 9, Integer.MAX_VALUE}
        };
        int[][] a = solver.cloneArray(checkNotNull(solver.c));
        int cost = solver.reduceAndGetCost(a);
        Assert.assertEquals(6, cost);
//        Assert.assertTrue(Arrays.deepEquals(expected, a));

    }

    private BranchAndBoundTspSolver.Node emptyNode() {
        return solver.new Node();
    }

    @Test
    public void rootNodeCreation() throws Exception {
        int[][] clone = solver.cloneArray(solver.c);
        BranchAndBoundTspSolver<String>.Node root = solver.createNode(clone, emptyNode());
        Assert.assertEquals(6, root.cost);
    }


    @Test
    public void leftNodeCreation() throws Exception {
        int[][] clone = solver.cloneArray(solver.c);
        BranchAndBoundTspSolver<String>.Node root = solver.createNode(clone, emptyNode());
        BranchAndBoundTspSolver.Element e = solver.new Element(2, 1);

        BranchAndBoundTspSolver<String>.Node left = solver.leftNode(root, e);
        Assert.assertEquals(left.a[e.i][e.j], Integer.MAX_VALUE - 5);
        Assert.assertEquals(left.cost, root.cost + 5);
    }

    @Test
    public void rightNodeCreation() throws Exception {
        int[][] clone = solver.cloneArray(solver.c);
        BranchAndBoundTspSolver<String>.Node root = solver.createNode(clone, emptyNode());
        BranchAndBoundTspSolver.Element e = solver.new Element(2, 1);

        BranchAndBoundTspSolver<String>.Node right = solver.rightNode(root, e);
        for (int i = 0; i < clone.length; i++) {
            Assert.assertEquals(Integer.MAX_VALUE, right.a[2][i]);
            Assert.assertEquals(Integer.MAX_VALUE - 2, right.a[i][1]);
        }
        Assert.assertEquals(root.cost, right.cost);
    }

    @Test
    public void findKeysInMatrix() {
        int[][] redused = new int[][]{
                {Integer.MAX_VALUE, 5, 0, 0},
                {2, Integer.MAX_VALUE, 0, 3},
                {5, 0, Integer.MAX_VALUE, 6},
                {0, 0, 9, Integer.MAX_VALUE}
        };
        List<BranchAndBoundTspSolver.Element> expected = Collections.singletonList(solver.new Element(2, 1));
        List<BranchAndBoundTspSolver<String>.Element> actual = solver.findKeysInMatrix(redused);
        Assert.assertTrue(Iterables.elementsEqual(expected, actual));

    }

    @Test
    public void makePathFromElementsPathExists() throws Exception {
        BranchAndBoundTspSolver<String>.Element e0 = solver.new Element(0, 1);
        BranchAndBoundTspSolver<String>.Element e1 = solver.new Element(1, 2);
        BranchAndBoundTspSolver<String>.Element e2 = solver.new Element(2, 3);
        BranchAndBoundTspSolver<String>.Element e3 = solver.new Element(3, 4);
        BranchAndBoundTspSolver<String>.Element e4 = solver.new Element(4, 0);
        Collection<BranchAndBoundTspSolver<String>.Element> expected = Arrays.asList(e0, e1, e2, e3, e4);
        for (Collection<BranchAndBoundTspSolver<String>.Element> c : Collections2.permutations(expected)) {
            Assert.assertTrue(Iterables.elementsEqual(expected, solver.makePathFromElements(c)));
        }
    }

    @Test
    public void makePathFromElementsElementsContainsDoulbesInTheEnd() throws Exception {
        BranchAndBoundTspSolver<String>.Element e0 = solver.new Element(0, 1);
        BranchAndBoundTspSolver<String>.Element e1 = solver.new Element(1, 2);
        BranchAndBoundTspSolver<String>.Element e2 = solver.new Element(2, 3);
        BranchAndBoundTspSolver<String>.Element e3 = solver.new Element(3, 4);
        BranchAndBoundTspSolver<String>.Element e4 = solver.new Element(4, 0);
        BranchAndBoundTspSolver<String>.Element e5 = solver.new Element(4, 0);
        Collection<BranchAndBoundTspSolver<String>.Element> expected = Arrays.asList(e0, e1, e2, e3, e4, e5);
        for (Collection<BranchAndBoundTspSolver<String>.Element> c : Collections2.permutations(expected)) {
            Assert.assertNull(solver.makePathFromElements(c));
        }
    }

    @Test
    public void makePathFromElementsElementsContainsDoulbesInTheMiddle() throws Exception {
        BranchAndBoundTspSolver<String>.Element e0 = solver.new Element(0, 1);
        BranchAndBoundTspSolver<String>.Element e1 = solver.new Element(1, 2);
        BranchAndBoundTspSolver<String>.Element e2 = solver.new Element(2, 3);
        BranchAndBoundTspSolver<String>.Element e3 = solver.new Element(3, 4);
        BranchAndBoundTspSolver<String>.Element e4 = solver.new Element(4, 0);
        BranchAndBoundTspSolver<String>.Element e5 = solver.new Element(2, 3);
        Collection<BranchAndBoundTspSolver<String>.Element> expected = Arrays.asList(e0, e1, e2, e3, e4, e5);
        for (Collection<BranchAndBoundTspSolver<String>.Element> c : Collections2.permutations(expected)) {
            Assert.assertNull(solver.makePathFromElements(c));
        }
    }

    @Test
    public void makePathFromElementsElementsDoesNotContainEnd() throws Exception {
        BranchAndBoundTspSolver<String>.Element e0 = solver.new Element(0, 1);
        BranchAndBoundTspSolver<String>.Element e1 = solver.new Element(1, 2);
        BranchAndBoundTspSolver<String>.Element e2 = solver.new Element(2, 3);
        BranchAndBoundTspSolver<String>.Element e3 = solver.new Element(3, 4);
        Collection<BranchAndBoundTspSolver<String>.Element> expected = Arrays.asList(e0, e1, e2, e3);
        for (Collection<BranchAndBoundTspSolver<String>.Element> c : Collections2.permutations(expected)) {
            Assert.assertNull(solver.makePathFromElements(c));
        }
    }


    @Test
    public void findMnPathTest() throws Exception {
        Tour<String> optimal = solver.findMinPath();
        Assert.assertEquals(optimal.distance, 9);
    }

    @Test
    public void test5x5() throws Exception {
        String origin = "1";
        List<String> waypoints = Arrays.asList("2", "3", "4", "5");
        int[][] a = new int[][]{
                {Integer.MAX_VALUE, 10, 25, 25, 10},
                {1, Integer.MAX_VALUE, 10, 15, 2},
                {8, 9, Integer.MAX_VALUE, 20, 10},
                {14, 10, 24, Integer.MAX_VALUE, 15},
                {10, 8, 25, 27, Integer.MAX_VALUE}
        };
        Map<RouteElement, Integer> distances = convertArrayToMap(a);
        DistancesProvider<String> provider = new SimpleDistanceProvider(distances);
        BranchAndBoundTspSolver<String> tsp = new BranchAndBoundTspSolver<String>(origin, waypoints, provider);
        Tour<String> minPath = tsp.findMinPath();
        Assert.assertEquals(62, minPath.distance);

    }

    @Test
    public void test6x6() throws Exception {
        String origin = "1";
        List<String> waypoints = Arrays.asList("2", "3", "4", "5", "6");
        int[][] a = new int[][]{
                {Integer.MAX_VALUE, 31, 15, 9, 8, 55},
                {19, Integer.MAX_VALUE, 22, 31, 7, 35},
                {25, 43, Integer.MAX_VALUE, 53, 57, 16},
                {5, 50, 49, Integer.MAX_VALUE, 39, 9},
                {24, 24, 33, 5, Integer.MAX_VALUE, 14},
                {34, 26, 6, 3, 36, Integer.MAX_VALUE}
        };
        Map<RouteElement, Integer> distances = convertArrayToMap(a);
        DistancesProvider<String> provider = new SimpleDistanceProvider(distances);
        BranchAndBoundTspSolver<String> tsp = new BranchAndBoundTspSolver<String>(origin, waypoints, provider);
        Tour<String> minPath = tsp.findMinPath();
        Assert.assertEquals(74, minPath.distance);

    }

    private Map<RouteElement, Integer> convertArrayToMap(int[][] a) {
        Map<RouteElement, Integer> map = new HashMap<>(a.length * a.length);
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a.length; j++) {
                RouteElement element = new RouteElement((i + 1) + "", (j + 1) + "");
                map.put(element, a[i][j]);
            }
        return map;
    }

    @Test
    public void test17x17() throws Exception {
        String origin = "1";
        List<String> waypoints = Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17");
        int[][] a = new int[][]{
                {2147483647, 75422, 109441, 87035, 81858, 76813, 81138, 97134, 102063, 90880, 73206, 74423, 86390, 70530, 85911, 93641, 85513},
                {76783, 2147483647, 19917, 18412, 13234, 6749, 15008, 13617, 16387, 22861, 2590, 2920, 19057, 5817, 19645, 26004, 18180},
                {110397, 21807, 2147483647, 13290, 8261, 16039, 13005, 8432, 4585, 11157, 22971, 20858, 14592, 23397, 31875, 14869, 15373},
                {88803, 18465, 13285, 2147483647, 5687, 12697, 8936, 12937, 10888, 9282, 19629, 19107, 8398, 23779, 30208, 12425, 9179},
                {83568, 13231, 8244, 5909, 2147483647, 7462, 5625, 7702, 5653, 10358, 14395, 13872, 9474, 18544, 24973, 13501, 7951},
                {77333, 6749, 15172, 12644, 7466, 2147483647, 9240, 9713, 10619, 17092, 7913, 7391, 13289, 12063, 21925, 20236, 12412},
                {82465, 15011, 12980, 7723, 5382, 9242, 2147483647, 12391, 10583, 10600, 16175, 15652, 6110, 20325, 28311, 13361, 5233},
                {97955, 15220, 8411, 12977, 7800, 9452, 12471, 2147483647, 6530, 17426, 16384, 15862, 15674, 17185, 19963, 20569, 14797},
                {103441, 17227, 4558, 10831, 5653, 11458, 10546, 7116, 2147483647, 12880, 18391, 17869, 14396, 21659, 25449, 16592, 12873},
                {92207, 22851, 11157, 9186, 10072, 17082, 10600, 17391, 12888, 2147483647, 24015, 23492, 7266, 28164, 42753, 5551, 8047},
                {74277, 2577, 21220, 19563, 14386, 7901, 14651, 14920, 17539, 24012, 2147483647, 2901, 20209, 6063, 27382, 27156, 19332},
                {75494, 2930, 20696, 19039, 13861, 7376, 15635, 14396, 17014, 23487, 2087, 2147483647, 19684, 7279, 20423, 26631, 18807},
                {87683, 19025, 14592, 8321, 9208, 13257, 6077, 15561, 14409, 7266, 20189, 19667, 2147483647, 24339, 31002, 10027, 1826},
                {72407, 4893, 22347, 22802, 17624, 11139, 19398, 16046, 20621, 27250, 5140, 6357, 23447, 2147483647, 22760, 30394, 22570},
                {86396, 19748, 31929, 30183, 25005, 21316, 29484, 18924, 23853, 43554, 20912, 20390, 31436, 22867, 2147483647, 37775, 30559},
                {94920, 25949, 14841, 12285, 13171, 20181, 13314, 20421, 16571, 5522, 27113, 26591, 9980, 31263, 46436, 2147483647, 10301},
                {87453, 18795, 16141, 10477, 8322, 13026, 5846, 15331, 13523, 8815, 19959, 19436, 2422, 24109, 30771, 8952, 2147483647}
        };
        Map<RouteElement, Integer> distances = convertArrayToMap(a);
        DistancesProvider<String> provider = new SimpleDistanceProvider(distances);
        BranchAndBoundTspSolver<String> tsp = new BranchAndBoundTspSolver<String>(origin, waypoints, provider);
        Tour<String> minPath = tsp.findMinPath();

    }

}
