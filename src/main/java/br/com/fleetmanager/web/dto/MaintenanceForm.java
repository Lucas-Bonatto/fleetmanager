package br.com.fleetmanager.web.dto;

import br.com.fleetmanager.domain.enums.MaintenanceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class MaintenanceForm {
    @NotNull(message = "Selecione o veículo")
    private Long vehicleId;

    @NotNull(message = "Selecione o tipo")
    private MaintenanceType type;

    @NotBlank(message = "Informe a descrição")
    @Size(max = 160)
    private String description;

    private LocalDate performedAt;

    @Min(value = 0, message = "A quilometragem não pode ser negativa")
    private Integer odometerAtService;

    private LocalDate nextDueDate;

    @Min(value = 0, message = "A quilometragem não pode ser negativa")
    private Integer nextDueKm;

    @DecimalMin(value = "0.00", message = "O custo não pode ser negativo")
    private BigDecimal cost;

    @Size(max = 500)
    private String notes;
}
