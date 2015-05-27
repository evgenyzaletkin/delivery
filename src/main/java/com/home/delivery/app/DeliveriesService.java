package com.home.delivery.app;

import javax.inject.Named;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by evgeny on 08.05.15.
 */
@Named("deliveriesService")
public class DeliveriesService {

    private final ConcurrentMap<String, Delivery> deliveries = new ConcurrentHashMap<>();

    public static final Predicate<Delivery> CORRUPTED =
            d -> d.getClientName() == null ||
                    d.getDeliveryDate() == null ||
                    d.getCity() == null ||
                    d.getState() == null ||
                    d.getStreet() == null ||
                    d.getZip() == null ||
                    d.getVolumeNumber() == null;

    public static final Predicate<Delivery> VALID = CORRUPTED.negate();

    public Collection<Delivery> getValidDeliveries() {
        return deliveries.values().stream().filter(VALID).collect(Collectors.toList());
    }

    public Collection<Delivery> getAllDeliveries() {return deliveries.values();}

    public Optional<Delivery> getDelivery(String deliveryId) {
        return Optional.ofNullable(deliveries.get(deliveryId));
    }

    public void addDeliveries(Collection<Delivery> deliveries) {
//        throw new RuntimeException(deliveries.
//                stream().
//                collect(Collectors.groupingBy(Delivery::getDeliveryDate)).
//                entrySet().
//                stream().
//                map(e -> new HashMap.SimpleEntry<>(e.getKey(), e.getValue().stream().mapToDouble(Delivery::getVolumeNumber).sum())).
//                collect(Collectors.toList()).toString());
        deliveries.forEach(d -> this.deliveries.put(d.getOrderNumber(), d));
    }

    public void removeDelivery(Delivery delivery) {
        this.deliveries.remove(delivery.getOrderNumber());
    }

    public void reset() {
        deliveries.clear();
    }

}
