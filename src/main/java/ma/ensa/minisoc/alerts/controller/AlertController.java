package ma.ensa.minisoc.alerts.controller;

import ma.ensa.minisoc.alerts.service.AlertService;
import ma.ensa.minisoc.logs.model.LogEntity;
import ma.ensa.minisoc.logs.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class AlertController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private LogService logService; // pour récupérer les logs


    @PostMapping("/generate-alerts")
    public String generateAlerts() {
        List<LogEntity> logs = logService.getAllLogs();
        alertService.generateAlerts(logs);
        return "redirect:/dashboard/alerts";
    }
}

