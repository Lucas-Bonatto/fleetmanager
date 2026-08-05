package br.com.fleetmanager.service;

import br.com.fleetmanager.domain.entity.Tax;
import br.com.fleetmanager.domain.enums.PaymentStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class TaxStatusCalculator {
    private static final long DUE_SOON_DAYS = 15;

    public PaymentStatus calculate(Tax tax, LocalDate today) {
        if (tax.isPaid()) {
            return PaymentStatus.PAID;
        }
        if (tax.getDueDate().isBefore(today)) {
            return PaymentStatus.OVERDUE;
        }
        long days = ChronoUnit.DAYS.between(today, tax.getDueDate());
        return days <= DUE_SOON_DAYS ? PaymentStatus.DUE_SOON : PaymentStatus.ON_TIME;
    }
}
