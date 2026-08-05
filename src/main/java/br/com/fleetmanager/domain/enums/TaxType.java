package br.com.fleetmanager.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaxType {
    IPVA("IPVA"),
    LICENSING("Licenciamento"),
    INSURANCE("Seguro"),
    FINE("Multa"),
    OTHER("Outro");

    private final String label;
}
