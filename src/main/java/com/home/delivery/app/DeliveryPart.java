package com.home.delivery.app;

public class DeliveryPart {

    private final Delivery delivery;
    private final Integer items;
    final Load load;

    public DeliveryPart(Delivery delivery, Integer items, Load load) {
        this.delivery = delivery;
        this.items = items;
        this.load = load;
    }

    public Delivery getDelivery() {
        return delivery;
    }
    public Integer getItems() {
        return items;
    }
    public Load getLoad() {
        return load;
    }
}
