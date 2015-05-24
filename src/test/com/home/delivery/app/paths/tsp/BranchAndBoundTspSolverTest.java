package com.home.delivery.app.paths.tsp;

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
        Assert.assertNotEquals(c ,b);
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

    @Test
    public void rootNodeCreation() throws Exception {
        int[][] clone = solver.cloneArray(solver.c);
        BranchAndBoundTspSolver<String>.Node root = solver.createNode(clone, 0);
        Assert.assertEquals(6, root.cost);
    }

    @Test
    public void leftNodeCreation() throws Exception {
        int[][] clone = solver.cloneArray(solver.c);
        BranchAndBoundTspSolver<String>.Node root = solver.createNode(clone, 0);
        BranchAndBoundTspSolver.Element e = solver.new Element(2, 1);

        BranchAndBoundTspSolver<String>.Node left = solver.leftNode(root, e);
        Assert.assertEquals(left.a[e.i][e.j], Integer.MAX_VALUE - 5);
        Assert.assertEquals(left.cost, root.cost + 5);
    }

    @Test
    public void rightNodeCreation() throws Exception {
        int[][] clone = solver.cloneArray(solver.c);
        BranchAndBoundTspSolver<String>.Node root = solver.createNode(clone, 0);
        BranchAndBoundTspSolver.Element e = solver.new Element(2, 1);

        BranchAndBoundTspSolver<String>.Node right = solver.rightNode(root, e);
        for (int i = 0; i < clone.length; i++) {
            Assert.assertEquals(Integer.MAX_VALUE, right.a[2][i]);
            Assert.assertEquals(Integer.MAX_VALUE, right.a[i][1]);
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



}
