package ma.ensa.minisoc.alerts.service;
import ma.ensa.minisoc.logs.model.LogEntity;
import ma.ensa.minisoc.alerts.model.AlertEntity;
import ma.ensa.minisoc.alerts.model.Severity;

import java.util.*;
import java.util.stream.Collectors;

public class BruteForceDetector {

    private final int threshold; // nombre de tentatives pour déclencher l'alerte
    private final long windowMillis; // fenêtre glissante en ms

    public BruteForceDetector(int threshold, long windowMillis) {
        this.threshold = threshold;
        this.windowMillis = windowMillis;
    }

    /**
     * Détecte les tentatives de brute force dans une liste de logs d'une source donnée
     * @param logs Liste des logs d'une même source
     * @return Une alerte si brute force détecté, sinon null
     */
    public AlertEntity detect(List<LogEntity> logs) {
        if (logs == null || logs.size() < threshold) {
            return null;
        }

        // Trier les logs par date
        List<LogEntity> sortedLogs = logs.stream()
                .sorted(Comparator.comparing(LogEntity::getTimestamp))
                .collect(Collectors.toList());

        for (int i = 0; i < sortedLogs.size(); i++) {
            Date start = sortedLogs.get(i).getTimestamp();
            int count = 1;

            for (int j = i + 1; j < sortedLogs.size(); j++) {
                long diff = sortedLogs.get(j).getTimestamp().getTime() - start.getTime();
                if (diff <= windowMillis) {
                    count++;
                    if (count >= threshold) {
                        // Brute force détecté
                        return createAlert(sortedLogs.get(i).getSource(), sortedLogs.subList(i, j + 1));
                    }
                } else {
                    break; // Fenêtre dépassée
                }
            }
        }
        return null; // Pas de brute force
    }

    /**
     * Crée une alerte basée sur les logs détectés
     */
    private AlertEntity createAlert(String source, List<LogEntity> attackLogs) {
        AlertEntity alert = new AlertEntity();
        alert.setCategory("Brute Force");
        alert.setSource(source);
        alert.setTimestamp(new Date());
        alert.setSeverity(Severity.CRITICAL);
        alert.setMsgAlert("Tentative de brute force détectée depuis la source : " + source);

        // Lier les logs à l'alerte
        for (LogEntity log : attackLogs) {
            log.setAlert(alert);
        }
        alert.setLogs(new ArrayList<>(attackLogs));

        return alert;
    }
}
