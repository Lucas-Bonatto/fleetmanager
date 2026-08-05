package br.com.fleetmanager.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    PAID("Pago"),
    OVERDUE("Atrasado"),
    DUE_SOON("Vencimento próximo"),
    ON_TIME("No prazo");

    private final String label;
}
