package com.softserve.marathon.dto;

import javax.validation.constraints.NotEmpty;

public class UserRequest {

    @NotEmpty
    private String login;

    @NotEmpty
    private String password;

    public UserRequest(@NotEmpty String login, @NotEmpty String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}