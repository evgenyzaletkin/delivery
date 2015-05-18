package com.home.delivery.app.paths.tsp;

import com.home.delivery.app.paths.DistancesProvider;
import org.junit.Test;

import java.util.*;

public class BruteForceTspSolverTest
{
    @Test
    public void testFindMinPath() throws Exception
    {
        String origin = "A0";
        int n = 15;
        List<String> waypoints = new ArrayList<>(n);
        for (int i = 0; i <= n; i++) {
            waypoints.add("A" + i);
        }
        Map<String, Integer> distances = new HashMap<>();
        DistancesProvider<String> provider = new DistancesProvider<String>()
        {

            Random rand = new Random();
            @Override
            public Integer getDistance(String from, String to)
            {
                String key = from + "->" + to;
                if (!distances.containsKey(key)) {
                    String reversed = to + "->" + from;
                    int dis = rand.nextInt(10) + 50;
                    distances.put(key, dis);
                    distances.put(reversed, dis);
                }
                return distances.get(key);
            }
        };

        TspSolver<String> solver = new BruteForceTspSolver<>(origin, waypoints, provider);
        Tour<String> minPath = solver.findMinPath();
        System.out.println(distances);
        System.out.println(minPath);
    }
}
