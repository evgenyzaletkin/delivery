package com.home.delivery.app;

import com.google.common.collect.ImmutableList;
import com.home.delivery.app.paths.tsp.Tour;

import javax.annotation.concurrent.ThreadSafe;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by evgeny on 08.05.15.
 */
@ThreadSafe
public final class Load {
    private final Tour<String> tour;
    private final DeliveryShift shift;
    private final LocalDate date;
    private final List<Delivery> deliveries;
    private volatile boolean isRouted = false;

    public Load(List<Delivery> deliveries,
                Tour<String> tour,
                DeliveryShift shift,
                LocalDate date) {
        this.tour = tour;
        this.date = date;
        this.shift = shift;
        this.deliveries = ImmutableList.copyOf(deliveries);
    }

    public Tour<String> getTour() {
        return tour;
    }

    public LocalDate getDate() {
        return date;
    }

    public DeliveryShift getShift() {
        return shift;
    }

    public List<Delivery> getDeliveries() {
        return deliveries;
    }

    public boolean isRouted() {
        return isRouted;
    }
}
