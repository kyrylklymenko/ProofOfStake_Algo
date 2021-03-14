package com.example.demo.Api;

import com.example.demo.Service.ControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @Autowired
    private ControlService service;

    @PostMapping("/start_election")
    public ResponseEntity<String> startElection (){
        service.initiateElections();
        return ResponseEntity.ok("Election started");
    }

    @PostMapping("/start_controller")
    public ResponseEntity<String> startController(){
        service.startController();
        return ResponseEntity.ok("Controller started");
    }
}





