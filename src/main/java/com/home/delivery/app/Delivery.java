package com.home.delivery.app;

import java.time.LocalDate;

/**
 * Created by evgeny on 05.05.15.
 */
public class Delivery {
    private final LocalDate deliveryDate;
    private final DeliveryShift deliveryShift;
//    private final String originName;
//    private final String originAdress;
//    private final String originCity;
//    private final String originState;
//    private final Integer originZip;
//    private final String originCountry;
    private final String clientName;
    private final String destinationAdress;
    private final String destinationCity;
    private final String destinationState;
    private final Integer destinationZip;
//    private final String destinationCountry;
    private final String phoneNumber;
    private final String orderNumber;
    private final Float volumeNumber;
    private final Integer quantity;

    public Delivery(LocalDate deliveryDate, DeliveryShift deliveryShift, String clientName, String destinationAdress,
                    String destinationCity, String destinationState, Integer destinationZip, String phoneNumber,
                    String orderNumber, Float volumeNumber, Integer quantity) {
        this.deliveryDate = deliveryDate;
        this.deliveryShift = deliveryShift;
        this.clientName = clientName;
        this.destinationAdress = destinationAdress;
        this.destinationCity = destinationCity;
        this.destinationState = destinationState;
        this.destinationZip = destinationZip;
        this.phoneNumber = phoneNumber;
        this.orderNumber = orderNumber;
        this.volumeNumber = volumeNumber;
        this.quantity = quantity;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public DeliveryShift getDeliveryShift() {
        return deliveryShift;
    }

    public String getClientName() {
        return clientName;
    }

    public String getDestinationAdress() {
        return destinationAdress;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public String getDestinationState() {
        return destinationState;
    }

    public Integer getDestinationZip() {
        return destinationZip;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public Float getVolumeNumber() {
        return volumeNumber;
    }

    public Integer getQuantity() {
        return quantity;
    }

    //    private final static int FIELDS_COUNT = 10;
//    private final static int DATE_INDEX = 0;
//    private final static int SHIFT_INDEX = 1;
//    private final static int CLIENT_NAME_INDEX = 8;
//    private final static int CLIENT_ADDRESS_INDEX = 9;
//    private final static int CLIENT_CITY_INDEX = 10;
//    private final static int CLIENT_STATE_INDEX = 11;
//    private final static int DESTINATION_ZIP_INDEX = 12;
//    private final static int PHONE_NUMBER_INDEX = 14;
//    private final static int ORDER_NUMBER_INDEX = 16;
//    private final static int VOLUME_INDEX = 17;
//    private final static int QUANTITY_INDEX = 18;


//    public static Delivery fromArray(@Nonnull String[] data) {
//        checkNotNull(data);
//        checkArgument(data.length == FIELDS_COUNT);
//        LocalDate date = LocalDate.parse(data[DATE_INDEX]);
//        DeliveryShift deliveryShift = DeliveryShift.fromLetter(data[SHIFT_INDEX]);
//        String clientName = data[CLIENT_NAME_INDEX];
//        String clientAddress = data[CLIENT_ADDRESS_INDEX];
//        String clientCity = data[CLIENT_CITY_INDEX];
//        String clientState = data[CLIENT_STATE_INDEX];
//        Integer destinationZip = Integer.valueOf(data[DESTINATION_ZIP_INDEX]);
//        String phoneNumber = data[PHONE_NUMBER_INDEX];
//        Integer orderNumber = Integer.valueOf(data[ORDER_NUMBER_INDEX]);
//        Float volume = Float.valueOf(data[VOLUME_INDEX]);
//        Integer quantity = Integer.valueOf(data[QUANTITY_INDEX]);
//        return new Delivery(date, deliveryShift, clientName, clientAddress, clientCity, clientState, destinationZip,
//                phoneNumber, orderNumber, volume, quantity);
//    }


    @Override
    public String toString() {
        return "Delivery{" +
                "deliveryDate=" + deliveryDate +
                ", deliveryShift=" + deliveryShift +
                ", clientName='" + clientName + '\'' +
                ", destinationAdress='" + destinationAdress + '\'' +
                ", destinationCity='" + destinationCity + '\'' +
                ", destinationState='" + destinationState + '\'' +
                ", destinationZip=" + destinationZip +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", volumeNumber=" + volumeNumber +
                ", quantity=" + quantity +
                '}';
    }

}
