package com.abechat.server.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateUserForm {
    @NotNull
    @Size(min=3, max=15, message = "Username must be between 3 and 15 characters")
    private String username;

    @NotNull
    @Size(min=3, max=10, message = "Password must be between 3 and 10 characters")
    private String password;

    @NotNull
    @Size(min=3, max=10)
    private String confirmPassword;

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getPassword(){
        return this.password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getConfirmPassword(){
        return this.confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword){
        this.confirmPassword = confirmPassword;
    }

}
