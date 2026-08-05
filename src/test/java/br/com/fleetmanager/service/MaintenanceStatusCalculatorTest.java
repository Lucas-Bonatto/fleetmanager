package br.com.fleetmanager.service;

import br.com.fleetmanager.domain.entity.Maintenance;
import br.com.fleetmanager.domain.entity.Vehicle;
import br.com.fleetmanager.domain.enums.MaintenanceStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MaintenanceStatusCalculatorTest {
    private final MaintenanceStatusCalculator calculator = new MaintenanceStatusCalculator();
    private final LocalDate today = LocalDate.of(2026, 8, 5);

    @Test
    void shouldReturnOverdueByMileage() {
        Maintenance maintenance = maintenance(50_000, null, 49_900);
        assertThat(calculator.calculate(maintenance, today)).isEqualTo(MaintenanceStatus.OVERDUE);
    }

    @Test
    void shouldReturnDueSoonByDate() {
        Maintenance maintenance = maintenance(50_000, today.plusDays(30), 60_000);
        assertThat(calculator.calculate(maintenance, today)).isEqualTo(MaintenanceStatus.DUE_SOON);
    }

    @Test
    void shouldReturnOnTimeOutsideAlertWindows() {
        Maintenance maintenance = maintenance(50_000, today.plusDays(31), 51_001);
        assertThat(calculator.calculate(maintenance, today)).isEqualTo(MaintenanceStatus.ON_TIME);
    }

    private Maintenance maintenance(int currentKm, LocalDate nextDate, Integer nextKm) {
        Vehicle vehicle = new Vehicle();
        vehicle.setCurrentKm(currentKm);
        Maintenance maintenance = new Maintenance();
        maintenance.setVehicle(vehicle);
        maintenance.setNextDueDate(nextDate);
        maintenance.setNextDueKm(nextKm);
        return maintenance;
    }
}
