package com.home.delivery.app;

import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by evgeny on 25.05.15.
 */
@Named
public class DriverHelper {

    private final LoadsService loadsService;
    final static String DRIVER1 = "driver1";
    final static String DRIVER2 = "driver2";

    private static final LocalDate START_DATE = LocalDate.of(2014, 9, 15);

    @Inject
    public DriverHelper(LoadsService loadsService) {
        this.loadsService = loadsService;
    }


    public EnumSet<DeliveryShift> getShifts(LocalDate date) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isSwaped = (date.getDayOfYear() - START_DATE.getDayOfYear()) % 2 == 1;
        if (isSwaped) {
            return name.equals(DRIVER1) ?
                    EnumSet.of(DeliveryShift.AFTERNOON) :
                    EnumSet.of(DeliveryShift.MORNING, DeliveryShift.EVENING);
        } else {
            return name.equals(DRIVER1) ?
                    EnumSet.of(DeliveryShift.MORNING, DeliveryShift.EVENING) :
                    EnumSet.of(DeliveryShift.AFTERNOON);
        }
    }

}
