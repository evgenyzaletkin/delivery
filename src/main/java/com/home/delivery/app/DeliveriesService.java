package com.home.delivery.app;

import com.home.delivery.app.paths.Utils;

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

    private static final Predicate<Delivery> CORRUPTED =
            d -> Utils.NO_ORDER_NUMBER.equals(d.getOrderNumber()) ||
                    d.getClientName() == null ||
                    d.getDeliveryDate() == null ||
                    d.getCity() == null ||
                    d.getState() == null ||
                    d.getStreet() == null ||
                    d.getZip() == null ||
                    d.getQuantity() == 0 ||
                    d.getVolumeNumber() == 0.0;


    public Collection<Delivery> getValidDeliveries() {
        return deliveries.
                values().
                stream().
                filter(Delivery::isValid).
                collect(Collectors.toList());
    }

//    public Collection<Delivery> getCorruptedDeliveries() {
//        return deliveries.
//                values().
//                stream().
//                filter(Delivery::isValid).collect(Collectors.toList());
//    }

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
        deliveries.stream().filter(CORRUPTED).forEach(d -> d.setIsValid(false));
        deliveries.stream().
                collect(Collectors.groupingBy(Delivery::getOrderNumber)).
                values().
                stream().
                filter(l -> l.size() != 1).
                flatMap(Collection::stream).
                forEach(d -> d.setIsValid(false));
        deliveries.forEach(d -> this.deliveries.put(d.getId(), d));
    }

    public void removeDelivery(Delivery delivery) {
        this.deliveries.remove(delivery.getId());
    }

    public void reset() {
        deliveries.clear();
    }

}
