package ma.ensa.minisoc.dashboard.controller;

import ma.ensa.minisoc.alerts.service.AlertService;
import ma.ensa.minisoc.logs.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsRestController {
    @Autowired
    private LogService logService;

    @Autowired
    private AlertService alertService;

    @GetMapping
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("logCount", logService.totalLogs());
        stats.put("incidentCount", alertService.totalAlerts());
        return stats;
    }
}
