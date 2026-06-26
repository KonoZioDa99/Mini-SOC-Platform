package ma.ensa.minisoc.alerts.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.ensa.minisoc.logs.model.LogEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class AlertEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;
    private String source;
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp ;
    private String msgAlert;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    @OneToMany(mappedBy = "alert", cascade = CascadeType.ALL)
    private List<LogEntity> logs;
}
