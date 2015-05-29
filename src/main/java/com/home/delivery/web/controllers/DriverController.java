package com.home.delivery.web.controllers;

import com.home.delivery.app.*;
import com.home.delivery.app.io.CSVHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by evgeny on 25.05.15.
 */
@Controller
@RequestMapping("/driver")
public class DriverController {

    private final DriverHelper driverHelper;
    private final LoadsService loadsService;
    private final CSVHelper csvHelper;
    private final List<String> header = Arrays.asList("Order", "Street", "City", "State", "ZIP", "Date",
            "Purchase Order", "Items", "Client Name","Phone");

    @Inject
    public DriverController(DriverHelper driverHelper, LoadsService loadsService, CSVHelper csvHelper) {
        this.driverHelper = driverHelper;
        this.loadsService = loadsService;
        this.csvHelper = csvHelper;
    }


    @RequestMapping(method = RequestMethod.GET)
    public String showLoads(Model model, @RequestParam(value = "date", required = false) LocalDate date) {
        if (date == null) date = LocalDate.of(2014, 9, 15);
        EnumSet<DeliveryShift> shifts = driverHelper.getShifts(date);
        Map<Load, List<DeliveryPart>> partsByLoads = new HashMap<>();
        for (DeliveryShift shift : shifts) {
            Load load = loadsService.getLoad(date, shift);
            if (load != null) {
                List<DeliveryPart> deliveryPartsForLoad = getSortedDeliveryParts(load);
                partsByLoads.put(load, deliveryPartsForLoad);
            }
        }
        model.addAttribute("loadsAndShifts", partsByLoads);
        model.addAttribute("date", date);
        return "driver/deliveries";
    }

    @RequestMapping("/download")
    public void downloadRouteList(@RequestParam("date") LocalDate date,
                                  @RequestParam("shift") DeliveryShift shift,
                                  HttpServletResponse response) throws IOException {
        Load load = loadsService.getLoad(date, shift);
        if (load != null) {
            response.setContentType("text/csv");
            String fileName = "route_for_" + date + "_" + shift.toString().toLowerCase() + ".csv";
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            PrintWriter writer = response.getWriter();
            List<DeliveryPart> parts = getSortedDeliveryParts(load);
            List<List<String>> lists = deliveriesToStrings(parts);
            csvHelper.printCSV(writer, header, lists);
            writer.flush();
        }
    }

    List<DeliveryPart> getSortedDeliveryParts(Load load) {
        return load.getWaypoints().stream().flatMap(w -> w.getParts().stream()).collect(Collectors.toList());
    }

    List<List<String>> deliveriesToStrings(List<DeliveryPart> parts) {
        List<List<String>> toReturn = new ArrayList<>(parts.size());
        int i = 1;
        for (DeliveryPart part : parts) {
            List<String> strings = new ArrayList<>(10);
            strings.add(i++ + "");
            strings.add(part.getDelivery().getDestinationStreet());
            strings.add(part.getDelivery().getDestinationCity());
            strings.add(part.getDelivery().getDestinationState());
            strings.add(part.getDelivery().getDestinationZip() + "");
            strings.add(part.getDelivery().getDeliveryDate().toString());
            strings.add(part.getDelivery().getId() + "");
            strings.add(part.getItems() + "");
            strings.add(part.getDelivery().getDestinationName());
            strings.add(part.getDelivery().getPhoneNumber());
            toReturn.add(strings);
        }
        return toReturn;
    }



}
