package com.qa.models.responses;


import com.google.gson.JsonElement;
import com.qa.models.WebApiRepositoryConnectionModel;
import com.qa.utils.GsonUtils;

import java.util.ArrayList;
import java.util.List;

public class WebApiRepositoryConnectionListResponseModel
    extends WebApiResponseModel
{
    
    public List<WebApiRepositoryConnectionModel> getConnecitons(){
        List<WebApiRepositoryConnectionModel> connections = new ArrayList<WebApiRepositoryConnectionModel>();
        for (JsonElement dataSource:getResponseData().getAsJsonArray())
            connections.add((WebApiRepositoryConnectionModel) GsonUtils.fromJsonToObject(dataSource.toString(), WebApiRepositoryConnectionModel.class));

        return connections;
    }
}
