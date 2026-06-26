package ma.ensa.minisoc.dashboard.controller;

import ma.ensa.minisoc.alerts.model.AlertEntity;
import ma.ensa.minisoc.alerts.service.AlertService;
import ma.ensa.minisoc.logs.model.LogEntity;
import ma.ensa.minisoc.logs.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    private LogService logService;

    @Autowired
    private AlertService alertService;

    @GetMapping("/logs")
    public String showLogs(Model model){
        List<LogEntity> logs = logService.getAllLogs();
        model.addAttribute("logs", logs);
        return "dashboard/logs";
    }

    @GetMapping("/alerts")
    public String showAlerts(Model model){
        List<AlertEntity> alerts = alertService.getAllAlerts();
        model.addAttribute("alerts", alerts);
        return "dashboard/alerts";
    }
}
