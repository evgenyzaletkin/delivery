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

    private static final Predicate<Delivery> CORRUPTED =
            d -> Utils.NO_ORDER_NUMBER.equals(d.getOrderNumber()) ||
                    d.getDestinationName() == null ||
                    d.getDeliveryDate() == null ||
                    d.getDestinationCity() == null ||
                    d.getDestinationState() == null ||
                    d.getDestinationStreet() == null ||
                    d.getDestinationZip() == null ||
                    d.getQuantity() == 0 ||
                    d.getVolume() == 0.0;


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
//                map(e -> new HashMap.SimpleEntry<>(e.getKey(), e.getValue().stream().mapToDouble(Delivery::getVolume).sum())).
//                collect(Collectors.toList()).toString());
        deliveries.forEach(d -> {
            if (Utils.LARKIN_NAME.equals(d.getDestinationName())) d.setIsReturn(true);
            this.deliveries.put(d.getId(), d);
        });
        deliveries.stream().filter(CORRUPTED).forEach(d -> d.setIsValid(false));

    }

    public void removeDelivery(Delivery delivery) {
        this.deliveries.remove(delivery.getId());
    }

    public void reset() {
        deliveries.clear();
    }

}
