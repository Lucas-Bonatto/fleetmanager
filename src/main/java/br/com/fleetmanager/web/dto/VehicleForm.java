package br.com.fleetmanager.web.dto;

import br.com.fleetmanager.domain.enums.VehicleStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleForm {
    @NotBlank(message = "Informe a placa")
    @Size(max = 10)
    private String plate;

    @NotBlank(message = "Informe a marca")
    @Size(max = 60)
    private String brand;

    @NotBlank(message = "Informe o modelo")
    @Size(max = 80)
    private String model;

    @NotNull(message = "Informe o ano")
    @Min(value = 1950, message = "Ano inválido")
    @Max(value = 2100, message = "Ano inválido")
    private Integer modelYear;

    @NotNull(message = "Informe a quilometragem")
    @Min(value = 0, message = "A quilometragem não pode ser negativa")
    private Integer currentKm;

    @NotNull(message = "Informe o status")
    private VehicleStatus status = VehicleStatus.ACTIVE;

    private Long driverId;
}
