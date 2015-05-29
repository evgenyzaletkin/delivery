package com.home.delivery.app;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Created by evgeny on 08.05.15.
 */
public final class Load {
    private final DeliveryShift shift;
    private final LocalDate date;
    private volatile List<Waypoint> waypoints;

    public DeliveryShift getShift() {
        return shift;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setWaypoints(List<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    public Load(LocalDate date, DeliveryShift shift, List<Waypoint> waypoints) {
        this.date = date;
        this.shift = shift;
        this.waypoints = waypoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Load load = (Load) o;
        return Objects.equals(shift, load.shift) &&
                Objects.equals(date, load.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shift, date);
    }
}
