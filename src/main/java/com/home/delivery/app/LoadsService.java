package com.home.delivery.app;

import com.google.common.collect.Maps;
import com.home.delivery.app.paths.RoutingService;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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


    /**
     * 1. Create all loads for the current day;
     * 2. Fill loads with the preferable deliveries by the time shift;
     * 3. Fill loads with the deliveries that may be placed into the load completely;
     * 4. Fill loads until they are full.
     * @param date
     * @param shift
     * @return
     */
    public synchronized Load createNewLoad(LocalDate date, DeliveryShift shift) {
        LoadKey key = new LoadKey(date, shift);
        return loads.computeIfAbsent(key, k -> {
            Load load = new Load();
            load.setDate(date);
            load.setShift(shift);
            gatherPartsForNewLoad(load);
            loads.put(new LoadKey(date, shift), load);
            return load;
        });
    }


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
                mapToDouble(p -> p.getItems() * p.getDelivery().getVolumeNumber()).
                sum();

        if (currentVolume < MAX_VOLUME) {
            List<DeliveryPart> possibleParts = availableDeliveryPartsForLoad.stream().
                    filter(p -> load.getShift() == null).collect(Collectors.toList());

            for (Iterator<DeliveryPart> partIterator = possibleParts.iterator(); partIterator.hasNext();) {
                DeliveryPart possiblePart = partIterator.next();
                double availableVolume = MAX_VOLUME - currentVolume;
                double partVolume = possiblePart.getItems() * possiblePart.getDelivery().getVolumeNumber();
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


    public void updateLoad(Load load) {
        double loadVolume = load.getDeliveries().stream().mapToDouble(Delivery::getVolumeNumber).sum();
        if (loadVolume > MAX_VOLUME) throw new IllegalArgumentException(
                String.format("The maximum volume is exceeded for load %s, %s ", load.getDate(), load.getShift()));
        load.setIsRouted(false);
        loads.put(new LoadKey(load.getDate(), load.getShift()), load);
        executorService.submit(() -> {
            List<Delivery> deliveries = load.getDeliveries();
            List<Delivery> sorted = routingService.buildRoute(deliveries);
            load.setDeliveries(sorted);
            load.setIsRouted(true);
        });
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

            if (deliveryNumber != null ? !deliveryNumber.equals(partKey.deliveryNumber) : partKey.deliveryNumber != null)
                return false;
            if (loadKey != null ? !loadKey.equals(partKey.loadKey) : partKey.loadKey != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = deliveryNumber != null ? deliveryNumber.hashCode() : 0;
            result = 31 * result + (loadKey != null ? loadKey.hashCode() : 0);
            return result;
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
            checkArgument(freePart.getItems() <= newPart.getItems());
            freeItems = freePart.getItems() - newPart.getItems();
        } else {
            checkArgument(freePart.getItems() + currentPart.getItems() >= newPart.getItems());
            freeItems = freePart.getItems() + currentPart.getItems() - newPart.getItems();
        }
        DeliveryPart newFreePart = new DeliveryPart(freePart.getDelivery(), freeItems, null);
        deliveryParts.put(freeKey, newFreePart);
        deliveryParts.put(currentKey, newPart);
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
