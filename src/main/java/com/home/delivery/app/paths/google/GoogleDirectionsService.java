package com.home.delivery.app.paths.google;

import com.google.common.collect.ImmutableMap;
import com.home.delivery.app.Delivery;
import com.home.delivery.app.paths.PathMaker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by evgeny on 14.05.15.
 */
@Named
public class GoogleDirectionsService implements PathMaker {

    private static final String URL = "http://maps.googleapis.com/maps/api/directions/json?" +
            "origin={origin}&" +
            "waypoints=optimize::true|{waypoints}&" +
            "destination={destination}";

    static final String ORIGIN_CITY = "RALEIGH";
    static final String ORIGIN_STATE = "NC";
    static final int ORIGIN_ZIP = 27603;
    final RestTemplate restTemplate = new RestTemplate();



    @Override
    public List<Delivery> makeTheShortestPath(List<Delivery> deliveries) {
        String origin = buildAddressString(ORIGIN_CITY, ORIGIN_STATE, "");
        String waypoints = buildWaypoints(deliveries);
        List<Leg> legs = getLegs(waypoints, origin, origin);
        return matchDeliveriesAndLegs(new ArrayList<>(deliveries), legs);
    }

    private final class OrderedLeg {
        int order;
        String city;
        String street;
        String state;
        Integer zip;
    }

    private List<Delivery> matchDeliveriesAndLegs(List<Delivery> deliveries, List<Leg> legs) {
        List<Delivery> sorted = new ArrayList<>(deliveries.size());
        int order = 0;
        List<OrderedLeg> orderedLegs = new ArrayList<>();
        for (Leg leg : legs) {
            order++;
            String[] split = leg.startAddress.split(",");
            //Assume that street is absent if length is 3
            String street;
            String city;
            String state;
            Integer zip;
            if (split.length == 3) {
                street = null;
                city = split[0].replaceAll(" ", "");
                String[] stateAndIndex = split[1].split(" ");
                state = stateAndIndex[1];
                zip = stateAndIndex.length == 2 ? 0 : Integer.valueOf(stateAndIndex[2]);
                if (city.equalsIgnoreCase(ORIGIN_CITY) && state.equalsIgnoreCase(ORIGIN_STATE)) continue;
                else {
                    OrderedLeg orderedLeg = new OrderedLeg();
                    orderedLeg.order = order;
                    orderedLeg.city = city;
                    orderedLeg.zip = zip;
                    orderedLeg.state = state;
                    orderedLegs.add(orderedLeg);
                }
            } else {
                street = split[0];
                city = split[1].replaceAll(" ", "");
                String[] stateAndIndex = split[2].split(" ");
                state = stateAndIndex[1];
                zip = Integer.valueOf(stateAndIndex[2]);
            }
            //Assume that states and cities have the same names
            Optional<Delivery> foundDelivery = deliveries.stream().
                    filter(d ->
                            d.getCity().equalsIgnoreCase(city) &&
                            d.getState().equalsIgnoreCase(state) &&
                            d.getZip().equals(zip)).
                    collect(Collectors.maxBy((d1, d2) -> {
                        double comp1 = compareTwoStrings(street, d1.getStreet());
                        double comp2 = compareTwoStrings(street, d2.getStreet());
                        return Double.compare(comp1, comp2);
                    }));
            if (foundDelivery.isPresent()) {
                Delivery d = foundDelivery.get();
                deliveries.remove(d);
                sorted.add(d);
            }
        }
        processForbiddenDeliveries(sorted, deliveries, orderedLegs);
        return sorted;
    }

    private void processForbiddenDeliveries(List<Delivery> sorted,
                                            List<Delivery> forbidden,
                                            List<OrderedLeg> orderedLegs) {
        for (Delivery d : forbidden) {
            Optional<OrderedLeg> opt = orderedLegs.stream().filter(l -> l.city.equalsIgnoreCase(d.getCity()) &&
                    l.state.equalsIgnoreCase(d.getState()) &&
                    (l.zip == 0 || l.zip.equals(d.getZip()))).findFirst();
            if (opt.isPresent()) sorted.add(opt.get().zip, d);
            else sorted.add(d);
        }
    }



    double compareTwoStrings(String s1, String s2) {
        Map<Character, Integer> s1Chars = calculateChars(s1);
        Map<Character, Integer> s2Chars = calculateChars(s2);
        int sum = 0;
        for (Map.Entry<Character, Integer> entry : s1Chars.entrySet()) {
            Integer count1 = entry.getValue();
            sum += Math.min(count1, s2Chars.getOrDefault(entry.getKey(), 0));
        }
        return (double) sum / (double) s1.length();
    }

    Map<Character, Integer> calculateChars(String str) {
        Map<Character, Integer> charsCounts = new HashMap<>();
        for (char c : str.replaceAll(" ", "").toCharArray()) {
            Integer count = charsCounts.getOrDefault(c, 0);
            charsCounts.put(c, ++count);
        }
        return charsCounts;
    }


    List<Leg> getLegs(String waypoints, String origin, String destination) {
        ResponseEntity<RoutesInfo> response = restTemplate.getForEntity(URL, RoutesInfo.class,
                ImmutableMap.of("origin", origin, "waypoints", waypoints, "destination", destination));
        Route route = response.getBody().routes[0];
        ArrayList<Leg> sortedByWaypoints = new ArrayList<>(route.legs.length);
        for (int i : route.order) {
            sortedByWaypoints.add(route.legs[i]);
        }
        return sortedByWaypoints;
    }

    String buildAddressString(String city, String state, String street) {
        return (street + "+" + city + "+" + state).replaceAll(" ", "+");
    }

    String buildWaypoints(List<Delivery> deliveries) {
        return deliveries.stream().
                map(d -> buildAddressString(d.getCity(), d.getState(), d.getStreet())).
                collect(Collectors.joining("|"));
    }


}
