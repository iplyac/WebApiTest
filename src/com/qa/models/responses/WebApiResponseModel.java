package com.qa.models.responses;

import com.google.gson.JsonElement;
import com.qa.utils.AssertHelper;

public class WebApiResponseModel
{
    private String status;
    private String error;
    private String errorText;
    private JsonElement data;
    
    public String getError() {
        return error;
    }
    public String getErrorText() {
        return errorText;
    }
    
    public String getStatus(){
        return status;
    }
    
    public boolean isFailed(){
        return getStatus().equals("failed");
    }
    
    public void assertSuccess(){
        AssertHelper.assertTrue(!isFailed());
    }
    
    public void assertFailed(){
        AssertHelper.assertTrue(isFailed());
    }
    
    public JsonElement getResponseData(){
        return data;
    }
}