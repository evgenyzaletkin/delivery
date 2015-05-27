package com.home.delivery.web.controllers;

import com.home.delivery.app.DeliveriesService;
import com.home.delivery.app.Delivery;
import com.home.delivery.app.io.CSVHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by evgeny on 10.05.15.
 */
@Controller
@RequestMapping("/upload")
public class UploadController {

    private final CSVHelper csvHelper;
    private final DeliveriesService deliveriesService;

    @Inject
    public UploadController(CSVHelper csvHelper, DeliveriesService deliveriesService) {
        this.csvHelper = csvHelper;
        this.deliveriesService = deliveriesService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String showUploadForm() {
        return "upload";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String uploadFile(@RequestParam("file") @NotNull MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            InputStream inputStream = file.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);
            List<Delivery> deliveries = csvHelper.readDeliveries(isr);
//            Map<LocalDate, List<Delivery>> collect =
//                    deliveries.stream().
//                            filter(d -> d.getDeliveryDate() != null).
//                            collect(Collectors.groupingBy(Delivery::getDeliveryDate));
//            for (Map.Entry<LocalDate, List<Delivery>> entry : collect.entrySet()) {
//                System.out.println("Date : " + entry.getKey());
//                System.out.println(entry.getValue().stream().
//                        collect(Collectors.groupingBy(Delivery::getZip)).
//                        values().
//                        stream().
//                        map(ds -> ds.stream().mapToDouble(Delivery::getVolumeNumber).sum() + "").
//                        collect(Collectors.joining(", ")));
//            }
            deliveriesService.addDeliveries(deliveries);
            return "redirect:deliveries?corrupted=true";
        } else throw new IllegalStateException("File must not be empty");

    }
}
