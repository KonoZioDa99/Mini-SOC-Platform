package ma.ensa.minisoc.alerts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.ensa.minisoc.alerts.model.AlertEntity;

public interface AlertRepository extends JpaRepository<AlertEntity, Long> {
    List<AlertEntity> findBySource(String source);
}
