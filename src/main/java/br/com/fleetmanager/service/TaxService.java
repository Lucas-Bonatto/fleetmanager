package br.com.fleetmanager.service;

import br.com.fleetmanager.domain.entity.Tax;
import br.com.fleetmanager.domain.enums.PaymentStatus;
import br.com.fleetmanager.exception.BusinessException;
import br.com.fleetmanager.exception.ResourceNotFoundException;
import br.com.fleetmanager.repository.TaxRepository;
import br.com.fleetmanager.web.dto.TaxForm;
import br.com.fleetmanager.web.view.TaxView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxService {
    private final TaxRepository repository;
    private final VehicleService vehicleService;
    private final TaxStatusCalculator calculator;

    @Transactional(readOnly = true)
    public List<TaxView> findAllViews() {
        LocalDate today = LocalDate.now();
        return repository.findAll(Sort.by(Sort.Direction.ASC, "dueDate"))
                .stream()
                .map(tax -> new TaxView(tax, calculator.calculate(tax, today)))
                .sorted(Comparator
                        .comparingInt((TaxView view) -> statusOrder(view.status()))
                        .thenComparing(view -> view.tax().getDueDate()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Tax findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tributo não encontrado"));
    }

    @Transactional(readOnly = true)
    public long countOverdue() {
        return repository.countByPaidFalseAndDueDateBefore(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public BigDecimal pendingAmount() {
        return repository.sumPendingAmount();
    }

    @Transactional
    public Tax create(TaxForm form) {
        validatePayment(form);
        Tax tax = new Tax();
        copy(form, tax);
        return repository.save(tax);
    }

    @Transactional
    public Tax update(Long id, TaxForm form) {
        validatePayment(form);
        Tax tax = findById(id);
        copy(form, tax);
        return repository.save(tax);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    public TaxForm toForm(Tax tax) {
        TaxForm form = new TaxForm();
        form.setVehicleId(tax.getVehicle().getId());
        form.setType(tax.getType());
        form.setDescription(tax.getDescription());
        form.setDueDate(tax.getDueDate());
        form.setAmount(tax.getAmount());
        form.setPaid(tax.isPaid());
        form.setPaymentDate(tax.getPaymentDate());
        return form;
    }

    private void validatePayment(TaxForm form) {
        if (form.isPaid() && form.getPaymentDate() == null) {
            throw new BusinessException("Informe a data de pagamento para um tributo pago");
        }
        if (!form.isPaid()) {
            form.setPaymentDate(null);
        }
    }

    private int statusOrder(PaymentStatus status) {
        return switch (status) {
            case OVERDUE -> 0;
            case DUE_SOON -> 1;
            case ON_TIME -> 2;
            case PAID -> 3;
        };
    }

    private void copy(TaxForm form, Tax tax) {
        tax.setVehicle(vehicleService.findById(form.getVehicleId()));
        tax.setType(form.getType());
        tax.setDescription(form.getDescription().trim());
        tax.setDueDate(form.getDueDate());
        tax.setAmount(form.getAmount());
        tax.setPaid(form.isPaid());
        tax.setPaymentDate(form.getPaymentDate());
    }
}
