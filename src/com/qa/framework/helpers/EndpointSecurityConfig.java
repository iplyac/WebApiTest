package com.qa.framework.helpers;


import com.reltio.qa.request.Request;

import java.util.ArrayList;
import java.util.List;

public class EndpointSecurityConfig {

    private String endpoint;
    private String httpMethod;

    private class permission
    {
        private String role;
        private boolean access;

        private void setRole(String role) {
            this.role = role;
        }


        private void setAccess(boolean access)
        {
            this.access = access;
        }

        public permission(String role, boolean access)
        {
            setRole(role);
            setAccess(access);
        }

        public String getRole(){return this.role;}
        public boolean getAccess(){return this.access;}
    }

    private List<permission> permissions = new ArrayList();

    public EndpointSecurityConfig(String endpoint)
    {
        this.endpoint = endpoint;
    }

    public EndpointSecurityConfig(String endpoint, Request.Type httpMethod)
    {
        this(endpoint);
        this.httpMethod = getHttpMethod(httpMethod);
    }

    public EndpointSecurityConfig addPermission(String role, boolean access)
    {
        permissions.add(new permission(role, access));
        return this;
    }

    public EndpointSecurityConfig setPermission(String role, boolean access)
    {
        for (permission permission:permissions)
            if (permission.getRole().equals(role))permission.setAccess(access);
        return this;
    }

    public String getHttpMethod(Request.Type httpMethod)
    {
        switch (httpMethod) {
            case POST: return "POST";
            case GET: return "GET";
            case DELETE: return "DELETE";
            case PUT: return "PUT";
            default: return null;
        }
    }

}
