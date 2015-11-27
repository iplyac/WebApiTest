package com.qa.models;

public class WebApiRepositoryObjectKeyModel
    extends WebApiBasicObjectModel
{
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public WebApiRepositoryObjectKeyModel(String key) {
        this.key = key;
    }
}

