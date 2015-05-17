package com.home.delivery.app.paths.dummy;

import com.home.delivery.app.Delivery;
import com.home.delivery.app.paths.GoogleMapsService;
import com.home.delivery.app.paths.StatefulDistancesProvider;
import org.junit.Test;

import javax.inject.Provider;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by evgeny on 16.05.15.
 */
public class BruteForcePathMakerTest {

    @Test
    public void justTest() throws Exception {
        GoogleMapsService service = new GoogleMapsService();
        Provider<StatefulDistancesProvider> provider = new Provider<StatefulDistancesProvider>() {
            @Override
            public StatefulDistancesProvider get() {
                return new StatefulDistancesProvider(service);
            }
        };
        BruteForcePathMaker bruteForcePathMaker = new BruteForcePathMaker(provider);

        String rawText = "1976 CULPEPPER LANE\tFAYETTEVILLE\tNC\t28304\n" +
                "3405 CASTLEFIELD LANE\tFAYETTEVILLE\tNC\t28306\n" +
                "1805 1 SARDONYX ROAD\tFAYETTEVILLE\tNC\t28303\n" +
                "912 TAMARACK DR APT 11115\tFAYETTEVILLE\tNC\t28311\n" +
                "912 TAMARACK DR APT 11115\tFAYETTEVILLE\tNC\t28311\n" +
                "799 MAGELLAN DRIVE\tFAYETTEVILLE\tNC\t28311\n" +
                "3429 GABLES DRIVE\tFAYETTEVILLE\tNC\t28311\n" +
                "3000 COOLEE CIRCLE\tFAYETTEVILLE\tNC\t28311\n" +
                "1040 WILD PINE DRIVE\tFAYETTEVILLE\tNC\t28312\n" +
                "2730 CREEK MEADOW PL, APT 142\tFAYETTEVILLE\tNC\t28304\n" +
                "182 LOCHWOOD DRIVE\tRAEFORD\tNC\t28376\n" +
                "107 DEERLODGE COURT\tRAEFORD\tNC\t28376\n";

        List<Delivery> deliveries = Arrays.stream(rawText.split("\n")).map(s -> {
            String[] split = s.split("\t");
            Delivery d = new Delivery();
            d.setStreet(split[0]);
            d.setCity(split[1]);
            d.setState(split[2]);
            d.setZip(Integer.valueOf(split[3]));
            return d;
        }).collect(Collectors.toList());

        List<Delivery> min = bruteForcePathMaker.makeTheShortestPath(deliveries);
        System.out.println(min.stream().map(bruteForcePathMaker::mapToAddress).collect(Collectors.joining("\n")));
    }


}