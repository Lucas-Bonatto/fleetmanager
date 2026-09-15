package br.com.fleetmanager.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
public class AuthenticationModelAdvice {

    private static final String DEMO_ROLE = "ROLE_DEMO";

    @ModelAttribute
    public void addAccessMode(Model model, Authentication authentication) {
        boolean readOnlyMode = authentication != null
            && authentication.getAuthorities().stream()
                .anyMatch(authority -> DEMO_ROLE.equals(authority.getAuthority()));

        model.addAttribute("readOnlyMode", readOnlyMode);
    }
}
