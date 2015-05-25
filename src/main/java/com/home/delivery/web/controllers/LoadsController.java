package com.home.delivery.web.controllers;

import com.home.delivery.app.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by evgeny on 19.05.15.
 */
@Controller
@RequestMapping("/loads")
public class LoadsController {

    private static final String ALL_SHIFTS_ATTRIBUTE = "allShifts";

    private final LoadsService loadsService;
    private final DeliveriesService deliveriesService;

    @Inject
    public LoadsController(LoadsService loadsService,
                           DeliveriesService deliveriesService) {
        this.loadsService = loadsService;
        this.deliveriesService = deliveriesService;
    }


    @RequestMapping(method = RequestMethod.GET)
    public String showPicker() {
        return "loadPicker";
    }

    @RequestMapping(method = RequestMethod.GET, params = {"date", "shift"})
    public String showLoad(@RequestParam("date")LocalDate date, @RequestParam("shift") DeliveryShift shift, Model model) {
        Load load = loadsService.getLoad(date, shift);
        if (load == null) load = loadsService.createNewLoad(date, shift);
        model.addAttribute("load", load);
        List<Delivery> deliveriesForLoad = load.getDeliveries();
        List<Delivery> freeDeliveries = deliveriesService.getAllDeliveries().
                stream().
                filter(d -> date.isEqual(d.getDeliveryDate())).
                filter(d -> d.getLoad() == null).
                filter(d -> !deliveriesForLoad.contains(d)).
                collect(Collectors.toList());
        model.addAttribute("deliveries", freeDeliveries);
        return "load";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String updateLoad(Load load,
                             @RequestParam(value = "delivery", defaultValue = "") List<String> deliveriesId) {
        Load oldLoad = loadsService.getLoad(load.getDate(), load.getShift());
        oldLoad.getDeliveries().forEach(d -> d.setLoad(null));
        List<Delivery> deliveries = deliveriesId.stream().
                map(deliveriesService::getDelivery).
                filter(Optional::isPresent).
                map(Optional::get).
                collect(Collectors.toList());
        deliveries.forEach(d -> d.setLoad(load));
        load.setDeliveries(deliveries);
        loadsService.updateLoad(load);
        return "redirect:deliveries?date=" + load.getDate() + "&not_loaded=true";
    }

    @ModelAttribute(ALL_SHIFTS_ATTRIBUTE)
    public Set<DeliveryShift> allShifts() {
        return EnumSet.allOf(DeliveryShift.class);
    }
}
