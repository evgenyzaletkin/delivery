package com.home.delivery.app;

import com.google.common.collect.ImmutableList;

import javax.annotation.concurrent.ThreadSafe;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by evgeny on 08.05.15.
 */
@ThreadSafe
public final class Load {
    private final List<Delivery> deliveries;
    private final DeliveryShift shift;
    private final LocalDate date;

    public Load(List<Delivery> deliveries, DeliveryShift shift, LocalDate date) {
        this.date = date;
        this.deliveries = ImmutableList.copyOf(deliveries);
        this.shift = shift;
    }

    public List<Delivery> getDeliveries() {
        return deliveries;
    }

    public DeliveryShift getShift() {
        return shift;
    }
}
