package com.home.delivery.app.paths;

import com.home.delivery.app.Delivery;

/**
 * Created by evgeny on 26.05.15.
 */
public class Utils {

    private Utils(){}

    public static String mapToAddress(Delivery d) {
        return d.getStreet() + ", " + mapToCity(d);
    }

    public static String mapToCity(Delivery d) {
        return d.getCity() + ", " + d.getState() + " " + d.getZip();
    }

    public static final String ORIGIN_ADDRESS = "RALEIGH, NC 27603";
}
