package br.com.fleetmanager.service;

import br.com.fleetmanager.domain.entity.Driver;
import br.com.fleetmanager.exception.BusinessException;
import br.com.fleetmanager.exception.ResourceNotFoundException;
import br.com.fleetmanager.repository.DriverRepository;
import br.com.fleetmanager.web.dto.DriverForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository repository;

    @Transactional(readOnly = true)
    public List<Driver> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @Transactional(readOnly = true)
    public Driver findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Motorista não encontrado"));
    }

    @Transactional
    public Driver create(DriverForm form) {
        if (repository.existsByLicenseNumberIgnoreCase(form.getLicenseNumber().trim())) {
            throw new BusinessException("Já existe um motorista com esta CNH");
        }
        Driver driver = new Driver();
        copy(form, driver);
        return repository.save(driver);
    }

    @Transactional
    public Driver update(Long id, DriverForm form) {
        Driver driver = findById(id);
        if (repository.existsByLicenseNumberIgnoreCaseAndIdNot(form.getLicenseNumber().trim(), id)) {
            throw new BusinessException("Já existe outro motorista com esta CNH");
        }
        copy(form, driver);
        return repository.save(driver);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    public DriverForm toForm(Driver driver) {
        DriverForm form = new DriverForm();
        form.setName(driver.getName());
        form.setLicenseNumber(driver.getLicenseNumber());
        form.setLicenseCategory(driver.getLicenseCategory());
        form.setLicenseExpiry(driver.getLicenseExpiry());
        form.setPhone(driver.getPhone());
        form.setActive(driver.isActive());
        return form;
    }

    private void copy(DriverForm form, Driver driver) {
        driver.setName(form.getName().trim());
        driver.setLicenseNumber(form.getLicenseNumber().trim().toUpperCase());
        driver.setLicenseCategory(form.getLicenseCategory().trim().toUpperCase());
        driver.setLicenseExpiry(form.getLicenseExpiry());
        driver.setPhone(form.getPhone());
        driver.setActive(form.isActive());
    }
}
