package br.com.fleetmanager.web.controller;

import br.com.fleetmanager.exception.BusinessException;
import br.com.fleetmanager.service.DriverService;
import br.com.fleetmanager.web.dto.DriverForm;
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
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class DriverController {
    private final DriverService service;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("drivers", service.findAll());
        return "drivers/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("driverForm", new DriverForm());
        model.addAttribute("editing", false);
        return "drivers/form";
    }

    @PostMapping
    public String create(@Valid DriverForm form, BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("editing", false);
            return "drivers/form";
        }
        try {
            service.create(form);
        } catch (BusinessException ex) {
            result.reject("business", ex.getMessage());
            model.addAttribute("editing", false);
            return "drivers/form";
        }
        redirectAttributes.addFlashAttribute("success", "Motorista cadastrado com sucesso");
        return "redirect:/drivers";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("driverForm", service.toForm(service.findById(id)));
        model.addAttribute("driverId", id);
        model.addAttribute("editing", true);
        return "drivers/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid DriverForm form, BindingResult result,
                         Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("driverId", id);
            model.addAttribute("editing", true);
            return "drivers/form";
        }
        try {
            service.update(id, form);
        } catch (BusinessException ex) {
            result.reject("business", ex.getMessage());
            model.addAttribute("driverId", id);
            model.addAttribute("editing", true);
            return "drivers/form";
        }
        redirectAttributes.addFlashAttribute("success", "Motorista atualizado com sucesso");
        return "redirect:/drivers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("success", "Motorista removido com sucesso");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("error", "O motorista está vinculado a um veículo e não pode ser removido");
        }
        return "redirect:/drivers";
    }
}
