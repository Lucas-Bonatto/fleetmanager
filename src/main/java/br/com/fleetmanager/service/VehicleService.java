package br.com.fleetmanager.service;

import br.com.fleetmanager.domain.entity.Vehicle;
import br.com.fleetmanager.domain.enums.VehicleStatus;
import br.com.fleetmanager.exception.BusinessException;
import br.com.fleetmanager.exception.ResourceNotFoundException;
import br.com.fleetmanager.repository.VehicleRepository;
import br.com.fleetmanager.web.dto.VehicleForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository repository;
    private final DriverService driverService;

    @Transactional(readOnly = true)
    public List<Vehicle> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "plate"));
    }

    @Transactional(readOnly = true)
    public Vehicle findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado"));
    }

    @Transactional(readOnly = true)
    public long count() {
        return repository.count();
    }

    @Transactional(readOnly = true)
    public long countActive() {
        return repository.countByStatus(VehicleStatus.ACTIVE);
    }

    @Transactional
    public Vehicle create(VehicleForm form) {
        String plate = normalizePlate(form.getPlate());
        if (repository.existsByPlateIgnoreCase(plate)) {
            throw new BusinessException("Já existe um veículo com esta placa");
        }
        Vehicle vehicle = new Vehicle();
        copy(form, vehicle);
        return repository.save(vehicle);
    }

    @Transactional
    public Vehicle update(Long id, VehicleForm form) {
        Vehicle vehicle = findById(id);
        String plate = normalizePlate(form.getPlate());
        if (form.getCurrentKm() < vehicle.getCurrentKm()) {
            throw new BusinessException("A quilometragem atual não pode ser menor que a já registrada");
        }
        if (repository.existsByPlateIgnoreCaseAndIdNot(plate, id)) {
            throw new BusinessException("Já existe outro veículo com esta placa");
        }
        copy(form, vehicle);
        return repository.save(vehicle);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    public VehicleForm toForm(Vehicle vehicle) {
        VehicleForm form = new VehicleForm();
        form.setPlate(vehicle.getPlate());
        form.setBrand(vehicle.getBrand());
        form.setModel(vehicle.getModel());
        form.setModelYear(vehicle.getModelYear());
        form.setCurrentKm(vehicle.getCurrentKm());
        form.setStatus(vehicle.getStatus());
        form.setDriverId(vehicle.getDriver() == null ? null : vehicle.getDriver().getId());
        return form;
    }

    private void copy(VehicleForm form, Vehicle vehicle) {
        vehicle.setPlate(normalizePlate(form.getPlate()));
        vehicle.setBrand(form.getBrand().trim());
        vehicle.setModel(form.getModel().trim());
        vehicle.setModelYear(form.getModelYear());
        vehicle.setCurrentKm(form.getCurrentKm());
        vehicle.setStatus(form.getStatus());
        vehicle.setDriver(form.getDriverId() == null ? null : driverService.findById(form.getDriverId()));
    }

    private String normalizePlate(String plate) {
        return plate.trim().replace("-", "").toUpperCase();
    }
}
