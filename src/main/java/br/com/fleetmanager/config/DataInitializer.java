package br.com.fleetmanager.config;

import br.com.fleetmanager.domain.entity.Driver;
import br.com.fleetmanager.domain.entity.Maintenance;
import br.com.fleetmanager.domain.entity.Tax;
import br.com.fleetmanager.domain.entity.Vehicle;
import br.com.fleetmanager.domain.enums.MaintenanceType;
import br.com.fleetmanager.domain.enums.TaxType;
import br.com.fleetmanager.domain.enums.VehicleStatus;
import br.com.fleetmanager.repository.DriverRepository;
import br.com.fleetmanager.repository.MaintenanceRepository;
import br.com.fleetmanager.repository.TaxRepository;
import br.com.fleetmanager.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    @ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
    CommandLineRunner seedData(DriverRepository drivers, VehicleRepository vehicles,
                               TaxRepository taxes, MaintenanceRepository maintenances) {
        return args -> {
            if (vehicles.count() > 0) {
                return;
            }

            Driver ana = driver("Ana Martins", "CNH-102030", "B", LocalDate.now().plusYears(2), "(54) 99999-1010");
            Driver carlos = driver("Carlos Ribeiro", "CNH-405060", "AB", LocalDate.now().plusMonths(8), "(54) 99999-2020");
            drivers.save(ana);
            drivers.save(carlos);

            Vehicle ranger = vehicle("ABC1D23", "Ford", "Ranger", 2023, 48_600, VehicleStatus.ACTIVE, ana);
            Vehicle duster = vehicle("EFG4H56", "Renault", "Duster", 2022, 72_350, VehicleStatus.ACTIVE, carlos);
            Vehicle onix = vehicle("IJK7L89", "Chevrolet", "Onix", 2021, 91_200, VehicleStatus.IN_MAINTENANCE, null);
            vehicles.save(ranger);
            vehicles.save(duster);
            vehicles.save(onix);

            taxes.save(tax(ranger, TaxType.IPVA, "IPVA 2026", LocalDate.now().plusDays(10), "2840.50", false));
            taxes.save(tax(duster, TaxType.LICENSING, "Licenciamento anual", LocalDate.now().minusDays(5), "198.40", false));
            taxes.save(tax(onix, TaxType.INSURANCE, "Seguro total", LocalDate.now().plusDays(45), "3150.00", false));
            Tax paid = tax(ranger, TaxType.INSURANCE, "Seguro total", LocalDate.now().minusMonths(2), "3890.00", true);
            paid.setPaymentDate(LocalDate.now().minusMonths(2).minusDays(3));
            taxes.save(paid);

            maintenances.save(maintenance(ranger, MaintenanceType.TIRE_ROTATION, "Rodízio e balanceamento",
                    LocalDate.now().minusMonths(4), 40_000, LocalDate.now().plusDays(20), 50_000, "240.00"));
            maintenances.save(maintenance(duster, MaintenanceType.TRANSMISSION, "Diagnóstico preventivo do câmbio CVT",
                    LocalDate.now().minusMonths(8), 60_000, LocalDate.now().minusDays(2), 72_000, "680.00"));
            maintenances.save(maintenance(onix, MaintenanceType.OIL_CHANGE, "Troca de óleo e filtros",
                    LocalDate.now().minusMonths(5), 82_000, LocalDate.now().plusMonths(2), 92_000, "390.00"));
        };
    }

    private Driver driver(String name, String license, String category, LocalDate expiry, String phone) {
        Driver item = new Driver();
        item.setName(name);
        item.setLicenseNumber(license);
        item.setLicenseCategory(category);
        item.setLicenseExpiry(expiry);
        item.setPhone(phone);
        item.setActive(true);
        return item;
    }

    private Vehicle vehicle(String plate, String brand, String model, int year, int km,
                            VehicleStatus status, Driver driver) {
        Vehicle item = new Vehicle();
        item.setPlate(plate);
        item.setBrand(brand);
        item.setModel(model);
        item.setModelYear(year);
        item.setCurrentKm(km);
        item.setStatus(status);
        item.setDriver(driver);
        return item;
    }

    private Tax tax(Vehicle vehicle, TaxType type, String description, LocalDate dueDate,
                    String amount, boolean paid) {
        Tax item = new Tax();
        item.setVehicle(vehicle);
        item.setType(type);
        item.setDescription(description);
        item.setDueDate(dueDate);
        item.setAmount(new BigDecimal(amount));
        item.setPaid(paid);
        return item;
    }

    private Maintenance maintenance(Vehicle vehicle, MaintenanceType type, String description,
                                    LocalDate performedAt, int serviceKm, LocalDate nextDate,
                                    int nextKm, String cost) {
        Maintenance item = new Maintenance();
        item.setVehicle(vehicle);
        item.setType(type);
        item.setDescription(description);
        item.setPerformedAt(performedAt);
        item.setOdometerAtService(serviceKm);
        item.setNextDueDate(nextDate);
        item.setNextDueKm(nextKm);
        item.setCost(new BigDecimal(cost));
        return item;
    }
}
