package com.home.delivery.web.controllers;

import com.home.delivery.app.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
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
    public String showLoad(@RequestParam("date")LocalDate date,
                           @RequestParam("shift") DeliveryShift shift,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (deliveriesService.getValidDeliveries().size() == 0) return "redirect:upload";
        Load load = loadsService.getLoad(date, shift);
        if (load == null) load = loadsService.createNewLoad(date, shift);
        model.addAttribute("load", load);
        model.addAttribute("currentParts", loadsService.getDeliveryPartsForLoad(load));
        model.addAttribute("availableParts", loadsService.getAvailableDeliveryPartsForLoad(load));
        if (redirectAttributes.containsAttribute("submited")) model.addAttribute("submited", true);
        return "load";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String updateLoad(@RequestParam ("date") LocalDate date,
                             @RequestParam("shift") DeliveryShift shift,
                             @RequestParam(value = "delivery", defaultValue = "") List<String> deliveriesId,
                             @RequestParam(value = "items", defaultValue = "") List<Integer> items,
                             RedirectAttributes redirectAttributes) {
        Load load = loadsService.getLoad(date, shift);
        loadsService.updateLoad(load, deliveriesId, items);
        redirectAttributes.addFlashAttribute("submited", true);
        return "redirect:loads?date=" + date + "&shift=" + shift;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"date", "shift", "next"})
    public String showNext(@RequestParam("date")LocalDate date, @RequestParam("shift") DeliveryShift shift) {
        DeliveryShift nextShift;
        if (shift == DeliveryShift.MORNING) nextShift = DeliveryShift.AFTERNOON;
        else if (shift == DeliveryShift.AFTERNOON) nextShift = DeliveryShift.EVENING;
        else nextShift = DeliveryShift.MORNING;

        if (nextShift == DeliveryShift.MORNING) {
            List<LocalDate> allDates = deliveriesService.getValidDeliveries().
                    stream().
                    map(Delivery::getDeliveryDate).
                    collect(Collectors.toList());
            LocalDate maxDate = Collections.max(allDates);
            do {
                date = date.plusDays(1L);
                if (allDates.contains(date)) break;
            } while (date.isBefore(maxDate));

        }
        return "redirect:loads?date=" + date + "&shift=" + nextShift;
    }

    @RequestMapping(method = RequestMethod.GET, params = {"date", "shift", "previous"})
    public String showPrevious(@RequestParam("date")LocalDate date, @RequestParam("shift") DeliveryShift shift) {
        DeliveryShift previousShift;
        if (shift == DeliveryShift.MORNING) previousShift = DeliveryShift.EVENING;
        else if (shift == DeliveryShift.AFTERNOON) previousShift = DeliveryShift.MORNING;
        else previousShift = DeliveryShift.AFTERNOON;

        if (previousShift == DeliveryShift.EVENING) {
            List<LocalDate> allDates = deliveriesService.getValidDeliveries().
                    stream().
                    map(Delivery::getDeliveryDate).
                    collect(Collectors.toList());
            LocalDate minDate = Collections.min(allDates);
            do {
                date = date.minusDays(1L);
                if (allDates.contains(date)) break;
            } while (date.isAfter(minDate));

        }
        return "redirect:loads?date=" + date + "&shift=" + previousShift;
    }

    @ModelAttribute(ALL_SHIFTS_ATTRIBUTE)
    public Set<DeliveryShift> allShifts() {
        return EnumSet.allOf(DeliveryShift.class);
    }
}
