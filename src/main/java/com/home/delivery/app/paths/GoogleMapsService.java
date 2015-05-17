package com.home.delivery.app.paths;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.client.RestTemplate;

import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

@Named
public class GoogleMapsService {

    static final String URL = "https://maps.googleapis.com/maps/api/distancematrix/json?" +
            "origins={origins}&" +
            "destinations={destinations}&" +
            "sensor=false";
    static final Log log = LogFactory.getLog(GoogleMapsService.class);
    static final int SIZE_LIMIT = 100;
    final RestTemplate restTemplate = new RestTemplate();
    private long lastAccess = 0L;
    private static final Long AWAIT_TIME = 10000L;



    /**
     * This method takes two lists of places containing origins and destinations and returns distances for each pair.
     * @param origins list of start points
     * @param destinations list of end points
     * @return Map that contains {@code origin + destination} as the key and distance between them as the value.
     */
    public Map<RouteElement, Integer> getDistances(List<String> origins, List<String> destinations) {
        List<Row> rows = getDistanceRows(origins, destinations);
        checkArgument(rows.size() == origins.size(),
                "The number of rows %s from  server differs from number of origins %s",
                rows.size(),
                origins.size());
        Map<RouteElement, Integer> result = new HashMap<>(origins.size() * destinations.size());
        int rowIndex = 0;
        for (String origin : origins) {
            Element[] elements = rows.get(rowIndex).elements;
            int elementIndex = 0;
            for (String destination : destinations) {
                RouteElement key = new RouteElement(origin, destination);
                Integer value = elements[elementIndex].distance.value;
                log.info(String.format("Putting key %s, value %s into result", key, value));
                result.put(key, value);
                elementIndex++;
            }
            rowIndex++;
        }
        return result;
    }

    synchronized List<Row> getDistanceRows(List<String> origins, List<String> destinations) {
        checkArgument(destinations.size() <= SIZE_LIMIT, "Limitation of destinations is exceeded");
        int originsSize = SIZE_LIMIT / origins.size();
        List<Row> resultedRows = new ArrayList<>(origins.size() * destinations.size());
        String destinationString = convertToString(destinations);
        for (int i = 0; i < origins.size(); i+=originsSize) {
            int toIndex = i + originsSize > origins.size() - 1 ? origins.size() : i + originsSize;
            String originString = convertToString(origins.subList(i, toIndex));
            DistancesInfo info = getDistanceInfo(originString, destinationString);
            performAwaitCheck();
            System.out.println(info.status);
            checkState(info.status.contains("OK"), "Response from server is not OK: %s ", info.status);
            resultedRows.addAll(Arrays.asList(info.rows));
        }
        return resultedRows;
    }

    private void performAwaitCheck() {
        long delay = System.currentTimeMillis() - lastAccess;
        lastAccess = System.currentTimeMillis();
        if (delay < AWAIT_TIME) {
            try {
                Thread.sleep(AWAIT_TIME - delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    DistancesInfo getDistanceInfo (String origins, String destinations) {
        Map<String, String> parameters = ImmutableMap.of("origins", origins, "destinations", destinations);
        return restTemplate.getForObject(URL, DistancesInfo.class, parameters);
    }

    String convertToString(Collection<String> elements) {
        return elements.stream().collect(Collectors.joining("|")).replaceAll(" ", "+");
    }


    static class DistancesInfo {
        @JsonProperty("origin_addresses")
        String[] originAddresses;
        @JsonProperty("destination_addresses")
        String[] destinationAddresses;
        @JsonProperty("rows")
        Row[] rows;
        @JsonProperty("status")
        String status;
    }

    static class Row {
        @JsonProperty("elements")
        Element[] elements;
    }

    static class Element {
        @JsonProperty("distance")
        Distance distance;
        @JsonProperty("duration")
        Duration duration;
    }

    static class Distance {
        @JsonProperty("text")
        String text;
        @JsonProperty("value")
        Integer value;
    }

    static class Duration {
        @JsonProperty("text")
        String text;
        @JsonProperty("value")
        Integer value;
    }
}
