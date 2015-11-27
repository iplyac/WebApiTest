package com.qa.models;

import com.qa.models.responses.WebApiResponseModel;

public class WebApiAccessTokenModel
    extends WebApiResponseModel
{
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

}

