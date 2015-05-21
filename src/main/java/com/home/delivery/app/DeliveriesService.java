package com.home.delivery.app;

import javax.inject.Named;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;

/**
 * Created by evgeny on 08.05.15.
 */
@Named("deliveriesService")
public class DeliveriesService {

    private final ConcurrentMap<String, Delivery> deliveries = new ConcurrentHashMap<>();

    public static final Predicate<Delivery> CORRUPTED =
            d -> d.getClientName() != null &&
                    d.getOrderNumber() != null &&
                    d.getDeliveryDate() != null &&
                    d.getCity() != null &&
                    d.getState() != null &&
                    d.getStreet() != null &&
                    d.getZip() != null &&
                    d.getVolumeNumber() != null;

    public Collection<Delivery> getAllDeliveries() {
        return deliveries.values();
    }

    public Optional<Delivery> getDelivery(String deliveryId) {
        return Optional.ofNullable(deliveries.get(deliveryId));
    }

    public void addDeliveries(Collection<Delivery> deliveries) {
        deliveries.forEach(d -> this.deliveries.put(d.getOrderNumber(), d));
    }

    public void removeDelivery(Delivery delivery) {
        this.deliveries.remove(delivery.getOrderNumber());
    }


}
