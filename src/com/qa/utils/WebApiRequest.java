package com.qa.utils;

public class WebApiRequest
{
    public enum HttpMethod{
        POST, GET, DELETE, PUT
    }
    
    private HttpMethod httpMethod;
    private String serviceUrl;
    private String body;
    
    
    public HttpMethod getHttpMethod(){
        return httpMethod;
    }
    public String getServiceUrl(){
        return serviceUrl;
    }
    public String getBody(){
        return body;
    }
    public void setHttpMethod(HttpMethod httpMethod){
        this.httpMethod = httpMethod;
    }
    public void setServiceUrl(String serviceUrl){
        this.serviceUrl = serviceUrl;
    }
    public void setBody(String body){
        this.body = body;
    }
    
    public WebApiRequest(HttpMethod httpMethod, String serviceUrl){
        this(httpMethod, serviceUrl, null);
    }
    
    
    public WebApiRequest(HttpMethod httpMethod, String serviceUrl, String body) {
        this.httpMethod = httpMethod;
        this.serviceUrl = serviceUrl;
        this.body = body;
    }
    
    public String execute(){
        String response = "";
        switch (httpMethod){
            case GET: response = HttpServiceUtils.doGet(getServiceUrl());break;
            case POST: response = HttpServiceUtils.doPost(getServiceUrl(), getBody());break;
            default: break;
        }
        return response;
    }
}

