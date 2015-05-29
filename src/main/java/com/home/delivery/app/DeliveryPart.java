package com.home.delivery.app;

public class DeliveryPart {

    private final Delivery delivery;
    private final Integer items;
    final DateShiftKey dateShiftKey;

    public DeliveryPart(Delivery delivery, Integer items, DateShiftKey dateShiftKey) {
        this.delivery = delivery;
        this.items = items;
        this.dateShiftKey = dateShiftKey;
    }

    public Delivery getDelivery() {
        return delivery;
    }
    public Integer getItems() {
        return items;
    }
    public DateShiftKey getDateShiftKey() {
        return dateShiftKey;
    }
    public Float getVolume() {
        return items * delivery.getVolume() / delivery.getQuantity();
    }

    public Float getDownloadVolume() {
        return items * delivery.getDownloadVolume() / delivery.getQuantity();
    }

    public Float getUploadVolume() {
        return items * delivery.getUploadVolume() / delivery.getQuantity();
    }

}
