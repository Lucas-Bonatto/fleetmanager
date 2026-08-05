package br.com.fleetmanager.service;

import br.com.fleetmanager.web.view.DashboardSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final VehicleService vehicleService;
    private final TaxService taxService;
    private final MaintenanceService maintenanceService;

    @Transactional(readOnly = true)
    public DashboardSummary summarize() {
        return new DashboardSummary(
                vehicleService.count(),
                vehicleService.countActive(),
                taxService.countOverdue(),
                maintenanceService.countUpcoming(),
                taxService.pendingAmount()
        );
    }
}
