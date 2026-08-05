package br.com.fleetmanager.web.controller;

import br.com.fleetmanager.domain.enums.VehicleStatus;
import br.com.fleetmanager.exception.BusinessException;
import br.com.fleetmanager.service.DriverService;
import br.com.fleetmanager.service.VehicleService;
import br.com.fleetmanager.web.dto.VehicleForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {
    private final VehicleService service;
    private final DriverService driverService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("vehicles", service.findAll());
        return "vehicles/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("vehicleForm", new VehicleForm());
        loadOptions(model);
        model.addAttribute("editing", false);
        return "vehicles/form";
    }

    @PostMapping
    public String create(@Valid VehicleForm form, BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            loadOptions(model);
            model.addAttribute("editing", false);
            return "vehicles/form";
        }
        try {
            service.create(form);
        } catch (BusinessException ex) {
            result.reject("business", ex.getMessage());
            loadOptions(model);
            model.addAttribute("editing", false);
            return "vehicles/form";
        }
        redirectAttributes.addFlashAttribute("success", "Veículo cadastrado com sucesso");
        return "redirect:/vehicles";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("vehicleForm", service.toForm(service.findById(id)));
        model.addAttribute("vehicleId", id);
        model.addAttribute("editing", true);
        loadOptions(model);
        return "vehicles/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid VehicleForm form, BindingResult result,
                         Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("vehicleId", id);
            model.addAttribute("editing", true);
            loadOptions(model);
            return "vehicles/form";
        }
        try {
            service.update(id, form);
        } catch (BusinessException ex) {
            result.reject("business", ex.getMessage());
            model.addAttribute("vehicleId", id);
            model.addAttribute("editing", true);
            loadOptions(model);
            return "vehicles/form";
        }
        redirectAttributes.addFlashAttribute("success", "Veículo atualizado com sucesso");
        return "redirect:/vehicles";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("success", "Veículo removido com sucesso");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("error", "O veículo possui registros vinculados e não pode ser removido");
        }
        return "redirect:/vehicles";
    }

    private void loadOptions(Model model) {
        model.addAttribute("drivers", driverService.findAll());
        model.addAttribute("statuses", VehicleStatus.values());
    }
}
