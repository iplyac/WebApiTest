package com.qa.models.responses;

import com.qa.models.WebApiAccessTokenModel;
import com.qa.utils.GsonUtils;

public class WebApiAccessTokenResponseModel
    extends WebApiResponseModel
{
    public WebApiAccessTokenModel getAccessToken(){
        return (WebApiAccessTokenModel) GsonUtils.fromJsonToObject(getResponseData().getAsJsonObject().toString(), WebApiAccessTokenModel.class);
    }
}

