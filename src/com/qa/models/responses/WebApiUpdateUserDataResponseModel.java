package com.qa.models.responses;

import com.qa.models.WebApiDataSourceModel;
import com.qa.utils.GsonUtils;

import java.util.ArrayList;
import java.util.List;

public class WebApiUpdateUserDataResponseModel
    extends WebApiResponseModel
{
    
    public class FailureDetails{
        class dsModel extends WebApiDataSourceModel {
            public dsModel(String name, String type, String login, String password) {
                super(name, type, login, password);
            }
            String errorText;
            public String geterrorText(){return errorText;}
        }
        List<dsModel> dataSources = new ArrayList<dsModel>();
    }    
    
    public FailureDetails getFailureDetails() {
        return (FailureDetails) GsonUtils.fromJsonToObject(getResponseData().getAsJsonObject().get("dataSources").getAsString(), FailureDetails.class);
    }
}

