package com.home.delivery.app;

import com.home.delivery.app.io.CSVFileReader;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Created by evgeny on 08.05.15.
 */
@Named("deliveriesService")
public class DeliveriesService {

    private final ConcurrentMap<String, Delivery> deliveries = new ConcurrentHashMap<>();
    private final CSVFileReader csvFileReader;

    @Inject
    public DeliveriesService(CSVFileReader csvFileReader) {
        this.csvFileReader = csvFileReader;
    }

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

    public List<Delivery> getDeliveriesByDate(LocalDate date) {
        return deliveries.values().stream().filter(d -> date.isEqual(d.getDeliveryDate())).collect(Collectors.toList());
    }

    //ToDo WA, should be removed when uploading is available
    @PostConstruct
    private void loadAllDeliveries() {
        InputStream is = DeliveriesService.class.getClassLoader().getResourceAsStream("schedule.csv");
        InputStreamReader isr = new InputStreamReader(is);
        List<Delivery> deliveries = csvFileReader.readDeliveries(isr);
        addDeliveries(deliveries);
    }

}
