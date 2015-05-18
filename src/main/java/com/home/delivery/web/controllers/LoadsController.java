package com.home.delivery.web.controllers;

import com.home.delivery.app.DeliveryShift;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;

/**
 * Created by evgeny on 19.05.15.
 */
@Controller
@RequestMapping("/loads")
public class LoadsController {

    private static final String ALL_SHIFTS_ATTRIBUTE = "allShifts";


    @RequestMapping(method = RequestMethod.GET)
    public String showPicker() {
        return "loadPicker";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String showLoad(@RequestParam("date")LocalDate date, @RequestParam("shift") DeliveryShift shift) {
        throw new IllegalStateException(String.format("Date %s, Shift %s", date, shift));
    }

    @ModelAttribute(ALL_SHIFTS_ATTRIBUTE)
    public Set<DeliveryShift> allShifts() {
        return EnumSet.allOf(DeliveryShift.class);
    }
}
