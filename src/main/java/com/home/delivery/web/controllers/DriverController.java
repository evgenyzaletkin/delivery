package com.home.delivery.web.controllers;

import com.home.delivery.app.*;
import com.home.delivery.app.paths.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by evgeny on 25.05.15.
 */
@Controller
@RequestMapping("/driver")
public class DriverController {

    private final DriverHelper driverHelper;
    private final LoadsService loadsService;

    @Inject
    public DriverController(DriverHelper driverHelper, LoadsService loadsService) {
        this.driverHelper = driverHelper;
        this.loadsService = loadsService;
    }


    @RequestMapping(method = RequestMethod.GET)
    public String showDatePick() {
        return "driver/pickDate";
    }

    @RequestMapping(method = RequestMethod.GET, params = "date")
    public String showLoads(Model model, @RequestParam("date") LocalDate date) {
        EnumSet<DeliveryShift> shifts = driverHelper.getShifts(date);
        Map<Load, List<DeliveryPart>> partsByLoads = new HashMap<>();
        for (DeliveryShift shift : shifts) {
            Load load = loadsService.getLoad(date, shift);
            if (load != null) {
                List<DeliveryPart> deliveryPartsForLoad = loadsService.getDeliveryPartsForLoad(load);
                if (load.isRouted()) {
                    Map<String, List<DeliveryPart>> groupedByAddresses = deliveryPartsForLoad.
                            stream().
                            collect(Collectors.
                                    groupingBy(p -> Utils.mapToAddress(p.getDelivery())));
                    deliveryPartsForLoad = load.getTour().getPath().stream().
                            filter(s -> !Utils.ORIGIN_ADDRESS.equals(s)).
                            flatMap(address -> groupedByAddresses.get(address).stream()).
                            collect(Collectors.toList());
                }
                partsByLoads.put(load, deliveryPartsForLoad);
            }
        }
        model.addAttribute("loadsAndShifts", partsByLoads);
        return "driver/deliveries";
    }

}
