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
import java.util.stream.Collectors;

/**
 * Created by evgeny on 08.05.15.
 */
@Named("deliveriesService")
public class DeliveriesService {

    volatile Set<Delivery> deliveries = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final CSVFileReader csvFileReader;

    @Inject
    public DeliveriesService(CSVFileReader csvFileReader) {
        this.csvFileReader = csvFileReader;
    }

    public Set<Delivery> getAllDeliveries() {
        return deliveries;
    }

    public Optional<Delivery> getDelivery(String deliveryId) {
        return deliveries.stream().filter(d -> d.getOrderNumber().equals(deliveryId)).findAny();
    }

    public void addDeliveries(Collection<Delivery> deliveries) {
        this.deliveries.addAll(deliveries);
    }

    public void removeDelivery(Delivery delivery) {
        this.deliveries.remove(delivery);
    }

    public List<Delivery> getDeliveriesByDate(LocalDate date) {
        return deliveries.stream().filter(d -> date.isEqual(d.getDeliveryDate())).collect(Collectors.toList());
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
