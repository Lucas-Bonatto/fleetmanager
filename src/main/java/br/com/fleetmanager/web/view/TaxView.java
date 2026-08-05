package br.com.fleetmanager.web.view;

import br.com.fleetmanager.domain.entity.Tax;
import br.com.fleetmanager.domain.enums.PaymentStatus;

public record TaxView(Tax tax, PaymentStatus status) {
}
