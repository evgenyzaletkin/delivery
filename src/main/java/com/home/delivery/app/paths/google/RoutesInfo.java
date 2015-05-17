package com.home.delivery.app.paths.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

/**
 * Created by evgeny on 15.05.15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoutesInfo {
    @JsonProperty("routes")
    Route[] routes;

    @Override
    public String toString() {
        return "Routes{" +
                "routeList=" + Arrays.toString(routes) +
                '}';
    }
}
