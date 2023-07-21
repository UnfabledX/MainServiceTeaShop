package com.leka.teashop.controller;

import com.leka.teashop.model.Registration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class LoginController {

    @GetMapping("greeting")
    public String greeting(Map<String, Object> model){
        model.put("message", "Hello Oleksii");
        return "greeting";
    }

    @GetMapping("registration")
    public String getRegistration(@ModelAttribute("registration")Registration registration){
        return "registration";
    }

    @PostMapping("registration")
    public String addRegistration(@ModelAttribute("registration")Registration registration){
        System.out.println("Registration: " + registration.getName());
        return "registration";
    }
}
