package br.com.fleetmanager.web.controller;

import br.com.fleetmanager.domain.enums.MaintenanceType;
import br.com.fleetmanager.exception.BusinessException;
import br.com.fleetmanager.service.MaintenanceService;
import br.com.fleetmanager.service.VehicleService;
import br.com.fleetmanager.web.dto.MaintenanceForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/maintenances")
@RequiredArgsConstructor
public class MaintenanceController {
    private final MaintenanceService service;
    private final VehicleService vehicleService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("maintenances", service.findAllViews());
        return "maintenances/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("maintenanceForm", new MaintenanceForm());
        loadOptions(model);
        model.addAttribute("editing", false);
        return "maintenances/form";
    }

    @PostMapping
    public String create(@Valid MaintenanceForm form, BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            loadOptions(model);
            model.addAttribute("editing", false);
            return "maintenances/form";
        }
        try {
            service.create(form);
        } catch (BusinessException ex) {
            result.reject("business", ex.getMessage());
            loadOptions(model);
            model.addAttribute("editing", false);
            return "maintenances/form";
        }
        redirectAttributes.addFlashAttribute("success", "Manutenção cadastrada com sucesso");
        return "redirect:/maintenances";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("maintenanceForm", service.toForm(service.findById(id)));
        model.addAttribute("maintenanceId", id);
        model.addAttribute("editing", true);
        loadOptions(model);
        return "maintenances/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid MaintenanceForm form, BindingResult result,
                         Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("maintenanceId", id);
            model.addAttribute("editing", true);
            loadOptions(model);
            return "maintenances/form";
        }
        try {
            service.update(id, form);
        } catch (BusinessException ex) {
            result.reject("business", ex.getMessage());
            model.addAttribute("maintenanceId", id);
            model.addAttribute("editing", true);
            loadOptions(model);
            return "maintenances/form";
        }
        redirectAttributes.addFlashAttribute("success", "Manutenção atualizada com sucesso");
        return "redirect:/maintenances";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.delete(id);
        redirectAttributes.addFlashAttribute("success", "Manutenção removida com sucesso");
        return "redirect:/maintenances";
    }

    private void loadOptions(Model model) {
        model.addAttribute("vehicles", vehicleService.findAll());
        model.addAttribute("types", MaintenanceType.values());
    }
}
