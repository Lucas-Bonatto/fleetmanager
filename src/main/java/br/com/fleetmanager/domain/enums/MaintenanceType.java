package br.com.fleetmanager.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MaintenanceType {
    OIL_CHANGE("Troca de óleo"),
    TIRE_ROTATION("Rodízio de pneus"),
    BRAKES("Freios"),
    TRANSMISSION("Câmbio / transmissão"),
    FLUIDS("Troca de fluidos"),
    ALIGNMENT("Alinhamento e balanceamento"),
    INSPECTION("Revisão geral"),
    OTHER("Outro");

    private final String label;
}
