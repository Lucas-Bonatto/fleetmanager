package br.com.fleetmanager.repository;

import br.com.fleetmanager.domain.entity.Tax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TaxRepository extends JpaRepository<Tax, Long> {
    long countByPaidFalseAndDueDateBefore(LocalDate date);

    @Query("select coalesce(sum(t.amount), 0) from Tax t where t.paid = false")
    BigDecimal sumPendingAmount();
}
