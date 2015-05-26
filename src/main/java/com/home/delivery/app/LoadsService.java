package com.home.delivery.app;

import com.google.common.collect.Maps;
import com.home.delivery.app.paths.RoutingService;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Named("loadsService")
public class LoadsService {

    private final ExecutorService executorService;
    private final RoutingService routingService;
    private final DeliveriesService deliveriesService;

    private final ConcurrentMap<LoadKey, Load> loads = Maps.newConcurrentMap();

    private static final double MAX_VOLUME = 1400.0;



    @Inject
    public LoadsService(ExecutorService executorService,
                        RoutingService routingService,
                        DeliveriesService deliveriesService) {
        this.executorService = executorService;
        this.routingService = routingService;
        this.deliveriesService = deliveriesService;
    }


    public Load getLoad(LocalDate date, DeliveryShift shift) {
        LoadKey key = new LoadKey(date, shift);
        return loads.get(key);
    }


    public Load createNewLoad(LocalDate date, DeliveryShift shift) {
        LoadKey key = new LoadKey(date, shift);
        return loads.computeIfAbsent(key, k -> {
            Load load = new Load();
            load.setDate(date);
            load.setShift(shift);
            gatherPartsForNewLoad(load);
            return load;
        });
    }


    private final static Comparator<DeliveryPart> PARTS_COMPARATOR = new Comparator<DeliveryPart>() {
        @Override
        public int compare(DeliveryPart p1, DeliveryPart p2) {
            float p1v = p1.getItems() * p1.getDelivery().getVolumeNumber() / p1.getDelivery().getQuantity();
            float p2v = p2.getItems() * p2.getDelivery().getVolumeNumber() / p2.getDelivery().getQuantity();
            return Float.compare(p1v, p2v);
        }
    };

    void gatherPartsForNewLoad(Load load) {
        List<DeliveryPart> availableDeliveryPartsForLoad = getAvailableDeliveryPartsForLoad(load);
        List<DeliveryPart> necessaryParts = availableDeliveryPartsForLoad.stream().
                filter(p -> load.getShift() == p.getDelivery().getDeliveryShift()).
                collect(Collectors.toList());
        for (DeliveryPart p : necessaryParts) {
            DeliveryPart newPart = new DeliveryPart(p.getDelivery(), p.getItems(), load);
            updateParts(newPart);
        }

        double currentVolume = necessaryParts.stream().
                mapToDouble(p -> p.getItems() * p.getDelivery().getVolumeNumber() / p.getDelivery().getQuantity()).
                sum();

        if (currentVolume < MAX_VOLUME) {
            List<DeliveryPart> possibleParts = availableDeliveryPartsForLoad.stream().
                    filter(p -> p.getDelivery().getDeliveryShift() == null).
                    sorted(PARTS_COMPARATOR).
                    collect(Collectors.toList());
            Collections.reverse(possibleParts);

            for (Iterator<DeliveryPart> partIterator = possibleParts.iterator(); partIterator.hasNext();) {
                DeliveryPart possiblePart = partIterator.next();
                double availableVolume = MAX_VOLUME - currentVolume;
                double partVolume = possiblePart.getItems() * possiblePart.getDelivery().getVolumeNumber()
                        / possiblePart.getDelivery().getQuantity();
                if (availableVolume > partVolume) {
                    currentVolume += partVolume;
                    DeliveryPart newPart = new DeliveryPart(possiblePart.getDelivery(), possiblePart.getItems(), load);
                    updateParts(newPart);
                    partIterator.remove();
                }
            }

            for (DeliveryPart possiblePart : possibleParts) {
                double availableVolume = MAX_VOLUME - currentVolume;
                double itemVolume = possiblePart.getDelivery().getVolumeNumber() / possiblePart.getDelivery().getQuantity();
                int items = (int) (availableVolume / itemVolume);
                if (items > 0) {
                    DeliveryPart part = new DeliveryPart(possiblePart.getDelivery(), items, load);
                    updateParts(part);
                }
            }

        }
    }

    public List<DeliveryPart> getDeliveryPartsForLoad(Load load) {
        return deliveryParts.values().stream().filter(d -> load.equals(d.getLoad())).collect(Collectors.toList());
    }

    public List<DeliveryPart> getAvailableDeliveryPartsForLoad(Load load) {
        return deliveriesService.getAllDeliveries().stream().
                filter(d -> load.getDate().isEqual(d.getDeliveryDate())).
                map(this::getFreeDelivery).
                filter(p -> p.getItems() != 0).
                collect(Collectors.toList());
    }


    public void updateLoad(Load load, List<String> deliveriesIds, List<Integer> items) {

        checkArgument(deliveriesIds.size() == items.size());
        clearOldParts(load, deliveriesIds);
        Iterator<Integer> itemsIterator = items.iterator();
        for (String deliveriesId : deliveriesIds) {
            Delivery d = deliveriesService.getDelivery(deliveriesId).get();
            updateParts(new DeliveryPart(d, itemsIterator.next(), load));
        }
        loads.put(new LoadKey(load.getDate(), load.getShift()), load);
        load.setIsRouted(false);
        executorService.submit(() -> {
            List<Delivery> allDeliveries = getDeliveryPartsForLoad(load).stream().
                    map(DeliveryPart::getDelivery).
                    distinct().
                    collect(Collectors.toList());
            load.setTour(routingService.buildRoute(allDeliveries));
            load.setIsRouted(true);
        });
    }

    private void clearOldParts(Load load, List<String> deliveriesIds) {
        getDeliveryPartsForLoad(load).stream().
                filter(p -> !deliveriesIds.contains(p.getDelivery().getOrderNumber())).
                forEach(p -> updateParts(new DeliveryPart(p.getDelivery(), 0, load)));
    }

    private class PartKey {
        final String deliveryNumber;
        final LoadKey loadKey;

        private PartKey(String deliveryNumber, LoadKey loadKey) {
            this.deliveryNumber = deliveryNumber;
            this.loadKey = loadKey;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PartKey partKey = (PartKey) o;
            return Objects.equals(deliveryNumber, partKey.deliveryNumber) &&
                    Objects.equals(loadKey, partKey.loadKey);
        }

        @Override
        public int hashCode() {
            return Objects.hash(deliveryNumber, loadKey);
        }
    }

    ConcurrentMap<PartKey, DeliveryPart> deliveryParts = new ConcurrentHashMap<>();


    void updateParts(DeliveryPart newPart) {
        checkArgument(newPart.getItems() <= newPart.getDelivery().getQuantity());
        PartKey currentKey = createPartKey(newPart.getDelivery(), newPart.getLoad());
        PartKey freeKey = createPartKey(newPart.getDelivery(), null);
        DeliveryPart currentPart = deliveryParts.get(currentKey);
        DeliveryPart freePart = deliveryParts.get(freeKey);
        int freeItems;
        if (currentPart == null) {
            checkArgument(freePart.getItems() >= newPart.getItems(),
                    "The number of available items %s is less than number of free items %s",
                    freePart.getItems(), newPart.getItems());
            freeItems = freePart.getItems() - newPart.getItems();
        } else {
            checkArgument(freePart.getItems() + currentPart.getItems() >= newPart.getItems());
            freeItems = freePart.getItems() + currentPart.getItems() - newPart.getItems();
        }
        DeliveryPart newFreePart = new DeliveryPart(freePart.getDelivery(), freeItems, null);
        deliveryParts.put(freeKey, newFreePart);
        if (newPart.getItems() == 0) deliveryParts.remove(currentKey);
        else deliveryParts.put(currentKey, newPart);
    }

    DeliveryPart getFreeDelivery(Delivery d) {
        return deliveryParts.computeIfAbsent(createPartKey(d, null), key -> new DeliveryPart(d, d.getQuantity(), null));
    }

    private PartKey createPartKey(Delivery d, Load l) {
        return l == null ?
                new PartKey(d.getOrderNumber(), null) :
                new PartKey(d.getOrderNumber(), new LoadKey(l.getDate(), l.getShift()));
    }


    private class LoadKey {
        private final LocalDate date;
        private final DeliveryShift shift;

        private LoadKey(LocalDate date, DeliveryShift shift) {
            this.date = date;
            this.shift = shift;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LoadKey loadKey = (LoadKey) o;
            return Objects.equals(date, loadKey.date) &&
                    Objects.equals(shift, loadKey.shift);
        }

        @Override
        public int hashCode() {
            return Objects.hash(date, shift);
        }
    }
}
