package com.qa.msc;

public enum WebApiProperties
{
    DEFAULT("http://ams-vm-qmf01:8081/QMFWebSphere112/api/v1");
    
    private final String url;
    private WebApiProperties(String url){
        this.url = url;
    }
    
    public String getUrl(){
        return url;
    }
}

