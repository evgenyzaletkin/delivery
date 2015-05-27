package com.home.delivery.app.paths;

import com.home.delivery.app.Delivery;

/**
 * Created by evgeny on 26.05.15.
 */
public class Utils {

    public static String NO_ORDER_NUMBER = "Number is missed";

    private Utils(){}

    public static String mapToAddress(Delivery d) {
        return d.getStreet().toUpperCase() + ", " + mapToCity(d);
    }

    public static String mapToCity(Delivery d) {
        return (d.getCity() + ", " + d.getState() + " " + d.getZip()).toUpperCase();
    }

    public static final String ORIGIN_ADDRESS = "RALEIGH, NC 27603";
}
