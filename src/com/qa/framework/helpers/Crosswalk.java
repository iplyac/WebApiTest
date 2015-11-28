package com.qa.framework.helpers;

import java.util.HashMap;
import java.util.Map;

public class Crosswalk {

    public String type;
    public String value;
    public String sourceTable;

    public Crosswalk(String type, String value, String sourceTable) {
        this.type = type;
        this.value = value;
        this.sourceTable = sourceTable;
    }

    public Crosswalk(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public Map<String, String> getPropertyMap() {
        Map<String, String> res = new HashMap();
        res.put("type", type);
        res.put("value", value);
        if (sourceTable != null) {
            res.put("sourceTable", sourceTable);
        }
        return res;
    }

}
