package com.home.delivery.app.paths.tsp;

import com.google.common.base.Preconditions;
import com.home.delivery.app.paths.DistancesProvider;
import com.home.delivery.app.paths.RouteElement;
import com.home.delivery.app.paths.SimpleDistanceProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        int[][] c = Preconditions.checkNotNull(solver.c);
        int[] reductionByRows = solver.getReductionByRows(c);
        Assert.assertArrayEquals(new int[]{0, 1, 1, 0}, reductionByRows);

    }

    @Test
    public void reductionByColumnsShouldReturnArrayWithTheSmallestElementInEachColumn() throws Exception {
        int[][] c = Preconditions.checkNotNull(solver.c);
        int[] reductionByColumns = solver.getReductionByColumns(c);
        Assert.assertArrayEquals(new int[]{2, 0, 0, 2}, reductionByColumns);
    }
}
