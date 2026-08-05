package br.com.fleetmanager.web.view;

import java.math.BigDecimal;

public record DashboardSummary(
        long totalVehicles,
        long activeVehicles,
        long overdueTaxes,
        long upcomingMaintenances,
        BigDecimal pendingTaxAmount
) {
}
