package com.softserve.marathon.controller.rest;

import com.softserve.marathon.dto.OperationResponse;
import com.softserve.marathon.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
public class UserRestController {
    Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    UserService userService;

    @GetMapping("/token-life")
    public OperationResponse expirationDate() {
        logger.info("**/token-life");
        return new OperationResponse("Token expiration date is " + userService.getExpirationLocalDate());
    }
}
