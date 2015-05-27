package com.home.delivery.app;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Created by evgeny on 05.05.15.
 */
public final class Delivery {
    private String id;
    @NotNull private LocalDate deliveryDate;
    private DeliveryShift deliveryShift;
//    private final String originName;
//    private final String originAdress;
//    private final String originCity;
//    private final String originState;
//    private final Integer originZip;
//    private final String originCountry;
    @NotEmpty private String clientName;
    @NotEmpty private String street;
    @NotEmpty private String city;
    @NotEmpty private String state;
    @Range(min = 1001, max = 99999) private Integer zip;
//    private final String destinationCountry;
    private String phoneNumber;
    @NotEmpty private String orderNumber;
    @Min(0) private Float volumeNumber;
    @Min(1) private Integer quantity;
    boolean isValid = true;

    public Delivery() {
    }

    public Delivery(String id, LocalDate deliveryDate, DeliveryShift deliveryShift, String clientName, String street,
                    String city, String state, Integer destinationZip, String phoneNumber,
                    String orderNumber, Float volumeNumber, Integer quantity) {
        this.id = id;
        this.deliveryDate = deliveryDate;
        this.deliveryShift = deliveryShift;
        this.clientName = clientName;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = destinationZip;
        this.phoneNumber = phoneNumber;
        this.orderNumber = orderNumber;
        this.volumeNumber = volumeNumber;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public DeliveryShift getDeliveryShift() {
        return deliveryShift;
    }

    public void setDeliveryShift(DeliveryShift deliveryShift) {
        this.deliveryShift = deliveryShift;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getZip() {
        return zip;
    }

    public void setZip(Integer zip) {
        this.zip = zip;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }



    public Float getVolumeNumber() {
        return volumeNumber;
    }

    public void setVolumeNumber(Float volumeNumber) {
        this.volumeNumber = volumeNumber;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "deliveryDate=" + deliveryDate +
                ", deliveryShift=" + deliveryShift +
                ", clientName='" + clientName + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zip=" + zip +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", volumeNumber=" + volumeNumber +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Delivery delivery = (Delivery) o;

        if (!orderNumber.equals(delivery.orderNumber)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return orderNumber.hashCode();
    }
}
