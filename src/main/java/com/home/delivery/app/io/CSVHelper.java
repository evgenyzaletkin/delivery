package com.home.delivery.app.io;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.home.delivery.app.Delivery;
import com.home.delivery.app.DeliveryShift;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import javax.inject.Named;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by evgeny on 05.05.15.
 */
@Named
public class CSVHelper {

    public List<Delivery> readDeliveries(Reader in) {

//        Map<String, List<Delivery>> allDeliveriesByNumbers = getRecords(in).
//                stream().
//                skip(1).
//                map(this::fromRecord).
//                collect(Collectors.groupingBy(Delivery::getId));

        return getRecords(in).
                stream().
                skip(1).
                map(this::fromRecord).
                collect(Collectors.toList());
    }

//    private final static int FIELDS_COUNT = 10;
    private final static int DATE_INDEX = 0;
    private final static int SHIFT_INDEX = 1;
    private final static int ORIGIN_NAME_INDEX = 2;
    private final static int ORIGIN_STREET_INDEX = 3;
    private final static int ORIGIN_CITY_INDEX = 4;
    private final static int ORIGIN_STATE_INDEX = 5;
    private final static int ORIGIN_ZIP_INDEX = 6;
    private final static int DESTINATION_NAME_INDEX = 8;
    private final static int DESTINATION_STREET_INDEX = 9;
    private final static int DESTINATION_CITY_INDEX = 10;
    private final static int DESTINATION_STATE_INDEX = 11;
    private final static int DESTINATION_ZIP_INDEX = 12;
    private final static int PHONE_NUMBER_INDEX = 14;
    private final static int ORDER_NUMBER_INDEX = 16;
    private final static int VOLUME_INDEX = 17;
    private final static int QUANTITY_INDEX = 18;

    private AtomicInteger idGen = new AtomicInteger(1);


    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("M/dd/yyyy");

    private Delivery fromRecord(CSVRecord record) {
        Delivery delivery = new Delivery();
        delivery.setId(idGen.getAndIncrement() + "");
        LocalDate date = Strings.isNullOrEmpty(record.get(DATE_INDEX)) ? null : LocalDate.parse(record.get(DATE_INDEX), FORMATTER);
        delivery.setDeliveryDate(date);
        DeliveryShift deliveryShift = DeliveryShift.fromLetter(record.get(SHIFT_INDEX));
        delivery.setDeliveryShift(deliveryShift);

        delivery.setOriginName(record.get(ORIGIN_NAME_INDEX));
        delivery.setOriginStreet(record.get(ORIGIN_STREET_INDEX));
        delivery.setOriginCity(record.get(ORIGIN_CITY_INDEX));
        delivery.setOriginState(record.get(ORIGIN_STATE_INDEX));
        Integer originZip = Strings.isNullOrEmpty(record.get(ORIGIN_ZIP_INDEX)) ? 0 : Integer.valueOf(record.get(ORIGIN_ZIP_INDEX));
        delivery.setOriginZip(originZip);

        delivery.setDestinationName(record.get(DESTINATION_NAME_INDEX));
        delivery.setDestinationStreet(record.get(DESTINATION_STREET_INDEX));
        delivery.setDestinationCity(record.get(DESTINATION_CITY_INDEX));
        delivery.setDestinationState(record.get(DESTINATION_STATE_INDEX));
        Integer destinationZip = Strings.isNullOrEmpty(record.get(DESTINATION_ZIP_INDEX)) ? 0 :
                Integer.valueOf(record.get(DESTINATION_ZIP_INDEX));
        delivery.setDestinationZip(destinationZip);

        delivery.setPhoneNumber(record.get(PHONE_NUMBER_INDEX));
        delivery.setOrderNumber(record.get(ORDER_NUMBER_INDEX));
        float volume = Strings.isNullOrEmpty(record.get(VOLUME_INDEX)) ? 0.0f : Float.parseFloat(record.get(VOLUME_INDEX));
        delivery.setVolume(volume);
        int quantity = Strings.isNullOrEmpty(record.get(QUANTITY_INDEX)) ? 0 : Integer.parseInt(record.get(QUANTITY_INDEX));
        delivery.setQuantity(quantity);
        return delivery;
    }

    private List<CSVRecord> getRecords(Reader in) {
        try {
            CSVParser parser = CSVFormat.EXCEL.parse(in);
            return Lists.newArrayList(parser);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void printCSV(Writer writer, List<String> header, List<List<String>> data) {
        try {
            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withRecordSeparator("\n"));
            printer.printRecord(header);
            for (List<String> datum  : data) {
                printer.printRecord(datum);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }





}
