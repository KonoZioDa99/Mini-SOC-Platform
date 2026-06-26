package ma.ensa.minisoc.logs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ma.ensa.minisoc.logs.model.LogEntity;

@Repository
public interface LogRepository extends JpaRepository<LogEntity, Long> {
}
