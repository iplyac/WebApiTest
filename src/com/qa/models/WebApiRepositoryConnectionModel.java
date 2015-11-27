package com.qa.models;

public class WebApiRepositoryConnectionModel
{
    private String name;
    private boolean storageLoginRequired;
    private boolean repositoryLoginRequired;
    
    public String getName(){
        return name;
    }
    public boolean isStorageLoginRequired(){
        return storageLoginRequired;
    }
    public boolean isRepositoryLoginRequired(){
        return repositoryLoginRequired;
    }
    
    public WebApiRepositoryConnectionModel(String name, boolean storageLoginRequired, boolean repositoryLoginRequired) {
        this.name = name;
        this.storageLoginRequired = storageLoginRequired;
        this.repositoryLoginRequired = repositoryLoginRequired;
    }
}

