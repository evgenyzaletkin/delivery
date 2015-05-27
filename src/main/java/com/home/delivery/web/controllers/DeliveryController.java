package com.home.delivery.web.controllers;

import com.home.delivery.app.DeliveriesService;
import com.home.delivery.app.Delivery;
import com.home.delivery.app.DeliveryShift;
import com.home.delivery.web.ResourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by evgeny on 08.05.15.
 */
@Controller
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveriesService deliveriesService;
    private static final Log log = LogFactory.getLog(DeliveryController.class);
    private static final String DELIVERIES_ATTRIBUTE = "deliveries";
    private static final String DELIVERY_ATTRIUBTE = "delivery";
    private static final String ALL_SHIFTS_ATTRIBUTE = "allShifts";

    @Inject
    public DeliveryController(DeliveriesService deliveriesService) {
        this.deliveriesService = deliveriesService;
    }


    @RequestMapping(method = RequestMethod.GET)
    public String getDeliveries(Model model,
                                @RequestParam(value = "date", required = false) LocalDate nullableDate,
                                @RequestParam(value = "corrupted", defaultValue = "False") Boolean corrupted,
                                @RequestParam(value = "not_loaded", defaultValue = "False") Boolean notLoaded){
        LocalDate date = nullableDate == null && !corrupted ? LocalDate.of(2014, 9, 15) : nullableDate;
        Stream<Delivery> stream = deliveriesService.getAllDeliveries().stream();
        stream = date != null ? stream.filter(d -> date.equals(d.getDeliveryDate())) : stream;
        stream = corrupted ? stream.filter(DeliveriesService.CORRUPTED) : stream;
        stream = notLoaded ? stream.filter(d -> d.getLoad() == null) : stream;
        List<Delivery> deliveries = stream.collect(Collectors.toList());
        model.addAttribute(DELIVERIES_ATTRIBUTE, deliveries);
        model.addAttribute("date", date);
        model.addAttribute("corrupted", corrupted);
        model.addAttribute("not_loaded", notLoaded);
        return "deliveries";
    }

    @RequestMapping(value = "/{deliveryId}", method = RequestMethod.GET)
    public String getDelivery(@PathVariable(value = "deliveryId") String deliveryId, Model model) {
        log.debug("Getting delivery" + deliveryId);
        Optional<Delivery> delivery = deliveriesService.getDelivery(deliveryId);
        return delivery.map(d -> {
//            model.addAttribute(ALL_SHIFTS_ATTRIBUTE, EnumSet.allOf(DeliveryShift.class));
            model.addAttribute(DELIVERY_ATTRIUBTE, d);
            return "delivery";
        }).orElseThrow(ResourceNotFoundException::new);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String updateDelivery(@Valid Delivery delivery, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "delivery";
        deliveriesService.addDeliveries(Collections.singletonList(delivery));
        return "redirect:deliveries";
    }

    @ModelAttribute(ALL_SHIFTS_ATTRIBUTE)
    public Set<DeliveryShift> allShifts() {
        return EnumSet.allOf(DeliveryShift.class);
    }
}
