package com.home.delivery.app;

import com.home.delivery.app.paths.tsp.Tour;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by evgeny on 08.05.15.
 */
public final class Load {
    private Tour<String> tour;
    private DeliveryShift shift;
    private LocalDate date;

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
