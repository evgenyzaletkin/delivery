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
import java.util.stream.Collectors;

/**
 * Created by evgeny on 05.05.15.
 */
@Named
public class CSVFileReader {

    public List<Delivery> readDeliveries(Reader in) {
        return getRecords(in).
                stream().
                skip(1).
                filter(record -> !Strings.isNullOrEmpty(record.get(0))).
                map(this::fromRecord).
                collect(Collectors.toList());
    }

//    private final static int FIELDS_COUNT = 10;
    private final static int DATE_INDEX = 0;
    private final static int SHIFT_INDEX = 1;
    private final static int CLIENT_NAME_INDEX = 8;
    private final static int CLIENT_ADDRESS_INDEX = 9;
    private final static int CLIENT_CITY_INDEX = 10;
    private final static int CLIENT_STATE_INDEX = 11;
    private final static int DESTINATION_ZIP_INDEX = 12;
    private final static int PHONE_NUMBER_INDEX = 14;
    private final static int ORDER_NUMBER_INDEX = 16;
    private final static int VOLUME_INDEX = 17;
    private final static int QUANTITY_INDEX = 18;


    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("M/dd/yyyy");

    private Delivery fromRecord(CSVRecord record) {
        LocalDate date = LocalDate.parse(record.get(DATE_INDEX), FORMATTER);
        DeliveryShift deliveryShift = DeliveryShift.fromLetter(record.get(SHIFT_INDEX));
        String clientName = record.get(CLIENT_NAME_INDEX);
        String clientAddress = record.get(CLIENT_ADDRESS_INDEX);
        String clientCity = record.get(CLIENT_CITY_INDEX);
        String clientState = record.get(CLIENT_STATE_INDEX);
        Integer destinationZip = Integer.valueOf(record.get(DESTINATION_ZIP_INDEX));
        String phoneNumber = record.get(PHONE_NUMBER_INDEX);
        String orderNumber = record.get(ORDER_NUMBER_INDEX);
        Float volume = Float.valueOf(record.get(VOLUME_INDEX));
        Integer quantity = Integer.valueOf(record.get(QUANTITY_INDEX));
        return new Delivery(date, deliveryShift, clientName, clientAddress, clientCity, clientState, destinationZip,
                phoneNumber, orderNumber, volume, quantity);
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
