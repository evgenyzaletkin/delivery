package com.home.delivery.app;

import com.home.delivery.app.paths.tsp.Tour;

import javax.annotation.concurrent.ThreadSafe;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Created by evgeny on 08.05.15.
 */
public final class Load {
    private Tour<String> tour;
    private DeliveryShift shift;
    private LocalDate date;
    private List<Delivery> deliveries = Collections.emptyList();
    private boolean isRouted = false;

    public Load() {

    }



    public Tour<String> getTour() {
        return tour;
    }

    public void setTour(Tour<String> tour) {
        this.tour = tour;
    }

    public DeliveryShift getShift() {
        return shift;
    }

    public void setShift(DeliveryShift shift) {
        this.shift = shift;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Delivery> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(List<Delivery> deliveries) {
        this.deliveries = deliveries;
    }

    public boolean isRouted() {
        return isRouted;
    }

    public void setIsRouted(boolean isRouted) {
        this.isRouted = isRouted;
    }

    @Override
    public String toString() {
        return "Load{" +
                "tour=" + tour +
                ", shift=" + shift +
                ", date=" + date +
                ", deliveries=" + deliveries +
                ", isRouted=" + isRouted +
                '}';
    }
}
