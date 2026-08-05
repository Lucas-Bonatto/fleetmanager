package br.com.fleetmanager.service;

import br.com.fleetmanager.domain.entity.Tax;
import br.com.fleetmanager.domain.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TaxStatusCalculatorTest {
    private final TaxStatusCalculator calculator = new TaxStatusCalculator();
    private final LocalDate today = LocalDate.of(2026, 8, 5);

    @Test
    void shouldReturnPaidWhenPaymentWasMade() {
        Tax tax = tax(today.minusDays(10), true);
        assertThat(calculator.calculate(tax, today)).isEqualTo(PaymentStatus.PAID);
    }

    @Test
    void shouldReturnOverdueWhenDueDateHasPassed() {
        Tax tax = tax(today.minusDays(1), false);
        assertThat(calculator.calculate(tax, today)).isEqualTo(PaymentStatus.OVERDUE);
    }

    @Test
    void shouldReturnDueSoonWithinFifteenDays() {
        Tax tax = tax(today.plusDays(15), false);
        assertThat(calculator.calculate(tax, today)).isEqualTo(PaymentStatus.DUE_SOON);
    }

    @Test
    void shouldReturnOnTimeAfterAlertWindow() {
        Tax tax = tax(today.plusDays(16), false);
        assertThat(calculator.calculate(tax, today)).isEqualTo(PaymentStatus.ON_TIME);
    }

    private Tax tax(LocalDate dueDate, boolean paid) {
        Tax tax = new Tax();
        tax.setDueDate(dueDate);
        tax.setPaid(paid);
        return tax;
    }
}
