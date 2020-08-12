package com.softserve.marathon.controller.rest;

import com.softserve.marathon.config.JwtProvider;
import com.softserve.marathon.dto.OperationResponse;
import com.softserve.marathon.dto.TokenResponse;
import com.softserve.marathon.dto.UserRequest;
import com.softserve.marathon.dto.UserResponse;
import com.softserve.marathon.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class AuthenticationController {
    Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping("/registration")
    public ResponseEntity<String> register(
            @RequestParam(value = "login")
                    String login,
            @RequestParam(value = "password")
                    String password) {
        logger.info("**/registration userLogin = " + login);
        if (userService.userExistsEmail(login)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(String.format("User with login %s already exists", login));
        }
        UserRequest userRequest = new UserRequest(login, password);
        userService.saveUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(String.format("Successfully registered %s", login));
    }

    @PostMapping("/auth")
    public TokenResponse signIn(
            @RequestParam(value = "login", required = true)
                    String login,
            @RequestParam(value = "password", required = true)
                    String password) {
        logger.info("**/auth userLogin = " + login);
        UserRequest userRequest = new UserRequest(login, password);
        try {
            UserResponse userResponse = userService.findByLoginAndPassword(userRequest);
            return new TokenResponse(jwtProvider.generateToken(userResponse.getLogin()));
        } catch (RuntimeException e) {
            logger.error(String.format("**/auth login = %s, password = %s",
                    userRequest.getLogin(), userRequest.getPassword()));
            return new TokenResponse("Wrong login or password!");
        }
    }

    @Secured({"ROLE_ADMIN", "ROLE_MENTOR", "ROLE_STUDENT"})
    @GetMapping("/expiration")
    public OperationResponse expirationDate() {
        logger.info("**/expiration");
        return new OperationResponse("Token expiration date is " + userService.getExpirationLocalDate());
    }
}
