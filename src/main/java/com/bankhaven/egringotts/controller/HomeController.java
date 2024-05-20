package com.bankhaven.egringotts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/auth/home")
    public String home(Model model) {
        // Fetch user account details
        // Add details to the model
        model.addAttribute("message", "Welcome to the home page!");
        return "home"; // Thymeleaf template "home.html"
    }



    // Other methods with business logic
}