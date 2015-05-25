package com.home.delivery.web.controllers;

import com.home.delivery.app.DeliveryShift;
import com.home.delivery.app.DriverHelper;
import com.home.delivery.app.Load;
import com.home.delivery.app.LoadsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by evgeny on 25.05.15.
 */
@Controller
@RequestMapping("/driver")
public class DriverController {

    private final DriverHelper driverHelper;
    private final LoadsService loadsService;

    @Inject
    public DriverController(DriverHelper driverHelper, LoadsService loadsService) {
        this.driverHelper = driverHelper;
        this.loadsService = loadsService;
    }


    @RequestMapping(method = RequestMethod.GET)
    public String showDatePick() {
        return "driver/pickDate";
    }

    @RequestMapping(method = RequestMethod.GET, params = "date")
    public String showLoads(Model model, @RequestParam("date") LocalDate date) {
        EnumSet<DeliveryShift> shifts = driverHelper.getShifts(date);
        Map<DeliveryShift, Load> loadsByShifts = new HashMap<>(2);
        for (DeliveryShift shift : shifts) {
            loadsByShifts.put(shift, loadsService.getLoad(date, shift));
        }
        model.addAttribute("loadsAndShifts", loadsByShifts);
        return "driver/deliveries";
    }

}
