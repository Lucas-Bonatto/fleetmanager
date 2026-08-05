package br.com.fleetmanager.repository;

import br.com.fleetmanager.domain.entity.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {}
