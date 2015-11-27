package com.qa.models.responses;

import com.google.gson.JsonElement;
import com.qa.models.WebApiRepositoryObjectModel;
import com.qa.utils.GsonUtils;

import java.util.ArrayList;
import java.util.List;

public class WebApiRepositoryObjectListResponseModel
    extends WebApiResponseModel
{

    public List<WebApiRepositoryObjectModel> getChildren(){
        List<WebApiRepositoryObjectModel> childrens = new ArrayList<WebApiRepositoryObjectModel>();
        if (!isFailed())
        for (JsonElement children:getResponseData().getAsJsonObject().get("children").getAsJsonArray())
            childrens.add((WebApiRepositoryObjectModel) GsonUtils.fromJsonToObject(children.toString(), WebApiRepositoryObjectModel.class));
        
        return childrens;
    }
}

