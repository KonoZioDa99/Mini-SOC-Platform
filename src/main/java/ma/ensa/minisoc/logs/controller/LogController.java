package ma.ensa.minisoc.logs.controller;

import ma.ensa.minisoc.logs.service.LogSimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LogController {
    @Autowired
    private LogSimulationService simulationService;

    @PostMapping("/start")
    public String startSimulation() {
        simulationService.startSimulation();
        return "redirect:/index";
    }

    @PostMapping("/stop")
    public String stopSimulation() {
        simulationService.stopSimulation();
        return "redirect:/index";
    }
}
