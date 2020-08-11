package com.softserve.marathon.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexRestController {
    @GetMapping("/api")
    public String welcome() {
        return "Welcome to Marathon REST API.";
    }
}
