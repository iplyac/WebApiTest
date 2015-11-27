package com.qa.models.responses;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.qa.models.WebApiResultSetModel;

public class WebApiResultSetResponseModel
    extends WebApiResponseModel
{
    WebApiResultSetModel.ColumnModel metadata;
    
    public WebApiResultSetModel.ColumnModel getMetadata() {
        return metadata;
    }
    public WebApiResultSetModel.ColumnModel getColumns() {
        return metadata;
    }
    public JsonObject getRows() {
        return getResponseData().getAsJsonObject().get("rows").getAsJsonObject();
    }
    
    public JsonElement getCell(int row, int column){
        return getRows().getAsJsonArray().get(row).getAsJsonArray().get(column).getAsJsonObject();
    }
}

