package com.home.delivery.app;

import javax.inject.Named;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by evgeny on 08.05.15.
 */
@Named("deliveriesService")
public class DeliveriesService {

    volatile Set<Delivery> deliveries = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public Set<Delivery> getAllDeliveries() {
        return deliveries;
    }

    public Optional<Delivery> getDelivery(String deliveryId) {
        return deliveries.stream().filter(d -> d.getOrderNumber().equals(deliveryId)).findAny();
    }

    public void addDeliveries(Collection<Delivery> deliveries) {
        this.deliveries.addAll(deliveries);
    }

    public void removeDelivery(Delivery delivery) {
        this.deliveries.remove(delivery);
    }

    public List<Delivery> getDeliveriesByDate(LocalDate date) {
        return deliveries.stream().filter(d -> date.isEqual(d.getDeliveryDate())).collect(Collectors.toList());
    }

}
