package br.com.fleetmanager.service;

import br.com.fleetmanager.domain.entity.Maintenance;
import br.com.fleetmanager.domain.enums.MaintenanceStatus;
import br.com.fleetmanager.exception.BusinessException;
import br.com.fleetmanager.exception.ResourceNotFoundException;
import br.com.fleetmanager.repository.MaintenanceRepository;
import br.com.fleetmanager.web.dto.MaintenanceForm;
import br.com.fleetmanager.web.view.MaintenanceView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceService {
    private final MaintenanceRepository repository;
    private final VehicleService vehicleService;
    private final MaintenanceStatusCalculator calculator;

    @Transactional(readOnly = true)
    public List<MaintenanceView> findAllViews() {
        LocalDate today = LocalDate.now();
        return repository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(item -> new MaintenanceView(item, calculator.calculate(item, today)))
                .sorted(Comparator.comparingInt(view -> statusOrder(view.status())))
                .toList();
    }

    @Transactional(readOnly = true)
    public Maintenance findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manutenção não encontrada"));
    }

    @Transactional(readOnly = true)
    public long countUpcoming() {
        return findAllViews().stream()
                .filter(view -> view.status() == MaintenanceStatus.DUE_SOON || view.status() == MaintenanceStatus.OVERDUE)
                .count();
    }

    @Transactional
    public Maintenance create(MaintenanceForm form) {
        validate(form);
        Maintenance maintenance = new Maintenance();
        copy(form, maintenance);
        return repository.save(maintenance);
    }

    @Transactional
    public Maintenance update(Long id, MaintenanceForm form) {
        validate(form);
        Maintenance maintenance = findById(id);
        copy(form, maintenance);
        return repository.save(maintenance);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    public MaintenanceForm toForm(Maintenance maintenance) {
        MaintenanceForm form = new MaintenanceForm();
        form.setVehicleId(maintenance.getVehicle().getId());
        form.setType(maintenance.getType());
        form.setDescription(maintenance.getDescription());
        form.setPerformedAt(maintenance.getPerformedAt());
        form.setOdometerAtService(maintenance.getOdometerAtService());
        form.setNextDueDate(maintenance.getNextDueDate());
        form.setNextDueKm(maintenance.getNextDueKm());
        form.setCost(maintenance.getCost());
        form.setNotes(maintenance.getNotes());
        return form;
    }

    private void validate(MaintenanceForm form) {
        if (form.getNextDueDate() == null && form.getNextDueKm() == null) {
            throw new BusinessException("Informe o próximo vencimento por data, quilometragem ou ambos");
        }
        if (form.getPerformedAt() != null && form.getNextDueDate() != null
                && form.getNextDueDate().isBefore(form.getPerformedAt())) {
            throw new BusinessException("A próxima revisão não pode ocorrer antes da execução registrada");
        }
        if (form.getOdometerAtService() != null && form.getNextDueKm() != null
                && form.getNextDueKm() <= form.getOdometerAtService()) {
            throw new BusinessException("A próxima quilometragem deve ser maior que a quilometragem da execução");
        }
    }

    private void copy(MaintenanceForm form, Maintenance maintenance) {
        maintenance.setVehicle(vehicleService.findById(form.getVehicleId()));
        maintenance.setType(form.getType());
        maintenance.setDescription(form.getDescription().trim());
        maintenance.setPerformedAt(form.getPerformedAt());
        maintenance.setOdometerAtService(form.getOdometerAtService());
        maintenance.setNextDueDate(form.getNextDueDate());
        maintenance.setNextDueKm(form.getNextDueKm());
        maintenance.setCost(form.getCost());
        maintenance.setNotes(form.getNotes());
    }

    private int statusOrder(MaintenanceStatus status) {
        return switch (status) {
            case OVERDUE -> 0;
            case DUE_SOON -> 1;
            case ON_TIME -> 2;
        };
    }
}
