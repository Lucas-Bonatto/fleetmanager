package br.com.fleetmanager.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VehicleStatus {
    ACTIVE("Ativo"),
    IN_MAINTENANCE("Em manutenção"),
    INACTIVE("Inativo");

    private final String label;
}
