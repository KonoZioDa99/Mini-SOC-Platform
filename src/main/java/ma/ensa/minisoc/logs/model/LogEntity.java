package ma.ensa.minisoc.logs.model;

import java.util.Date;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.ensa.minisoc.alerts.model.AlertEntity;

@Entity
@Table(name = "log_entries")
@Getter
@Setter
@NoArgsConstructor
public class LogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    private String message;
    private String source;
    private String type;

    @ManyToOne
    @JoinColumn(name = "alert_id")
    private AlertEntity alert;
}