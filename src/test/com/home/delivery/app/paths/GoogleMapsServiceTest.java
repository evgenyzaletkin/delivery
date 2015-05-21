package com.home.delivery.app.paths;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by evgeny on 16.05.15.
 */
public class GoogleMapsServiceTest {
    GoogleMapsService googleMapsService = new GoogleMapsService();
    @Test
    public void getDistancesShouldReturnDistancesBetweenEachOriginAndDest() throws Exception {
        String origin1 = "RALEIGH NC";
        String origin2 = "RAEFORD NC";
        String dest1 = "MAXTON NC";
        String dest2 = "FAYETTEVILLE NC";
        String origins = googleMapsService.convertToString(Arrays.asList(origin1, origin2));
        String destinations = googleMapsService.convertToString(Arrays.asList(dest1, dest2));
        GoogleMapsService.DistancesInfo distancesInfo = googleMapsService.getDistanceInfo(origins, destinations);
        Assert.assertEquals(distancesInfo.rows.length, 2);
        for (GoogleMapsService.Row row : distancesInfo.rows) {
            Assert.assertEquals(row.elements.length, 2);
        }

        String actualOrigin1 = distancesInfo.originAddresses[0].split(",")[0];
        Assert.assertTrue("RALEIGH".equalsIgnoreCase(actualOrigin1));

        String actualOrigin2 = distancesInfo.originAddresses[1].split(",")[0];
        Assert.assertTrue("RAEFORD".equalsIgnoreCase(actualOrigin2));

        String actualDest1 = distancesInfo.destinationAddresses[0].split(",")[0];
        Assert.assertTrue("MAXTON".equalsIgnoreCase(actualDest1));

        String actualDest2 = distancesInfo.destinationAddresses[1].split(",")[0];
        Assert.assertTrue("FAYETTEVILLE".equalsIgnoreCase(actualDest2));
    }

    @Test
    public void resultedMapShouldContainAPairOfPlacesAsKeyAndDistanceAsValue() throws Exception {
        String origin1 = "RALEIGH NC";
        String origin2 = "RAEFORD NC";
        String dest1 = "MAXTON NC";
        String dest2 = "FAYETTEVILLE NC";
        List<String> origins = Arrays.asList(origin1, origin2);
        List<String> destinations = Arrays.asList(dest1, dest2);

        Map<RouteElement, Integer> distances = googleMapsService.getDistances(origins, destinations);
        Assert.assertEquals(4, distances.size());
        Assert.assertTrue(distances.containsKey(new RouteElement("RALEIGH NC", "MAXTON NC")));
        Assert.assertTrue(distances.containsKey(new RouteElement("RALEIGH NC", "FAYETTEVILLE NC")));
        Assert.assertTrue(distances.containsKey(new RouteElement("RAEFORD NC", "MAXTON NC")));
        Assert.assertTrue(distances.containsKey(new RouteElement("RAEFORD NC", "FAYETTEVILLE NC")));
    }

    @Ignore
    @Test
    public void bigRequest() throws Exception {
        String rawText = "105 SILVER BERRY CT\tRAEFORD\tNC\t28376\n" +
                "2315 QWIGLEY COURT\tRAEFORD\tNC\t28376\n" +
                "120 QUAIL RD\tRAEFORD\tNC\t28376\n" +
                "6041 GOLDEN RAIN DRIVE\tFAYETTEVILLE\tNC\t28314\n" +
                "6016 CROWN RIDGE COURT\tFAYETTEVILLE\tNC\t28314\n" +
                "9032 GROUSE RUN LANE\tFAYETTEVILLE\tNC\t28314\n" +
                "8347 JUDY DRIVE\tFAYETTEVILLE\tNC\t28314\n" +
                "1579 ROSSMERE DRIVE\tFAYETTEVILLE\tNC\t28314\n" +
                "182 LOCHWOOD DRIVE\tRAEFORD\tNC\t28376\n" +
                "107 DEERLODGE COURT\tRAEFORD\tNC\t28376\n" +
                "1976 CULPEPPER LANE\tFAYETTEVILLE\tNC\t28304\n" +
                "3405 CASTLEFIELD LANE\tFAYETTEVILLE\tNC\t28306\n" +
                "1805 1 SARDONYX ROAD\tFAYETTEVILLE\tNC\t28303\n" +
                "912 TAMARACK DR APT 11115\tFAYETTEVILLE\tNC\t28311\n" +
                "799 MAGELLAN DRIVE\tFAYETTEVILLE\tNC\t28311\n";
        List<String> points = Arrays.stream(rawText.split("\n")).map(s -> s.replaceAll("\t", ",")).collect(Collectors.toList());
        Map<RouteElement, Integer> distances = googleMapsService.getDistances(points, points);
        Assert.assertEquals(distances.size(), 225);
    }
}