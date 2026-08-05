package br.com.fleetmanager.web.view;

import br.com.fleetmanager.domain.entity.Maintenance;
import br.com.fleetmanager.domain.enums.MaintenanceStatus;

public record MaintenanceView(Maintenance maintenance, MaintenanceStatus status) {
}
