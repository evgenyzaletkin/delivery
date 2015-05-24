package com.home.delivery.app;

import com.google.common.collect.Maps;
import com.home.delivery.app.paths.RoutingService;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

@Named("loadsService")
public class LoadsService {

    private final ExecutorService executorService;
    private final RoutingService routingService;

    private final ConcurrentMap<LoadKey, Load> loads = Maps.newConcurrentMap();

    @Inject
    public LoadsService(ExecutorService executorService,
                        RoutingService routingService) {
        this.executorService = executorService;
        this.routingService = routingService;
    }


    public Load getLoad(LocalDate date, DeliveryShift shift) {
        LoadKey key = new LoadKey(date, shift);
        return loads.computeIfAbsent(key, k -> {
            Load load = new Load();
            load.setDate(k.date);
            load.setShift(k.shift);
            return load;
        });
    }

    public void updateLoad(Load load) {
        load.setIsRouted(false);
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
