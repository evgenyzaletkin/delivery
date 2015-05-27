package com.home.delivery.web.validators;

import com.home.delivery.app.DeliveriesService;
import com.home.delivery.app.Delivery;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by evgeny on 28.05.15.
 */
@Named
public class DeliveryValidator implements Validator {

    private final DeliveriesService deliveriesService;

    @Inject
    public DeliveryValidator(DeliveriesService deliveriesService) {
        this.deliveriesService = deliveriesService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Delivery.class == aClass;
    }

    @Override
    public void validate(Object o, Errors errors) {
        Delivery delivery = (Delivery) o;
        long count = deliveriesService.getAllDeliveries().stream().
                filter(d -> d.getOrderNumber().equals(delivery.getOrderNumber())).
                count();
        if (count > 1) errors.rejectValue("orderNumber", "deliveries.orderexits");
    }


}
