package com.home.delivery.app;

import com.google.common.collect.Maps;
import com.home.delivery.app.paths.RoutingService;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

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
            List<Delivery> deliveries = deliveriesService.getAllDeliveries().stream().
                    filter(d -> d.getLoad() == null && date.isEqual(d.getDeliveryDate()) && shift == d.getDeliveryShift()).
                    collect(Collectors.toList());
            load.setDeliveries(deliveries);
            return load;
        });
    }

    public void updateLoad(Load load) {
        double loadVolume = load.getDeliveries().stream().mapToDouble(Delivery::getVolumeNumber).sum();
        if (loadVolume > MAX_VOLUME) throw new IllegalArgumentException(
                String.format("The maximum volume is exceeded for load %s, %s ", load.getDate(), load.getShift()));
        load.setIsRouted(false);
        load.getDeliveries().forEach(d -> d.setLoad(load));
        loads.put(new LoadKey(load.getDate(), load.getShift()), load);
        executorService.submit(() -> {
            List<Delivery> deliveries = load.getDeliveries();
            List<Delivery> sorted = routingService.buildRoute(deliveries);
            load.setDeliveries(sorted);
            load.setIsRouted(true);
        });
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
