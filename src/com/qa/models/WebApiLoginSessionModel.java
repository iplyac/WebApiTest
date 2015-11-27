package com.qa.models;

import java.util.List;


public class WebApiLoginSessionModel extends WebApiBasicObjectModel
{
    private WebApiRepositoryStorageModel repositoryStorage;
    private WebApiRepositoryModel repository;
    private WebApiDataSourceModel[] dataSources;
    
    public WebApiRepositoryStorageModel getRepositoryStorage(){
        return repositoryStorage;
    }
    public WebApiRepositoryModel getRepository(){
        return repository;
    }
    public WebApiDataSourceModel[] getDataSources(){
        return dataSources;
    }
    
    public void setRepositoryStorage(WebApiRepositoryStorageModel repositoryStorage) {
        this.repositoryStorage = repositoryStorage;
    }
    public void setRepository(WebApiRepositoryModel repository) {
        this.repository = repository;
    }
    public void setDataSources(WebApiDataSourceModel... dataSources) {
        this.dataSources = dataSources;
    }

    public WebApiLoginSessionModel(WebApiRepositoryStorageModel repositoryStorage, WebApiRepositoryModel repository, WebApiDataSourceModel... dataSources) {
        this.repositoryStorage = repositoryStorage;
        this.repository = repository;
        this.dataSources = dataSources;
    }
}

