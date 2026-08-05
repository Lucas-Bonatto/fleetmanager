package br.com.fleetmanager.web.dto;

import br.com.fleetmanager.domain.enums.TaxType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class TaxForm {
    @NotNull(message = "Selecione o veículo")
    private Long vehicleId;

    @NotNull(message = "Selecione o tipo")
    private TaxType type;

    @NotBlank(message = "Informe a descrição")
    @Size(max = 140)
    private String description;

    @NotNull(message = "Informe o vencimento")
    private LocalDate dueDate;

    @NotNull(message = "Informe o valor")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal amount;

    private boolean paid;

    @PastOrPresent(message = "A data de pagamento não pode estar no futuro")
    private LocalDate paymentDate;
}
