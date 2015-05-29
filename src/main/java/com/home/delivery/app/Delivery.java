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
    @NotNull
    private LocalDate deliveryDate;
    private DeliveryShift deliveryShift;


    @NotEmpty
    private String orderNumber;
    @Min(0)
    private Float volume;
    @Min(1)
    private Integer quantity;


    @NotEmpty
    private String originName;
    @NotEmpty
    private String originStreet;
    @NotEmpty
    private String originCity;
    @NotEmpty
    private String originState;
    @Range(min = 1001, max = 99999)
    private Integer originZip;

    @NotEmpty
    private String destinationName;
    @NotEmpty
    private String destinationStreet;
    @NotEmpty
    private String destinationCity;
    @NotEmpty
    private String destinationState;
    @Range(min = 1001, max = 99999)
    private Integer destinationZip;
    @NotEmpty
    private String phoneNumber;

    boolean isValid = true;
    boolean isReturn = false;

    public Delivery() {
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

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getDestinationStreet() {
        return destinationStreet;
    }

    public void setDestinationStreet(String destinationStreet) {
        this.destinationStreet = destinationStreet;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public String getDestinationState() {
        return destinationState;
    }

    public void setDestinationState(String destinationState) {
        this.destinationState = destinationState;
    }

    public Integer getDestinationZip() {
        return destinationZip;
    }

    public void setDestinationZip(Integer destinationZip) {
        this.destinationZip = destinationZip;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getOriginStreet() {
        return originStreet;
    }

    public void setOriginStreet(String originStreet) {
        this.originStreet = originStreet;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getOriginState() {
        return originState;
    }

    public void setOriginState(String originState) {
        this.originState = originState;
    }

    public Integer getOriginZip() {
        return originZip;
    }

    public void setOriginZip(Integer originZip) {
        this.originZip = originZip;
    }

    public String getClientName() {
        return isReturn ? getOriginName() : getDestinationName();
    }

    public String getStreet() {
        return isReturn ? getOriginStreet() : getDestinationStreet();
    }

    public String getCity() {
        return isReturn ? getOriginCity() : getDestinationCity();
    }

    public String getState() {
        return isReturn ? getOriginState() : getDestinationState();
    }

    public Integer getZip() {
        return isReturn ? getOriginZip() : getDestinationZip();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Float getVolume() {
        return volume;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
    }

    public Float getDownloadVolume() {
        return isReturn ? 0.0f : getVolume();
    }

    public Float getUploadVolume() {
        return isReturn ? getVolume() : 0.0f;
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


    public boolean isReturn() {
        return isReturn;
    }

    public void setIsReturn(boolean isReturn) {
        this.isReturn = isReturn;
    }


    @Override
    public String toString() {
        return "Delivery{" +
                "id='" + id + '\'' +
                ", deliveryDate=" + deliveryDate +
                ", deliveryShift=" + deliveryShift +
                ", orderNumber='" + orderNumber + '\'' +
                ", volume=" + volume +
                ", quantity=" + quantity +
                ", originName='" + originName + '\'' +
                ", originStreet='" + originStreet + '\'' +
                ", originCity='" + originCity + '\'' +
                ", originState='" + originState + '\'' +
                ", originZip=" + originZip +
                ", destinationName='" + destinationName + '\'' +
                ", destinationStreet='" + destinationStreet + '\'' +
                ", destinationCity='" + destinationCity + '\'' +
                ", destinationState='" + destinationState + '\'' +
                ", destinationZip=" + destinationZip +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", isValid=" + isValid +
                ", isReturn=" + isReturn +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Delivery delivery = (Delivery) o;

        if (!id.equals(delivery.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return orderNumber.hashCode();
    }
}
