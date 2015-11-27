package com.qa.models;

public class WebApiRepositoryStorageModel
{
    private String login;
    private String password;
    
    public String getLogin(){
        return login;
    }
    public String getPassword(){
        return password;
    }
    public void setLogin(String login){
        this.login = login;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public WebApiRepositoryStorageModel(String login, String password) {
        this.login = login;
        this.password = password;
    }
}

