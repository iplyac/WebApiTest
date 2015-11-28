package com.qa.framework.helpers;

import com.google.gson.JsonObject;

public abstract class ReltioObject {

    public static final String URI_KEY = "uri";
    public static final String ATTRIBUTES_KEY = "attributes";
    public static final String ATTRIBUTES_KEY_REF = "attributeURIs";
    public static final String SUCCESSFUL_KEY = "successful";
    public static final String URI_SEPARATOR = "/";

    protected JsonObject jsonData;

    public JsonObject getJsonData() {
        return jsonData;
    }

}
