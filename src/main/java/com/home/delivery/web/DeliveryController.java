package com.home.delivery.web;

import com.home.delivery.app.DeliveriesService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;

/**
 * Created by evgeny on 08.05.15.
 */
@Controller
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveriesService deliveriesService;

    @Inject
    public DeliveryController(DeliveriesService deliveriesService) {
        this.deliveriesService = deliveriesService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public void getAllDeliveries() {
        deliveriesService.getAllDeliveries();
    }

    @RequestMapping(value = "/{deliveryId}", method = RequestMethod.GET)
    public void getDelivery(@PathVariable(value = "deliveryId") String deliveryId) {
        deliveriesService.getDelivery(deliveryId);
    }
}
