package com.qa.framework.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.reltio.qa.exceptions.ReltioObjectException;

public class Attribute {

    public Crosswalk xw;
    public String name;
    public List<String> values;

    public Attribute(Crosswalk xw, String name) {
        this.xw = xw;
        this.name = name;
    }

    public Attribute(Crosswalk xw, String name, String value) {
        this.xw = xw;
        this.name = name;
        this.values = Arrays.asList(value);
    }

    public Attribute addValue(String newValue) {
        if (values == null) {
            values = new ArrayList();
        }
        values.add(newValue);
        return this;
    }

    public String getSingleValue() throws ReltioObjectException {
        if (values == null || values.isEmpty()) {
            throw new ReltioObjectException("There are no values for attribute");
        }
        return values.get(0);
    }

    public String getJsonForPost() {
        JsonArray res = new JsonArray();
        for (String value : values) {
            JsonObject v = new JsonObject();
            v.addProperty("value", value);
            res.add(v);
        }
        return res.toString();
    }

} 