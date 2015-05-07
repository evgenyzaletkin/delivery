package com.home.delivery.app;

import com.google.common.collect.ImmutableList;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

/**
 * Created by evgeny on 08.05.15.
 */
@ThreadSafe
public final class Load {
    private final List<Delivery> deliveries;
    private final DeliveryShift shift;

    public Load(List<Delivery> deliveries, DeliveryShift shift) {
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
