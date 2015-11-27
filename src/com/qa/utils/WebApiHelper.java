package com.qa.utils;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.qa.models.*;
import com.qa.models.responses.*;
import com.qa.msc.WebApiProperties;


public class WebApiHelper
{
    private String serviceUrl;
    private WebApiAccessTokenModel token;
    private WebApiRepositoryConnectionModel currentRepositoryConnection;
    private List<WebApiDataSourceModel> dataSources;
    private WebApiLoginSessionModel loginSession;

    public WebApiHelper(){
        this (new WebApiLoginSessionModel(new WebApiRepositoryStorageModel("db2admin", "db2admin")
             ,new WebApiRepositoryModel("db2admin", "db2admin")
             ,new WebApiDataSourceModel("SAMPLE", "RELATIONAL", "db2admin", "db2admin")));
    }

    public WebApiHelper(WebApiLoginSessionModel loginSession){
        this(loginSession, null);
    }

    public WebApiHelper(WebApiLoginSessionModel loginSession, String serviceUrl){
        this.serviceUrl = serviceUrl;
        this.loginSession = loginSession;
    }

    public String getServiceUrl() {
        return (serviceUrl != null)?serviceUrl: WebApiProperties.DEFAULT.getUrl();
    }
    
    private WebApiAccessTokenModel getToken() {
        return (token != null)?token:getAccessToken(getCurrentRepositoryConnection(), loginSession).getAccessToken();
    }

    public WebApiRepositoryConnectionModel getCurrentRepositoryConnection() {
        return (currentRepositoryConnection != null)?
            currentRepositoryConnection
            :new WebApiRepositoryConnectionModel("DB2_NONE", false, false);
    }
    public WebApiLoginSessionModel getCurrentLoginSession() {
        return loginSession;
    }

    public List<WebApiDataSourceModel> getDataSources() {
        return dataSources;
    }
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
    public void setToken(WebApiAccessTokenModel token) {
        this.token = token;
    }
    public void setCurrentRepositoryConnection(WebApiRepositoryConnectionModel currentRepositoryConnection) {
        this.currentRepositoryConnection = currentRepositoryConnection;
    }
    public void setDataSources(List<WebApiDataSourceModel> dataSources) {
        this.dataSources = dataSources;
    }
    
    
    public WebApiRepositoryConnectionListResponseModel getRepositoryConnectionList(){
        return (WebApiRepositoryConnectionListResponseModel)GsonUtils.fromJsonToObject(
            new WebApiRequest(WebApiRequest.HttpMethod.GET, getServiceUrl() + "/repositories").execute(), WebApiRepositoryConnectionListResponseModel.class);
    }

    public WebApiAccessTokenResponseModel getAccessToken(){
        return getAccessToken(getCurrentRepositoryConnection());
    }

    public WebApiAccessTokenResponseModel getAccessToken(WebApiRepositoryConnectionModel repositoryConnection){
        return getAccessToken(repositoryConnection, getCurrentLoginSession());
    }

    public WebApiAccessTokenResponseModel getAccessToken(WebApiRepositoryConnectionModel repositoryConnection, WebApiLoginSessionModel login){
        return (WebApiAccessTokenResponseModel)GsonUtils.fromJsonToObject(
            new WebApiRequest(WebApiRequest.HttpMethod.POST, 
                String.format("%s/repositories/%s/connect", getServiceUrl(), StringUtils.urlEncode(repositoryConnection.getName())),
                login.toString()).execute(), 
                WebApiAccessTokenResponseModel.class);
    }
    
    public WebApiUpdateUserDataResponseModel updateUserData(WebApiDataSourceModel... dataSourcesList){
        return updateUserData(getToken(), getCurrentRepositoryConnection(), dataSourcesList);
    }
    
    public WebApiUpdateUserDataResponseModel updateUserData(WebApiAccessTokenModel accessToken, WebApiRepositoryConnectionModel repositoryConnection, WebApiDataSourceModel... dataSourcesList){
        JsonArray dataSources = new JsonArray();
        for (WebApiDataSourceModel dataSource:dataSourcesList)
            dataSources.add(GsonUtils.parse(GsonUtils.fromObjectToJson(dataSource)));
        JsonObject json = new JsonObject();
        json.add("dataSources", dataSources);
        return (WebApiUpdateUserDataResponseModel)GsonUtils.fromJsonToObject(
            new WebApiRequest(WebApiRequest.HttpMethod.POST, 
                String.format("%s/repositories/%s/updateUserData?accessToken=%s", getServiceUrl(), StringUtils.urlEncode(repositoryConnection.getName()), accessToken.getAccessToken()),
                json.toString()).execute(), 
                WebApiUpdateUserDataResponseModel.class);
    }
    
    public WebApiRepositoryObjectListResponseModel getRepositoryObjectList(WebApiRepositoryObjectKeyModel key){
        return getRepositoryObjectList(getToken(), getCurrentRepositoryConnection(), key);
    }
    
    public WebApiRepositoryObjectListResponseModel getRepositoryObjectList(WebApiAccessTokenModel accessToken, WebApiRepositoryConnectionModel repositoryConnection, WebApiRepositoryObjectKeyModel key){
        return (WebApiRepositoryObjectListResponseModel)GsonUtils.fromJsonToObject(
            new WebApiRequest(WebApiRequest.HttpMethod.POST, 
                String.format("%s/repositories/%s/getChildren?accessToken=%s", getServiceUrl(), StringUtils.urlEncode(repositoryConnection.getName()), accessToken.getAccessToken()),
                key.toString()).execute(), 
                WebApiRepositoryObjectListResponseModel.class);
    }
    
    public WebApiRepositoryObjectInfoResponseModel getRepositoryObjectInfo(WebApiRepositoryObjectKeyModel key){
        return getRepositoryObjectInfo(getToken(), getCurrentRepositoryConnection(), key);
    }
    
    public WebApiRepositoryObjectInfoResponseModel getRepositoryObjectInfo(WebApiAccessTokenModel accessToken, WebApiRepositoryConnectionModel repositoryConnection, WebApiRepositoryObjectKeyModel key){
        return (WebApiRepositoryObjectInfoResponseModel)GsonUtils.fromJsonToObject(
            new WebApiRequest(WebApiRequest.HttpMethod.POST, 
                String.format("%s/repositories/%s/getObjectInfo?accessToken=%s", getServiceUrl(), StringUtils.urlEncode(repositoryConnection.getName()), accessToken.getAccessToken()),
                key.toString()).execute(), 
                WebApiRepositoryObjectInfoResponseModel.class);
    }
    
    public WebApiResponseModel logout(){
        return logout(getToken(), getCurrentRepositoryConnection());
    }
    
    public WebApiResponseModel logout(WebApiAccessTokenModel accessToken, WebApiRepositoryConnectionModel repositoryConnection){
        return (WebApiResponseModel)GsonUtils.fromJsonToObject(
            new WebApiRequest(WebApiRequest.HttpMethod.POST, 
                String.format("%s/repositories/%s/disconnect?accessToken=%s", getServiceUrl(), StringUtils.urlEncode(repositoryConnection.getName()), accessToken.getAccessToken())).execute(),
                WebApiResponseModel.class);
    }
    
    public WebApiQueryResponseModel runQuery(WebApiQueryModel query){
        return runQuery(getToken(), getCurrentRepositoryConnection(), query);
    }
    
    public WebApiQueryResponseModel runQuery(WebApiAccessTokenModel accessToken, WebApiRepositoryConnectionModel repositoryConnection, WebApiQueryModel query){
        return (WebApiQueryResponseModel)GsonUtils.fromJsonToObject(
            new WebApiRequest(WebApiRequest.HttpMethod.POST, 
                String.format("%s/repositories/%s/runObject?accessToken=%s", getServiceUrl(), StringUtils.urlEncode(repositoryConnection.getName()), accessToken.getAccessToken()),
                query.toString()).execute(), 
                WebApiQueryResponseModel.class);
    }

    public WebApiResultSetResponseModel getResultSet(WebApiQueryResponseModel queryResponse){
        return getResultSet(getToken(), getCurrentRepositoryConnection(), queryResponse);
    }
    
    public WebApiResultSetResponseModel getResultSet(WebApiAccessTokenModel accessToken, WebApiRepositoryConnectionModel repositoryConnection, WebApiQueryResponseModel queryResponse){
        return (WebApiResultSetResponseModel)GsonUtils.fromJsonToObject(
            new WebApiRequest(WebApiRequest.HttpMethod.GET, 
                String.format("%s/repositories/%s/results/%s?accessToken=%s", getServiceUrl(), StringUtils.urlEncode(repositoryConnection.getName()), queryResponse.getResultsId().get(0), accessToken.getAccessToken())).execute(),
                WebApiResultSetResponseModel.class);
    }
    
    
}

