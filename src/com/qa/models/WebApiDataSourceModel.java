package com.qa.models;

public class WebApiDataSourceModel
{
    private String name;
    private String type;
    private String login;
    private String password;
    private String sblogin;
    private String sbpassword;
    
    public String getName(){
        return name;
    }
    public String getType(){
        return type;
    }
    public String getLogin(){
        return login;
    }
    public String getPassword(){
        return password;
    }
    public String getSblogin(){
        return sblogin;
    }
    public String getSbpassword(){
        return sbpassword;
    }
    
    public WebApiDataSourceModel(String name, String type, String login, String password) {
        this(name, type, login, password, null, null);
    }
    
    public WebApiDataSourceModel(String name, String type, String login, String password, String sblogin, String sbpassword) {
        this.name = name;
        this.type = type;
        this.login = login;
        this.password = password;
        this.sblogin = sblogin;
        this.sbpassword = sbpassword;
    }
    
    
}

