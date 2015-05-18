package com.home.delivery.app.paths.dummy;

import com.home.delivery.app.Delivery;
import com.home.delivery.app.paths.GoogleMapsService;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by evgeny on 16.05.15.
 */
public class BruteForceRoutingServiceTest
{

    @Test
    public void justTest() throws Exception {
        String rawText = "8347 JUDY DRIVE\tFAYETTEVILLE\tNC\t28314\n" +
                "1579 ROSSMERE DRIVE\tFAYETTEVILLE\tNC\t28314\n" +
                "182 LOCHWOOD DRIVE\tRAEFORD\tNC\t28376\n" +
                "107 DEERLODGE COURT\tRAEFORD\tNC\t28376\n" +
                "1976 CULPEPPER LANE\tFAYETTEVILLE\tNC\t28304\n" +
                "3405 CASTLEFIELD LANE\tFAYETTEVILLE\tNC\t28306\n" +
                "1805 1 SARDONYX ROAD\tFAYETTEVILLE\tNC\t28303\n" +
                "912 TAMARACK DR APT 11115\tFAYETTEVILLE\tNC\t28311\n" +
                "912 TAMARACK DR APT 11115\tFAYETTEVILLE\tNC\t28311\n" +
                "799 MAGELLAN DRIVE\tFAYETTEVILLE\tNC\t28311\n" +
                "3429 GABLES DRIVE\tFAYETTEVILLE\tNC\t28311\n" +
                "3000 COOLEE CIRCLE\tFAYETTEVILLE\tNC\t28311\n" +
                "1040 WILD PINE DRIVE\tFAYETTEVILLE\tNC\t28312\n" +
                "2730 CREEK MEADOW PL, APT 142\tFAYETTEVILLE\tNC\t28304\n" +
                "6319 RAEFORD ROAD, APT 46\tFAYETTEVILLE\tNC\t28304\n" +
                "3551 DORADO CIRCLE APT 106\tFAYETTEVILLE\tNC\t28304\n" +
                "1057 GLEN REILLY DRIVE\tFAYETTEVILLE\tNC\t28314\n";
        List<Delivery> deliveries = Arrays.stream(rawText.split("\n")).map(s -> {
            String[] split = s.split("\t");
            Delivery d = new Delivery();
            d.setStreet(split[0]);
            d.setCity(split[1]);
            d.setState(split[2]);
            d.setZip(Integer.valueOf(split[3]));
            return d;
        }).collect(Collectors.toList());

        GoogleMapsService googleMapsService = new GoogleMapsService();
        BruteForceRoutingService routingService = new BruteForceRoutingService(googleMapsService);
        List<Delivery> deliveries1 = routingService.buildRoute(deliveries);
        System.out.println(deliveries1);
    }


}