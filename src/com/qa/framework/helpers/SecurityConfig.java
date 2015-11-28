package com.qa.framework.helpers;

import java.util.ArrayList;
import java.util.List;

public class SecurityConfig {

    private String uri;

    private class permission
    {
        private String role;
        private String filter;
        private List<String> access = new ArrayList();

        private void setRole(String role) {
            this.role = role;
        }

        private void setAccess(String... accesses) {
            this.access.clear();
            for (String access:accesses)
                this.access.add(access);
        }

        private void setFilter(String filter)
        {
            this.filter = filter;
        }

        public permission(String role, String... accesses)
        {
            setRole(role);
            setAccess(accesses);
        }

        public String getRole()
        {
            return this.role;
        }

        public String[] getAccess()
        {
            return this.access.toArray(new String[access.size()]);
        }

        public String getFilter()
        {
            return this.filter;
        }
    }

    private List<permission> permissions = new ArrayList();

    public SecurityConfig(String uri ,String role, String... access)
    {
        this(uri);
        addPermission(role, access);
    }

    public SecurityConfig(String uri)
    {
        setUri(uri);
    }

    public SecurityConfig setUri(String uri)
    {
        this.uri = uri;
        return this;
    }

    public SecurityConfig addPermission(String role, String... access)
    {
        permissions.add(new permission(role, access));
        return this;
    }

    public SecurityConfig setPermission(String role, String... access)
    {
        permission permission = getPermission(role);
        if (permission!=null){
            permission.setAccess(access);
        }else{
            permissions.add(new permission(role, access));
        }
        return this;
    }

    public SecurityConfig setAllPermissions (String role, String permList)
    // permList - permissions delimited with comma: "READ, DELETE, UPDATE"
    {
        String[] aPerms = permList.split(",");
        permissions.clear();
        for (int i=0; i<aPerms.length; i++)
        {
            aPerms[i] = aPerms[i].trim();
        }
        addPermission(role, aPerms);
        return this;
    }

    public SecurityConfig setFilter(String role, String filter)
    {
        for (permission permission:permissions)
            if (permission.getRole().equals(role))permission.setFilter(filter);
        return this;
    }

    public permission getPermission(String role){
        for (permission permission:permissions)
            if (permission.getRole().equals(role))return permission;
        return null;
    }

    public SecurityConfig removePermissions()
    {
        permissions.clear();
        return this;
    }
}
