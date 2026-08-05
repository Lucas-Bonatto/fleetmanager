package br.com.fleetmanager.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DriverForm {
    @NotBlank(message = "Informe o nome do motorista")
    @Size(max = 120)
    private String name;

    @NotBlank(message = "Informe o número da CNH")
    @Size(max = 20)
    private String licenseNumber;

    @NotBlank(message = "Informe a categoria")
    @Size(max = 5)
    private String licenseCategory;

    @NotNull(message = "Informe a validade da CNH")
    private LocalDate licenseExpiry;

    @Size(max = 30)
    private String phone;

    private boolean active = true;
}
