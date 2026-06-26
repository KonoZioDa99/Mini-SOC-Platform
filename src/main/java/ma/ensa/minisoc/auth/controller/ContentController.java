package ma.ensa.minisoc.auth.controller;

import ma.ensa.minisoc.auth.model.AppUser;
import ma.ensa.minisoc.logs.service.LogSimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContentController {
    @Autowired
    private LogSimulationService simulationService;

    @GetMapping("/req/login")
    public String login(){
        return "login";
    }

    @GetMapping("/req/signup")
    public String signup(){
        return "signup";
    }

    @GetMapping("/index")
    public String home(Model model){
        model.addAttribute("isRunning", simulationService.isRunning());
        return "index";
    }
}
