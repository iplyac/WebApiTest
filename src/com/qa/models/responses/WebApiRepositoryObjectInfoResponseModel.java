package com.qa.models.responses;

import com.qa.models.WebApiRepositoryObjectModel;
import com.qa.utils.GsonUtils;

public class WebApiRepositoryObjectInfoResponseModel
    extends WebApiResponseModel
{
    public WebApiRepositoryObjectModel getInfo(){
        return (WebApiRepositoryObjectModel) GsonUtils.fromJsonToObject(getResponseData().getAsJsonObject().get("properties").toString(), WebApiRepositoryObjectModel.class);
    }
}

