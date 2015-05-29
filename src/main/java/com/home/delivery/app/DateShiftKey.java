package com.home.delivery.app;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by evgeny on 29.05.15.
 */
public class DateShiftKey {
    public final LocalDate date;
    public final DeliveryShift shift;

    public DateShiftKey(LocalDate date, DeliveryShift shift) {
        this.date = date;
        this.shift = shift;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateShiftKey dateShiftKey = (DateShiftKey) o;
        return Objects.equals(date, dateShiftKey.date) &&
                Objects.equals(shift, dateShiftKey.shift);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, shift);
    }
}
