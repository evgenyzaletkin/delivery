package com.home.delivery.app;

/**
 * Created by evgeny on 26.05.15.
 */
public class Utils {

    public static String NO_ORDER_NUMBER = "Number is missed";

    private Utils(){}

    public static String mapToAddress(Delivery d) {
        String s = (d.getStreet().toUpperCase() + ", " + mapToCity(d)).
                replace(" DRIVE,", " DR,").
                replace(" ROAD,", " RD,").
                replace(" STREET,", " ST,");
        return s;
    }

    public static String mapToCity(Delivery d) {
        return (d.getCity() + ", " + d.getState() + " " + d.getZip()).toUpperCase();
    }

    public static final String ORIGIN_ADDRESS = "RALEIGH, NC 27603";
    public static final String LARKIN_NAME = "Larkin LLC";
    public static final double MAX_VOLUME = 1400.0;


}
