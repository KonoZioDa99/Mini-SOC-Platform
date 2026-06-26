package ma.ensa.minisoc.alerts.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ma.ensa.minisoc.alerts.model.Severity;
import ma.ensa.minisoc.alerts.model.AlertEntity;
import ma.ensa.minisoc.alerts.repository.AlertRepository;
import ma.ensa.minisoc.logs.model.LogEntity;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    /**
     * Génère les alertes à partir d'une liste de logs.
     */
    public void generateAlerts(List<LogEntity> logs) {
        System.out.println("Nombre total de logs reçus: " + logs.size());

        // Regrouper les logs par source (IP ou user)
        Map<String, List<LogEntity>> logsBySource = logs.stream()
                .collect(Collectors.groupingBy(LogEntity::getSource));

        // Initialiser le détecteur
        BruteForceDetector detector = new BruteForceDetector(5, 1200000); // ex. 5 tentatives en 120 sec

        // Ensemble pour stocker les IDs des logs déjà utilisés pour brute force
        Set<Long> logsInBruteForce = new HashSet<>();

        for (Map.Entry<String, List<LogEntity>> entry : logsBySource.entrySet()) {
            AlertEntity alert = detector.detect(entry.getValue());
            if (alert != null) {
                alertRepository.save(alert);
                System.out.println("Alerte brute force sauvegardée pour: " + alert.getSource());

                // Ajouter les logs utilisés dans cette alerte brute force
                for (LogEntity log : alert.getLogs()) {
                    logsInBruteForce.add(log.getId());
                }
            } else {
                System.out.println("Pas de brute force détecté pour la source: " + entry.getKey());
            }
        }

        // Traiter les logs restants (non inclus dans brute force)
        generateIndividualAlerts(logs, logsInBruteForce);
    }





    private void generateIndividualAlerts(List<LogEntity> logs, Set<Long> logsInBruteForce) {
        List<LogEntity> logsToProcess = logs.stream()
                .filter(log -> !logsInBruteForce.contains(log.getId()))
                .collect(Collectors.toList());

        System.out.println("Nombre de logs hors brute force à traiter: " + logsToProcess.size());

        for (LogEntity log : logsToProcess) {
            System.out.println("Création d'une alerte individuelle pour le log ID: " + log.getId());

            AlertEntity alert = buildAlertForLog(log);

            // Sauvegarder l'alerte et le log
            alertRepository.save(alert);

            System.out.println("Alerte sauvegardée pour le log ID: " + log.getId());
        }
        System.out.println("Génération des alertes terminée !");
    }


    private AlertEntity buildAlertForLog(LogEntity log) {
        AlertEntity alert = new AlertEntity();
        alert.setTimestamp(log.getTimestamp());
        alert.setSource(log.getSource());
        alert.setCategory(determineCategory(log));
        alert.setMsgAlert(determineAlertMessage(log));
        alert.setSeverity(determineSeverity(log));
        alert.setLogs(Collections.singletonList(log));

        // Lier le log à l'alerte
        log.setAlert(alert);

        return alert;
    }

    public static Severity determineSeverity(LogEntity log) {
        String message = log.getMessage().toLowerCase();

        if (message.contains("ssh2")) {
            return Severity.WARNING;
        } else if (message.contains("ftpd")) {
            return Severity.WARNING;
        } else if (message.contains("scan")) {
            return Severity.MEDIUM;
        } else if ("networkError".equalsIgnoreCase(log.getType())) {
            if (message.contains("connection reset")) {
                return Severity.LOW;
            } else if (message.contains("destination unreachable")) {
                return Severity.LOW;
            } else if (message.contains("dropped (in=eth0 out=)")) {
                return Severity.MEDIUM;
            } else if (message.contains("syn flood")) {
                return Severity.CRITICAL;
            } else if (message.contains("dhcprequest")) {
                return Severity.LOW;
            } else if (message.contains("can't synchronise")) {
                return Severity.LOW;
            } else if (message.contains("link down")) {
                return Severity.MEDIUM;
            } else if (message.contains("crc errors")) {
                return Severity.MEDIUM;
            } else if (message.contains("authentication timeout")) {
                return Severity.MEDIUM;
            } else if (message.contains("too many connections")) {
                return Severity.HIGH;
            } else if (message.contains("connection timed")) {
                return Severity.LOW;
            } else if (message.contains("tls handshake failed")) {
                return Severity.MEDIUM;
            } else if (message.contains("nmap os detection")) {
                return Severity.MEDIUM;
            } else if (message.contains("flip-flop detected")) {
                return Severity.MEDIUM;
            } else {
                return Severity.INFO;
            }
        } else {
            return Severity.INFO;
        }
    }

    public String determineCategory(LogEntity log) {
        String message = log.getMessage().toLowerCase();

        if (message.contains("ssh2")) {
            return "Failed SSH Login";
        } else if (message.contains("ftpd")) {
            return "Failed FTP Login";
        } else if (message.contains("scan")) {
            return "Port Scanning";
        } else {
            return log.getType();
        }
    }

    public static String determineAlertMessage(LogEntity log) {
        String message = log.getMessage().toLowerCase();

        if (message.contains("ssh2")) {
            return "Tentative de connexion SSH échouée détectée";
        } else if (message.contains("ftpd")) {
            return "Tentative de connexion FTP échouée détectée";
        } else if (message.contains("scan")) {
            return "Activité suspecte de scan de ports détectée";
        } else if ("networkError".equalsIgnoreCase(log.getType())) {
            if (message.contains("connection reset")) {
                return "Réinitialisation de connexion TCP par le pair détectée";
            } else if (message.contains("destination unreachable")) {
                return "Paquet ICMP destination inaccessible détecté";
            } else if (message.contains("dropped (in=eth0 out=)")) {
                return "Paquet TCP bloqué par le pare-feu";
            } else if (message.contains("syn flood")) {
                return "Attaque SYN flood possible détectée sur le port 80";
            } else if (message.contains("dhcprequest")) {
                return "Demande DHCP refusée : plus de baux disponibles";
            } else if (message.contains("can't synchronise")) {
                return "Synchronisation NTP impossible : serveurs injoignables";
            } else if (message.contains("link down")) {
                return "Lien réseau eth0 perdu (carrier down)";
            } else if (message.contains("crc errors")) {
                return "Erreurs CRC détectées sur le port 7 du switch";
            } else if (message.contains("authentication timeout")) {
                return "Timeout d'authentification client WLAN détecté";
            } else if (message.contains("too many connections")) {
                return "Nombre maximal de connexions atteint sur haproxy";
            } else if (message.contains("connection timed")) {
                return "Timeout de connexion SMTP détecté";
            } else if (message.contains("tls handshake failed")) {
                return "Échec de la négociation TLS OpenVPN détecté";
            } else if (message.contains("nmap os detection")) {
                return "Tentative de scan NMAP détectée";
            } else if (message.contains("flip-flop detected")) {
                return "Changement d'association MAC-IP suspect détecté";
            } else {
                return "Erreur réseau non identifiée";
            }
        } else {
            return "Erreur réseau non identifiée";
        }
    }

    public List<AlertEntity> getAllAlerts() {
        return alertRepository.findAll();
    }

    public Long totalAlerts()
    {
        return alertRepository.count();
    }
}
