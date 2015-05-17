package com.home.delivery.app.paths;

import java.util.Objects;

/**
 * Created by evgeny on 16.05.15.
 */
public final class RouteElement {
    private final String startAddress;
    private final String endAddress;

    public RouteElement(String startAddress, String endAddress) {
        this.startAddress = startAddress;
        this.endAddress = endAddress;
    }

    @Override
    public String toString() {
        return "RouteElement{" +
                "startAddress='" + startAddress + '\'' +
                ", endAddress='" + endAddress + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteElement that = (RouteElement) o;
        return Objects.equals(startAddress, that.startAddress) &&
                Objects.equals(endAddress, that.endAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startAddress, endAddress);
    }
}
