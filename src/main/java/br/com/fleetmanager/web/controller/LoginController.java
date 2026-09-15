package br.com.fleetmanager.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    private final boolean demoEnabled;

    public LoginController(
        @Value("${app.security.demo.enabled:false}") boolean demoEnabled
    ) {
        this.demoEnabled = demoEnabled;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("demoEnabled", demoEnabled);
        return "login";
    }
}
