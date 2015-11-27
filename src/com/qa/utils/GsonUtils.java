package com.qa.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class GsonUtils
{
    private static Gson gson;
    private static JsonParser parser;
    
    private static Gson getGson(){
        if (gson == null)gson = new Gson();
        return gson;
    }
    
    private static JsonParser getJsonParser(){
        if (parser == null) parser = new JsonParser();
        return parser;
    }
    
    public static Object fromJsonToObject(String json, Class clazz){
        try{
            return getGson().getAdapter(clazz).fromJson(json);
        }catch(Exception e){
            AssertHelper.fail("An error occurred on serializing json to object of " + clazz.getCanonicalName() + " class. Details:\n" + e.getMessage());
        }
        return null;
    }
    
    public static String fromObjectToJson(Object object){
        return getGson().toJson(object);
    }
    
    public static JsonElement parse(String str){
        return getJsonParser().parse(str);
    }
}

