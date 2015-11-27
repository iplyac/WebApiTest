package com.qa.models.responses;

import com.google.gson.JsonElement;
import com.qa.models.WebApiDataSourceModel;
import com.qa.utils.GsonUtils;

import java.util.ArrayList;
import java.util.List;

public class WebApiQueryResponseModel
    extends WebApiResponseModel
{
    public List<String> getResultsId(){
        List<String> resultsId = new ArrayList<String>();
        if (!isFailed())
        for (JsonElement resultId:getResponseData().getAsJsonObject().get("resultsIds").getAsJsonArray())
            resultsId.add(resultId.getAsString());
        return resultsId;
    }
    
    public List<WebApiDataSourceModel> getDataSources(){
        List<WebApiDataSourceModel> dataSources = new ArrayList<WebApiDataSourceModel>();
        if (isFailed())
        for (JsonElement dataSource:getResponseData().getAsJsonObject().get("dataSources").getAsJsonArray())
             dataSources.add((WebApiDataSourceModel) GsonUtils.fromJsonToObject(dataSource.toString(), WebApiDataSourceModel.class));
        return dataSources;
    }
}

