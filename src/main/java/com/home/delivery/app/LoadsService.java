package com.home.delivery.app;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.home.delivery.app.paths.RoutingService;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Named("loadsService")
public class LoadsService {

    private final ExecutorService executorService;
    private final RoutingService routingService;
    private final DeliveriesService deliveriesService;

    private final ConcurrentMap<DateShiftKey, Load> loads = Maps.newConcurrentMap();



    @Inject
    public LoadsService(ExecutorService executorService,
                        RoutingService routingService,
                        DeliveriesService deliveriesService) {
        this.executorService = executorService;
        this.routingService = routingService;
        this.deliveriesService = deliveriesService;
    }


    public Load getLoad(LocalDate date, DeliveryShift shift) {
        DateShiftKey key = new DateShiftKey(date, shift);
        return loads.get(key);
    }


    public synchronized Load createNewLoad(LocalDate date, DeliveryShift shift) {
        List<DateShiftKey> keys = EnumSet.allOf(DeliveryShift.class).stream().
                map(s -> new DateShiftKey(date, s)).collect(Collectors.toList());

        ////        Waypoints with predefined shifts should be put into the loads before any optimizations
        keys.forEach(this::gatherLoadByShifts);
        ////        try to put all deliveries with one zip in one loadKey
        keys.forEach(k -> gatherByFunction(k, w -> w.getParts().get(0).getDelivery().getZip()));
//        try to put other waypoints
        keys.forEach(k -> gatherByFunction(k, w -> w));

        keys.forEach(k -> loads.put(k, new Load(k.date, k.shift, gatherToWaypoints(getDeliveryPartsForKey(k)))));

        return loads.get(new DateShiftKey(date, shift));
    }

    void gatherLoadByShifts(DateShiftKey key) {
        List<Waypoint> availableWaypoints = getAvailableWaypoints(key.date);
        List<Waypoint> necessaryWaypoints = availableWaypoints.stream().
                filter(w -> w.getParts().
                        stream().
                        anyMatch(p -> p.getDelivery().getDeliveryShift() == key.shift)).
                collect(Collectors.toList());
        necessaryWaypoints.forEach(w -> w.parts.forEach(p -> createAndPutFromFreePart(p, key)));
    }

    public List<Waypoint> getAvailableWaypoints(LocalDate date) {
        return gatherToWaypoints(getAvailablePartsForDay(date));
    }

    public List<Waypoint> gatherToWaypoints(List<DeliveryPart> parts) {
        return parts.
                stream().
                collect(Collectors.groupingBy(p -> Utils.mapToAddress(p.getDelivery()))).
                values().
                stream().
                map(Waypoint::new).
                collect(Collectors.toList());
    }

    public List<DeliveryPart> getAvailablePartsForDay(LocalDate date) {
        List<Delivery> deliveriesForDay = deliveriesService.getValidDeliveries().
                stream().
                filter(d -> date.isEqual(d.getDeliveryDate())).
                collect(Collectors.toList());
        Map<Delivery, List<DeliveryPart>> partsByDeliveries = deliveryParts.values().
                stream().filter(p -> date.isEqual(p.getDelivery().getDeliveryDate())).
                collect(Collectors.groupingBy(DeliveryPart::getDelivery));
        List<DeliveryPart> availableParts = new ArrayList<>(deliveriesForDay.size());

        for (Delivery delivery : deliveriesForDay) {

            int usedItems = partsByDeliveries.getOrDefault(delivery, Collections.emptyList()).
                    stream().mapToInt(DeliveryPart::getItems).sum();
            Preconditions.checkState(usedItems <= delivery.getQuantity());
            if (usedItems < delivery.getQuantity()) {
                availableParts.add(new DeliveryPart(delivery,  delivery.getQuantity() - usedItems, null));
            }
        }
        return availableParts;
    }

    void gatherByFunction(DateShiftKey key,  Function<Waypoint, ?> f) {

        List<DeliveryPart> currentParts = getDeliveryPartsForKey(key);
        double availableDownloadVolume = Utils.MAX_VOLUME - currentParts.stream().mapToDouble(DeliveryPart::getDownloadVolume).sum();
        double availableUploadVolume = Utils.MAX_VOLUME - currentParts.stream().mapToDouble(DeliveryPart::getUploadVolume).sum();
        if (availableDownloadVolume > 0.0 || availableUploadVolume > 0.0) {
            List<Waypoint> availableWaypoints = getAvailableWaypoints(key.date);
            Map<Object, List<Waypoint>> groupedByF = availableWaypoints.
                    stream().
                    collect(Collectors.groupingBy(f));
            for (Map.Entry<Object, List<Waypoint>> entry : groupedByF.entrySet()) {
                List<Waypoint> waypoints = entry.getValue();
                double downloadVolume = waypoints.stream().mapToDouble(Waypoint::getDownloadVolume).sum();
                double uploadVolume = waypoints.stream().mapToDouble(Waypoint::getUploadVolume).sum();
                if (availableDownloadVolume >= downloadVolume && availableUploadVolume >= uploadVolume) {
                    availableDownloadVolume -= downloadVolume;
                    availableUploadVolume -= uploadVolume;
                    waypoints.stream().flatMap(w -> w.getParts().stream()).forEach(p -> createAndPutFromFreePart(p, key));
                }
            }
        }
    }


    public List<DeliveryPart> getDeliveryPartsForKey(DateShiftKey key) {
        return deliveryParts.values().stream().filter(d -> key.equals(d.getDateShiftKey())).collect(Collectors.toList());
    }

    public DeliveryPart createNewPart(String deliveryId, DateShiftKey key, Integer items) {
        Delivery delivery = deliveriesService.getDelivery(deliveryId).get();
        int usedItems = items +  deliveryParts.values().
                stream().
                filter(p -> delivery.equals(p.getDelivery())).
                mapToInt(DeliveryPart::getItems).
                sum();
        Preconditions.checkState(usedItems <= delivery.getQuantity(), "Delivery contains only %s, but required %s",
                delivery.getQuantity(), usedItems);
       return new DeliveryPart(delivery, items, key);
    }


    public void updateLoad(DateShiftKey key, List<String> deliveriesIds, List<Integer> items) {

        checkArgument(deliveriesIds.size() == items.size());

        clearOldParts(key, deliveriesIds);
        Iterator<Integer> itemsIterator = items.iterator();
        List<DeliveryPart> parts = new ArrayList<>(deliveriesIds.size());
        for (String deliveriesId : deliveriesIds) {
            Delivery d = deliveriesService.getDelivery(deliveriesId).get();
            DeliveryPart newPart = createNewPart(d.getId(), key, itemsIterator.next());
            parts.add(newPart);
            deliveryParts.put(new PartKey(d.getId(), key), newPart);
        }
        Load load = loads.get(key);
        load.setWaypoints(gatherToWaypoints(parts));
        executorService.submit(() -> {
            routingService.buildRoute(load);
        });
    }

    private void clearOldParts(DateShiftKey key, List<String> deliveriesIds) {
        deliveriesIds.forEach(id -> deliveryParts.remove(createPartKey(id, key)));
    }

    void createAndPutFromFreePart(DeliveryPart part, DateShiftKey key) {
        createAndPutNewPart(part.getDelivery(), key, part.getItems());
    }

    void createAndPutNewPart(Delivery delivery, DateShiftKey key, int items) {
        PartKey partKey = new PartKey(delivery.getId(), key);
        deliveryParts.put(partKey, new DeliveryPart(delivery, items, key));
    }

    private class PartKey {
        final String deliveryNumber;
        final DateShiftKey dateShiftKey;

        private PartKey(String deliveryNumber, DateShiftKey dateShiftKey) {
            this.deliveryNumber = deliveryNumber;
            this.dateShiftKey = dateShiftKey;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PartKey partKey = (PartKey) o;
            return Objects.equals(deliveryNumber, partKey.deliveryNumber) &&
                    Objects.equals(dateShiftKey, partKey.dateShiftKey);
        }

        @Override
        public int hashCode() {
            return Objects.hash(deliveryNumber, dateShiftKey);
        }
    }

    ConcurrentMap<PartKey, DeliveryPart> deliveryParts = new ConcurrentHashMap<>();

    private PartKey createPartKey(String deliveryId, DateShiftKey key) {
        return new PartKey(deliveryId, key);
    }


    public void reset() {
        deliveryParts.clear();
        loads.clear();
    }
}
