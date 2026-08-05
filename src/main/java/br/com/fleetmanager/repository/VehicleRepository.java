package br.com.fleetmanager.repository;

import br.com.fleetmanager.domain.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    boolean existsByPlateIgnoreCase(String plate);

    boolean existsByPlateIgnoreCaseAndIdNot(String plate, Long id);

    long countByStatus(br.com.fleetmanager.domain.enums.VehicleStatus status);
}
