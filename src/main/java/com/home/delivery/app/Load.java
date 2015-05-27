package com.home.delivery.app;

import com.home.delivery.app.paths.tsp.Tour;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by evgeny on 08.05.15.
 */
public final class Load {
    private volatile Tour<String> tour;
    private final DeliveryShift shift;
    private final LocalDate date;

    public Tour<String> getTour() {
        return tour;
    }

    public void setTour(Tour<String> tour) {
        this.tour = tour;
    }

    public DeliveryShift getShift() {
        return shift;
    }


    public LocalDate getDate() {
        return date;
    }

    public Load(LocalDate date, DeliveryShift shift) {
        this.date = date;
        this.shift = shift;
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
