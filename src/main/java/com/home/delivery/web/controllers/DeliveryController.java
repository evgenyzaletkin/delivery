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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

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
    public String getAllDeliveries(Model model) {
        List<Delivery> sortedByDate = deliveriesService.getAllDeliveries().
                stream().
                sorted((d1, d2) -> {
                    if (d1.getDeliveryDate() == null) return 1;
                    if (d2.getDeliveryDate() == null) return -1;
                    return d1.getDeliveryDate().compareTo(d2.getDeliveryDate());
                }).
                collect(Collectors.toList());
        model.addAttribute(DELIVERIES_ATTRIBUTE, sortedByDate);
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