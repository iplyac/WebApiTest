package com.qa.models;


import com.qa.msc.WebApiProperties;
import com.qa.utils.GsonUtils;
import com.qa.utils.WebApiHelper;

public class WebApiBasicObjectModel
{
    private WebApiHelper webApi;
    
    @Override
    public String toString(){
        return GsonUtils.fromObjectToJson(this);
    }

    public WebApiHelper getWebApi() {
        return (webApi != null)?webApi:new WebApiHelper();
    }

    public void setWebApi(WebApiHelper webApi) {
        this.webApi = webApi;
    }
}