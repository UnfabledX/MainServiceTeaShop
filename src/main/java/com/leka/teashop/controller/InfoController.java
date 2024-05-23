package com.leka.teashop.controller;

import com.leka.teashop.model.dto.PaymentAndDeliveryInfoDto;
import com.leka.teashop.service.InfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class InfoController {

    private final InfoService infoService;

    @GetMapping("/payment-and-delivery")
    public String getPaymentAndDeliveryInfo (Model model) {
        PaymentAndDeliveryInfoDto infoDto = infoService.getLastContent();
        model.addAttribute("info", infoDto);
        return "payment-and-delivery";
    }

    @PostMapping("/changePaymentAndDeliveryContent")
    public String changePaymentAndDeliveryContent(@Valid @ModelAttribute("info") PaymentAndDeliveryInfoDto infoDto,
                                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("info", infoDto);
            return "payment-and-delivery";
        }
        infoDto = infoService.changeAndSavePaymentAndDeliveryContent(infoDto);
        model.addAttribute("info", infoDto);
        return "payment-and-delivery";
    }



}
