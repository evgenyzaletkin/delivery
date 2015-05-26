package com.home.delivery.web.controllers;

import com.home.delivery.app.DeliveriesService;
import com.home.delivery.app.DeliveryShift;
import com.home.delivery.app.Load;
import com.home.delivery.app.LoadsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

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
        model.addAttribute("currentParts", loadsService.getDeliveryPartsForLoad(load));
        model.addAttribute("availableParts", loadsService.getAvailableDeliveryPartsForLoad(load));
        return "load";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String updateLoad(Load load,
                             @RequestParam(value = "delivery", defaultValue = "") List<String> deliveriesId,
                             @RequestParam(value = "items", defaultValue = "") List<Integer> items) {
        loadsService.updateLoad(load, deliveriesId, items);
        return "redirect:deliveries?date=" + load.getDate() + "&not_loaded=true";
    }

    @ModelAttribute(ALL_SHIFTS_ATTRIBUTE)
    public Set<DeliveryShift> allShifts() {
        return EnumSet.allOf(DeliveryShift.class);
    }
}
