package com.home.delivery.app;

import javax.inject.Named;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Named("loadsService")
public class LoadsService {

    public List<Load> findAll() {
        return Collections.emptyList();
    }

    public List<Load> getLoad(LocalDate date, DeliveryShift shift) {
        return null;
    }

    public void addLoad(Load load) {

    }
}
