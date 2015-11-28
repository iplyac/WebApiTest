package com.qa.framework;

import java.util.Map;

public class ServiceSetting {
    private String config;
    private String testList;

    private Map<String, Object> patams;

    public String getConfig() {
        return config;
    }

    public String getTestList() {
        return testList;
    }

    public ServiceSetting(String config, String testList) {
        this.config = config;
        this.testList = testList;
    }
}
