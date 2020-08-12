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
    public OperationResponse register(
            @RequestParam(value = "login", required = true)
                    String login,
            @RequestParam(value = "password", required = true)
                    String password) {
        logger.info("**/registration userLogin = " + login);
        UserRequest userRequest = new UserRequest(login, password);
        return new OperationResponse(String.valueOf(userService.saveUser(userRequest)));
    }

    @PostMapping("/auth")
    public TokenResponse signIn(
            @RequestParam(value = "login", required = true)
                    String login,
            @RequestParam(value = "password", required = true)
                    String password) {
        logger.info("**/auth userLogin = " + login);
        UserRequest userRequest = new UserRequest(login, password);
        UserResponse userResponse = userService.findByLoginAndPassword(userRequest);
        return new TokenResponse(jwtProvider.generateToken(userResponse.getLogin()));
    }

    @GetMapping("/expiration")
    public OperationResponse expirationDate() {
        logger.info("**/expiration");
        return new OperationResponse("Token expiration date is " + userService.getExpirationLocalDate());
    }

}
