package com.home.delivery.app.paths.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

/**
 * Created by evgeny on 15.05.15.
 */
@JsonIgnoreProperties(value = "true")
public class Route {

    @JsonProperty("legs")
    public Leg[] legs;

    @JsonProperty("waypoint_order")
    public int[] order;

    @Override
    public String toString() {
        return "Route{" +
                "legs=" + Arrays.toString(legs) +
                '}';
    }
}
