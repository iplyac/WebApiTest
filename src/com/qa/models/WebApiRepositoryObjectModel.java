package com.qa.models;

import com.google.gson.annotations.SerializedName;

public class WebApiRepositoryObjectModel
    extends WebApiBasicObjectModel
{
    private String id;
    private String key;
    private String name;
    private String comment;
    private String created;
    @SerializedName("last Modified")
    private String lastModified;
    @SerializedName("last Used")
    private String lastUsed;
    private String linked;
    private String owner;
    @SerializedName("read Only")
    private String readOnly;
    private String type;
    public String getId() {
        return id;
    }
    public String getKey() {
        return key;
    }
    public String getName() {
        return name;
    }
    public String getComment() {
        return comment;
    }
    public String getCreated() {
        return created;
    }
    public String getLastModified() {
        return lastModified;
    }
    public String getLastUsed() {
        return lastUsed;
    }
    public String getLinked() {
        return linked;
    }
    public String getOwner() {
        return owner;
    }
    public String getReadOnly() {
        return readOnly;
    }
    public String getType() {
        return type;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public void setCreated(String created) {
        this.created = created;
    }
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }
    public void setLastUsed(String lastUsed) {
        this.lastUsed = lastUsed;
    }
    public void setLinked(String linked) {
        this.linked = linked;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public void setReadOnly(String readOnly) {
        this.readOnly = readOnly;
    }
    public void setType(String type) {
        this.type = type;
    }
    
    
}

