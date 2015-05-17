package com.home.delivery.app.paths.google;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by evgeny on 15.05.15.
 */
public class Leg {
    @JsonProperty("start_address")
    public String startAddress;
    @JsonProperty("end_address")
    public String endAddress;

    @Override
    public String toString() {
        return "Leg{" +
                "startAddress='" + startAddress + '\'' +
                ", endAddress='" + endAddress + '\'' +
                '}';
    }
}
