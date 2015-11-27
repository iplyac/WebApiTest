package com.qa.models;

import com.qa.models.responses.WebApiQueryResponseModel;

import java.util.HashMap;
import java.util.Map;


public class WebApiQueryModel
    extends WebApiBasicObjectModel
{
    private String key;
    private Map<String, String> params;
    private WebApiDataSourceModel dataSource;
    
    public String getKey() {
        return key;
    }
    public Map<String, String> getParams() {
        return params;
    }
    public WebApiDataSourceModel getDataSource() {
        return dataSource;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public void setParams(Map<String, String> params) {
        this.params = params;
    }
    public void setDataSource(WebApiDataSourceModel dataSource) {
        this.dataSource = dataSource;
    }
    
    public WebApiQueryModel(String key) {
        this(key, null);
    }
    
    public WebApiQueryModel(String key, WebApiDataSourceModel dataSource) {
        this(key, dataSource, null);
    }
    
    public WebApiQueryModel(String key, WebApiDataSourceModel dataSource, Map<String, String> params) {
        params = new HashMap<String, String>();
        this.key = key;
        this.params = params;
        this.dataSource = dataSource;
    }
    
    public void setParam(String param, String value){
        params.put(param, value);
    }
    
    public String getParam(String param){
        return params.get(param);
    }
    
    public WebApiQueryResponseModel run(){
        return getWebApi().runQuery(this);
    }
}

