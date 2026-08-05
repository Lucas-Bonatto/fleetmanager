package br.com.fleetmanager.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MaintenanceStatus {
    OVERDUE("Vencida"),
    DUE_SOON("Próxima"),
    ON_TIME("Em dia");

    private final String label;
}
