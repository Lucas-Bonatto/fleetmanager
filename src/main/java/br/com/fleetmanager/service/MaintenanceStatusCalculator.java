package br.com.fleetmanager.service;

import br.com.fleetmanager.domain.entity.Maintenance;
import br.com.fleetmanager.domain.enums.MaintenanceStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class MaintenanceStatusCalculator {
    private static final int DUE_SOON_KM = 1_000;
    private static final long DUE_SOON_DAYS = 30;

    public MaintenanceStatus calculate(Maintenance maintenance, LocalDate today) {
        int currentKm = maintenance.getVehicle().getCurrentKm();

        boolean overdueByDate = maintenance.getNextDueDate() != null
                && maintenance.getNextDueDate().isBefore(today);
        boolean overdueByKm = maintenance.getNextDueKm() != null
                && maintenance.getNextDueKm() <= currentKm;
        if (overdueByDate || overdueByKm) {
            return MaintenanceStatus.OVERDUE;
        }

        boolean dueSoonByDate = maintenance.getNextDueDate() != null
                && ChronoUnit.DAYS.between(today, maintenance.getNextDueDate()) <= DUE_SOON_DAYS;
        boolean dueSoonByKm = maintenance.getNextDueKm() != null
                && maintenance.getNextDueKm() - currentKm <= DUE_SOON_KM;
        if (dueSoonByDate || dueSoonByKm) {
            return MaintenanceStatus.DUE_SOON;
        }

        return MaintenanceStatus.ON_TIME;
    }
}
