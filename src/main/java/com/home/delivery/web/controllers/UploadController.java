package com.home.delivery.web.controllers;

import com.home.delivery.app.DeliveriesService;
import com.home.delivery.app.Delivery;
import com.home.delivery.app.ToolsService;
import com.home.delivery.app.io.CSVHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final ToolsService toolsService;

    @Inject
    public UploadController(CSVHelper csvHelper, DeliveriesService deliveriesService, ToolsService toolsService) {
        this.csvHelper = csvHelper;
        this.deliveriesService = deliveriesService;
        this.toolsService = toolsService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String showUploadForm(RedirectAttributes redirectAttributes, Model model) {
        if (redirectAttributes.containsAttribute("showMessage")) model.addAttribute("showMessage", true);
        return "upload";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String uploadFile(@RequestParam("file") @NotNull MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            InputStream inputStream = file.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);
            List<Delivery> deliveries = csvHelper.readDeliveries(isr);
//            Merging of csvs files is no longer supported
            toolsService.resetAll();
            deliveriesService.addDeliveries(deliveries);
            return "redirect:deliveries?corrupted=true";
        } else throw new IllegalStateException("File must not be empty");

    }
}
