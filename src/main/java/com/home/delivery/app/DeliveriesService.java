package com.home.delivery.app;

import com.home.delivery.app.io.CSVFileReader;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.List;

/**
 * Created by evgeny on 08.05.15.
 */
@Named("deliveriesService")
public class DeliveriesService {

    private final CSVFileReader csvFileReader;

    @Inject
    public DeliveriesService(CSVFileReader csvFileReader) {
        this.csvFileReader = csvFileReader;
    }

    public List<Delivery> getAllDeliveries() {
        return Collections.emptyList();
    }

    public Delivery getDelivery(String deliveryId) {
        return null;
    }

    public void loadDeliveries(Object o) {

    }

}
