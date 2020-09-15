package com.softserve.marathon.controller.rest;

import com.softserve.marathon.dto.OperationResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexRestController {
    @GetMapping("/api")
    public OperationResponse welcome() {
        return new OperationResponse("Welcome to Course REST API.");
    }
}
