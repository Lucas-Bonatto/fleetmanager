package br.com.fleetmanager.web.controller;

import br.com.fleetmanager.service.DashboardService;
import br.com.fleetmanager.service.MaintenanceService;
import br.com.fleetmanager.service.TaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;
    private final TaxService taxService;
    private final MaintenanceService maintenanceService;

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("summary", dashboardService.summarize());
        model.addAttribute("taxes", taxService.findAllViews().stream().limit(5).toList());
        model.addAttribute("maintenances", maintenanceService.findAllViews().stream().limit(5).toList());
        return "dashboard";
    }
}
