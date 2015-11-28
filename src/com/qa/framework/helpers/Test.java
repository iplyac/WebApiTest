package com.qa.framework.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.qa.framework.exceptions.FatalTestException;
import com.qa.framework.exceptions.StepException;
import com.qa.framework.exceptions.TestException;
import com.reltio.qa.Config;
import com.reltio.qa.exceptions.*;
import com.reltio.qa.json.JsonDiff;
import com.reltio.qa.model.EntityModel;
import com.reltio.qa.request.Request;
import com.reltio.qa.utils.GsonUtils;
import com.reltio.qa.utils.IOUtils;
import com.reltio.qa.utils.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Test {

    private static final Logger logger = Logger.getLogger(Test.class);

    private static final long MAX_WAIT_TIME = 300000;
    private static final long QUEUE_WAIT_INTERVAL = 300;

    private static final String STARTVAR = "\\{\\{";
    private static final String ENDVAR = "\\}\\}";

    private static final int DEFAULT_SLEEP = 500; //ms
    private static final int DEFAULT_TRIES = 5; //ms

    protected Map<String, Object> vars;
    protected String dataFolder;

    private boolean cleanTenant;
    private boolean updateConfig;
    private boolean useDrive;
    private String tenantName;
    private String datatenantName;

    public boolean useDrive(){return useDrive;}
    public void setUseDrive(boolean useDrive){this.useDrive = useDrive;}
    public boolean needToCleanTenant() {
        return cleanTenant;
    }
    public void setCleanTenant(boolean cleanTenant) {this.cleanTenant = cleanTenant;}
    public boolean needToUpdateConfig(){return updateConfig;}
    public void setUpdateConfig(boolean updateConfig){this.updateConfig = updateConfig;}
    public void setTenantName(String tenantName){this.tenantName = tenantName;}
    public String getTenantName(){return this.tenantName;}

    public void setDatatenantName(String tenantName){this.datatenantName = tenantName;}
    public String getDatatenantName(){return this.datatenantName;}

    public String loadFile(String fileName) throws IOException {
        return loadFile(fileName, true);
    }

    public String loadFile(String fileName, boolean isJson) throws IOException {
        String data;
        if(fileName.startsWith("resources:")) {
            String filePath = fileName.substring(10, fileName.length());
            data = FileUtils.readFileToString(new File(getClass().getClassLoader().getResource(filePath).getFile()));
        } else if (dataFolder.startsWith("resources:")) {
            String resourcePath = dataFolder.substring(10, dataFolder.length());
            data = FileUtils.readFileToString(new File(getClass().getClassLoader().getResource(resourcePath + fileName).getFile()));
        } else {
            data = IOUtils.readFromFile(dataFolder + fileName);
        }
        if (isJson) {
            data = GsonUtils.getGson().toJson(GsonUtils.getJsonParser().parse(data));
        }
        return parseString(data);
    }

    public void saveFile(String fileName, String data) throws IOException{
        String path = dataFolder;
        String fullpath;
        if (dataFolder.startsWith("resources:")) {
            path = dataFolder.substring(10, dataFolder.length());
        }
        fullpath = getClass().getClassLoader().getResource(path + fileName).getFile();
        File f = new File(fullpath);
        IOUtils.saveToFile(f, data);
    }

    public String parseString(String str) {
        for (Map.Entry<String, Object> var : vars.entrySet()) {
            str = str.replaceAll(STARTVAR + var.getKey() + ENDVAR, var.getValue().toString());
        }
        return str;
    }

    public String unparseString(String str) {
        for (Map.Entry<String, Object> var : vars.entrySet()) {
            str = str.replaceAll(var.getValue().toString(), STARTVAR + var.getKey() + ENDVAR);
        }
        return str;
    }

    public JsonElement loadJson(String fileName) throws IOException {
        return GsonUtils.getJsonParser().parse(loadFile(fileName));
    }

    public void setVars(Map<String, Object> vars) {
        this.vars = vars;
    }

    public void setDataFolder(String dataFolder) {
        if (!dataFolder.endsWith("/")) dataFolder += "/";
        this.dataFolder = dataFolder;
    }

    protected String getLastPartOfPath(String path, String separator) {
        return path.substring(path.lastIndexOf(separator) + 1, path.length());
    }

    protected boolean getBooleanVar(String varKey) {
        boolean result = false;
        if (vars.containsKey(varKey)) result = Boolean.valueOf(vars.get(varKey).toString());
        return result;
    }

    protected String getStringVar(String varKey) {
        return vars.get(varKey).toString();
    }

    //post entity from entityJson string and return uri
    protected String postEntity(String entityJson) throws CommandException {
        return postEntity(entityJson, null);
    }
    protected String postEntity(String entityJson, String username) throws CommandException {
        return postObject(entityJson, "entities", username);
    }
    protected String postEntity(String entityJson, String username,String activityId) throws  CommandException{
        return postObject(entityJson, "entities", username, activityId);
    };
/*    protected String updateEntity(String entityURI)throws CommandException {
        return
    }

    protected String updateEntity(String entityURI, String username)throws CommandException {
        return postObject(entityJson, "entities", username);
    }*/

    //post category from categoryJson string and return uri
    protected String postCategory(String categoryJson) throws CommandException {
        return postCategory(categoryJson, null);
    }
    protected String postCategory(String categoryJson, String username) throws CommandException {
        return postObject(categoryJson, "categories", username);
    }

    //post entity from relationJson string and return uri
    protected String postRelation(String relationJson) throws CommandException {
        return postRelation(relationJson, null);
    }
    protected String postRelation(String relationJson, String username) throws CommandException {
        return postObject(relationJson, "relations", username);
    }

    protected void putTenantConfiguration(String config)throws CommandException{
        new Request(null, Request.Type.PUT, parseString("{{tenantUrl}}/") + "configuration", config).execute();
    }
    protected String postRelation(String relationJson, String username, String activityId) throws CommandException {
        return postObject(relationJson, "relations", username, activityId);
    }

    //post security configuration and return
    protected String postPermissions(SecurityConfig... security)throws CommandException {
        return new Request(null, Request.Type.POST, parseString("{{service_uri}}/permissions/{{tenant_name}}"), GsonUtils.getGson().toJson(security)).execute();
    }

    protected String postEndpointPermissions(EndpointSecurityConfig... security)throws CommandException {
        return new Request(null, Request.Type.POST, parseString("{{service_uri}}/access/{{tenant_name}}"), GsonUtils.getGson().toJson(security)).execute();
    }

    protected String deletePermissions()throws CommandException{
        return new Request(null, Request.Type.DELETE, parseString("{{service_uri}}/permissions/{{tenant_name}}")).execute();
    }
    protected String deletePermissions(String tenant)throws CommandException{
        return new Request(null, Request.Type.DELETE, parseString("{{service_uri}}/permissions/"+tenant)).execute();
    }

    protected String deleteEndpointPermissions()throws CommandException{
        return new Request(null, Request.Type.DELETE, parseString("{{service_uri}}/access/{{tenant_name}}")).execute();
    }

    protected String deleteEndpointPermissions(String tenant)throws CommandException{
        return new Request(null, Request.Type.DELETE, parseString("{{service_uri}}/access/" + tenant)).execute();
    }

    private String postObject(String objectJson, String type, String username) throws CommandException {
        String response = new Request(username, Request.Type.POST, parseString("{{tenantUrl}}/") + type, objectJson).execute();
        JsonElement entity = GsonUtils.getJsonParser().parse(response);
        JsonElement entityElement = GsonUtils.getByPath(entity, "0/object/uri");
        String result;
        if (entityElement != null) {
            result = entityElement.getAsString();
        } else {
            JsonElement error = GsonUtils.getByPath(entity, "0/errors/errorDetailMessage");
            if (error != null){
                result = error.getAsString();
            }else{
				error = GsonUtils.getByPath(entity, "0/errors/errorMessage");
				if (error != null){
					result = error.getAsString();
				}
				else {
					throw new FatalTestException("Object uri or security error not found");
				}
            }
        }
        return result;
    }
    private String postObject(String objectJson, String type, String username, String activityId) throws CommandException {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("ActivityID", activityId);
        String response = new Request(username, Request.Type.POST, parseString("{{tenantUrl}}/") + type, headers,objectJson).
                execute();
        JsonElement entity = GsonUtils.getJsonParser().parse(response);
        JsonElement entityElement = GsonUtils.getByPath(entity, "0/object/uri");
        String result;
        if (entityElement != null) {
            result = entityElement.getAsString();
        } else {
            JsonElement error = GsonUtils.getByPath(entity, "0/errors/errorDetailMessage");
            if (error != null){
                result = error.getAsString();
            }else{
                error = GsonUtils.getByPath(entity, "0/errors/errorMessage");
                if (error != null){
                    result = error.getAsString();
                }
                else {
                    throw new FatalTestException("Object uri or security error not found");
                }
            }
        }
        return result;
    }


    //TODO: rename to deleteTestObject
    protected void deleteTestEntity(String entityUri) {
        deleteTestEntity(entityUri, null);
    }
    protected void deleteTestEntity(String entityUri, String username) {
        if (entityUri == null) return;
        try {
            new Request(username, Request.Type.DELETE, parseString("{{tenantUrl}}/" + entityUri)).execute();
        } catch (CommandException e) {
            throw new TestException("Unable to delete test entity '" + entityUri + "', message: " + e.getMessage());
        }
    }

    protected void setVar(String key, String value) {
        setVar(key, value, false);
    }

    protected void setVar(String key, String value, boolean override) {
        if (override || !vars.containsKey(key)) {
            vars.put(key, value);
        }
    }

    protected void sleep() {
        sleep(getSleepTime());
    }

    protected void sleep(long ms) {
        if (ms == 0) return;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    protected int getEventsQueueSize() {
        return getEventsQueueSize(null);
    }
    protected int getEventsQueueSize(String username) {
        String status;
        try {
            status = new Request(username, Request.Type.GET, parseString("{{statusUrl}}")).execute();
        } catch (CommandException e) {
            throw new FatalTestException("Unable to get reltio status: " + e.getMessage());
        }

        JsonObject statusJson = GsonUtils.getJsonParser().parse(status).getAsJsonObject();
        return Integer.parseInt(statusJson.get("search_service").getAsJsonObject().get("Current events queue size").getAsString());
    }


    public JsonArray getEvents(String eventType, long startTime, long endTime)throws CommandException{
        String json = new Request(Request.Type.GET, parseString("{{tenantUrl}}") + String.format("/entities/_events?types=%s&startTime=%s&endTime=%s",eventType, startTime, endTime)).execute();
        return GsonUtils.getJsonParser().parse(json).getAsJsonArray();
    }

    public JsonObject getEventsTotal(String eventType, long startTime, long endTime)throws CommandException{
        String json = new Request(Request.Type.GET, parseString("{{tenantUrl}}") + String.format("/entities/_events/_total?facet=day&types=%s&startTime=%s&endTime=%s",eventType, startTime, endTime)).execute();
        return GsonUtils.getJsonParser().parse(json).getAsJsonObject();
    }

    protected int getMatchingQueueSize() {
        return getMatchingQueueSize(null);
    }
    protected int getMatchingQueueSize(String username) {
        String status;
        try {
            status = new Request(username, Request.Type.GET, parseString("{{statusUrl}}")).execute();
        } catch (CommandException e) {
            throw new FatalTestException("Unable to get reltio status: " + e.getMessage());
        }

        JsonObject statusJson = GsonUtils.getJsonParser().parse(status).getAsJsonObject();
        return Integer.parseInt(statusJson.get("search_service").getAsJsonObject().get("Matching queue size").getAsString());
    }

    public static int getSleepTime() {
        if (Config.isLoaded() && Config.has("sleep")) {
            return Config.getAsInt("sleep");
        }
        return DEFAULT_SLEEP;
    }

    public static int getTriesCount() {
        if (Config.isLoaded() && Config.has("tries")) {
            return Config.getAsInt("tries");
        }
        return DEFAULT_TRIES;
    }

    protected void waitForEmptyMatchingQueue() {
        long totalWait = 0;
        sleep(getSleepTime());
        while (getMatchingQueueSize() > 0) {
            sleep(QUEUE_WAIT_INTERVAL);
            totalWait += QUEUE_WAIT_INTERVAL;
            if (totalWait > MAX_WAIT_TIME) {
                throw new FatalTestException("Matching Queue was not emptied for a long time.");
            }
        }
    }

    protected void waitForEmptyEventsQueue() {
        waitForEventsQueueSize(0,0);
    }

    protected void waitForEmptyEventsQueue(int queueMaxSize) {
        waitForEventsQueueSize(0,queueMaxSize);
    }

    protected void waitForEventsQueueSize(int queueSize){
        waitForEventsQueueSize(queueSize,0);
    }
    protected void waitForEventsQueueSize(int queueSize, int queueMaxSize) {
        long totalWait = 0;
        int counter = 0;
        sleep(getSleepTime());
        int qs;
        qs=getEventsQueueSize();
        if (queueMaxSize>0 && qs>queueMaxSize){
            throw new FatalTestException("Events Queue size is too long: "+qs);
        }
        while (qs > queueSize) {
            sleep(QUEUE_WAIT_INTERVAL);
            totalWait += QUEUE_WAIT_INTERVAL;
            if (totalWait > MAX_WAIT_TIME) {
                throw new FatalTestException("Events Queue was not emptied for a long time.");
            }
            qs=getEventsQueueSize();
            counter++;
            if (queueMaxSize>0 && counter>queueMaxSize/10) throw new FatalTestException("Events Queue was not emptied for a long time.");
        }
    }

    //return null if object was not found
    protected String getObjectUriByCrosswalk(String crosswalkType, String crosswalkValue) {
        return getObjectUriByCrosswalk(crosswalkType, crosswalkValue, null);
    }

    protected String getObjectUriByCrosswalk(String crosswalkType, String crosswalkValue, String username) {
        String uri = null,
                response;

        try {
            String url = String.format("%s/entities/_byCrosswalk/%s?type=%s", parseString("{{tenantUrl}}"), crosswalkValue, crosswalkType);
            response = new Request(username, Request.Type.GET, url).execute();
        } catch (CommandException e) {
            throw new FatalTestException("Unable to get entity by crosswalk: " + e.getMessage());
        }

        JsonElement uriElement = GsonUtils.getByPath(GsonUtils.getJsonParser().parse(response), "0/object/uri");
        if (uriElement != null) {
            uri = uriElement.getAsString();
        }

        return uri;
    }

    protected JsonElement getEntityWithFilter(String filter) {
        return getEntityWithFilter(filter, null);
    }
    protected JsonElement getEntityWithFilter(String filter, String username) {
        String response;
        try {
            response = new Request(username, Request.Type.GET, parseString("{{tenantUrl}}/entities?filter=" + filter)).execute();
        } catch (CommandException e) {
            String msg = e.getMessage().substring(e.getMessage().indexOf('{'));
            JsonElement error = GsonUtils.getByPath(GsonUtils.getJsonParser().parse(msg), "errorDetailMessage");
            if (error != null) {
                return error;
            } else {
                throw new TestException("Unable to get entity, message: " + e.getMessage());
            }
        }
        return GsonUtils.getJsonParser().parse(response);
    }

    protected JsonElement getEntityTotal(String filter) {
        return getEntityTotal(filter, null);
    }
    protected JsonElement getEntityTotal(String filter, String username) {
        String response;
        try {
            response = new Request(username, Request.Type.GET, parseString("{{tenantUrl}}/entities/_total?filter=" + filter.replaceAll(" ", "%20"))).execute();
        } catch (CommandException e) {
            String msg = e.getMessage().substring(e.getMessage().indexOf('{'));
            JsonElement error = GsonUtils.getByPath(GsonUtils.getJsonParser().parse(msg), "errorDetailMessage");
            if (error != null) {
                return error;
            } else {
                throw new TestException("Unable to get entity, message: " + e.getMessage());
            }
        }
        return GsonUtils.getJsonParser().parse(response);
    }
    protected JsonElement getEntityFacets(String filter) {
        return getEntityWithFilter(filter, null);
    }
    protected JsonElement getEntityFacets(String filter, String username) {
        String response;
        try {
            response = new Request(username, Request.Type.GET, parseString("{{tenantUrl}}/entities/_facets?facet=type&filetr=" + filter)).execute();
        } catch (CommandException e) {
            String msg = e.getMessage().substring(e.getMessage().indexOf('{'));
            JsonElement error = GsonUtils.getByPath(GsonUtils.getJsonParser().parse(msg), "errorDetailMessage");
            if (error != null) {
                return error;
            } else {
                throw new TestException("Unable to get entity, message: " + e.getMessage());
            }
        }
        return GsonUtils.getJsonParser().parse(response);
    }
    //TODO: rename to getObject
    protected JsonElement getEntity(String entityUri) {
        return getEntity(entityUri, null);
    }
    protected JsonElement getEntity(String entityUri, String username) {
        String response;
        try {
            response = new Request(username, Request.Type.GET, parseString("{{tenantUrl}}/" + entityUri + "?options=sendHidden")).execute();
        } catch (CommandException e) {
            String msg = e.getMessage().substring(e.getMessage().indexOf('{'));
            JsonElement error = GsonUtils.getByPath(GsonUtils.getJsonParser().parse(msg), "errorDetailMessage");
            if (error != null) {
                return error;
            } else {
                throw new TestException("Unable to get entity, message: " + e.getMessage());
            }
        }
        return GsonUtils.getJsonParser().parse(response);
    }

    protected void postAttributes(final String objectUri, final String attributeName, final String attributesBody, final String crosswalkType, final String crosswalkValue){
        postAttributes(objectUri, attributeName, attributesBody, crosswalkType, crosswalkValue, null);
    }

    @SuppressWarnings("serial")
    protected void postAttributes(final String objectUri, final String attributeName, final String attributesBody, final String crosswalkType, final String crosswalkValue, String username) {

        final Map<String, String> attributePostHeaders = new HashMap<String, String>() {{
            put("Source-System", crosswalkType);
        }};

        try {
            new Request(username, Request.Type.POST, parseString("{{tenantUrl}}/" + objectUri
                    + "/attributes/" + attributeName + "?returnObjects=false&crosswalkValue=" + crosswalkValue),
                    attributePostHeaders, attributesBody).execute();
        } catch (CommandException e) {
            throw new FatalTestException("Unexpected error during posting attributes: " + e.getMessage(), e);
        }
    }
    protected void updateAttribute(final String attributeUri, final String body, final String crosswalkType, final String crosswalkValue) {
        updateAttribute(attributeUri, body, crosswalkType, crosswalkValue, null);
    }

    @SuppressWarnings("serial")
    protected void updateAttribute(final String attributeUri, final String body, final String crosswalkType, final String crosswalkValue, String username) {

        final Map<String, String> attributePostHeaders = new HashMap<String, String>() {{
            put("Source-System", crosswalkType);
        }};

        try {
            new Request(username, Request.Type.PUT, parseString("{{tenantUrl}}/" + attributeUri + "?returnObjects=false&crosswalkValue=" + crosswalkValue),
                    attributePostHeaders, body).execute();
        } catch (CommandException e) {
            throw new FatalTestException("Unexpected error during updating attribute: " + e.getMessage(), e);
        }
    }

    protected void deleteAttribute(final String attributeUri, final String crosswalkType, final String crosswalkValue){
        deleteAttribute(attributeUri, crosswalkType, crosswalkValue, null);
    }

    @SuppressWarnings("serial")
    protected void deleteAttribute(final String attributeUri, final String crosswalkType, final String crosswalkValue, String username) {

        final Map<String, String> attributePostHeaders = new HashMap<String, String>() {{
            put("Source-System", crosswalkType);
        }};

        try {
            new Request(username, Request.Type.DELETE, parseString("{{tenantUrl}}/" + attributeUri + "?returnObjects=false&crosswalkValue=" + crosswalkValue),
                    attributePostHeaders).execute();
        } catch (CommandException e) {
            throw new FatalTestException("Unexpected error during deleting attribute: " + e.getMessage(), e);
        }
    }
    protected String merge(String uri1, String uri2) throws CommandException {
        return merge(uri1, uri2, null);
    }

    protected String merge(String uri1, String uri2, String username) throws CommandException {
        return new Request(username, Request.Type.POST, parseString("{{tenantUrl}}/" + uri1
                + "/_sameAs?returnObjects=false&uri=" + uri2),"").execute();
    }

    protected void split(String uri1, String uri2) throws CommandException {
        split(uri1, uri2, null);
    }

    protected void split(String uri1, String uri2, String username) throws CommandException {
        new Request(username, Request.Type.POST, parseString("{{tenantUrl}}/" + uri1
                + "/_splitContributor?contributor=" + uri2),"").execute();
    }

    //TODO: add error handling for 500 error (Entity "entities/1ArKF4T" is no longer a part of entity 'entities/1ArJtlB'.)
    protected JsonObject unmerge(String uri1, String uri2) throws CommandException {
        return unmerge(uri1, uri2, null);
    }
    protected JsonObject unmerge(String uri1, String uri2, String username) throws CommandException {
        return new Request(username, Request.Type.POST, parseString("{{tenantUrl}}/" + uri1
                + "/_unmerge?contributorURI=" + uri2),"").executeJson().getAsJsonObject();
    }

    //TODO: add error handling for 500 error (Entity "entities/1ArKF4T" is no longer a part of entity 'entities/1ArJtlB'.)
    protected JsonObject treeUnmerge(String uri1, String uri2) throws CommandException {
        return treeUnmerge(uri1, uri2, null);
    }
    protected JsonObject treeUnmerge(String uri1, String uri2, String username) throws CommandException {
        return new Request(username, Request.Type.POST, parseString("{{tenantUrl}}/" + uri1
                + "/_treeUnmerge?contributorURI=" + uri2),"").executeJson().getAsJsonObject();
    }

    protected JsonObject getMatches(String uri) throws CommandException {
        return getMatches(uri, null);
    }

    protected JsonObject getMatches(String uri, String username) throws CommandException {
        String url = parseString("{{tenantUrl}}/" + uri + "/_matches?calculateNegativeRules=true&select=uri");
        return new Request(username, Request.Type.GET, url).executeJson().getAsJsonObject();
    }

    protected JsonObject getNotMatches(String uri) throws CommandException {
        return getMatches(uri, null);
    }

    protected JsonArray getNotMatches(String uri, String username) throws CommandException {
        String url = parseString("{{tenantUrl}}/" + uri + "/_notMatch");
        return new Request(username, Request.Type.GET, url).executeJson().getAsJsonArray();
    }

    protected JsonElement overwriteEntityType(String uri, String type) throws CommandException {
        return overwriteEntityType(uri, null);
    }

    protected JsonElement overwriteEntityType(String uri, String type, String username) throws CommandException {
        String url = parseString("{{tenantUrl}}/" + uri + "/type");
        String body = "{\"value\" : \"" + type + "\"}";
        return new Request(username, Request.Type.PUT, url, body).executeJson();
    }

    protected void deleteAllEntities(String searchCriteria) {
        deleteAllEntities(searchCriteria, false);
    }

    protected JsonArray entitiesSearch(String filters) {
        return entitiesSearch(filters, null);
    }

    protected JsonArray entitiesSearch(String filters, String username) {
        try {
            return new Request(username, Request.Type.GET, parseString("{{tenantUrl}}/entities?")
                    + String.format("filter=%s&select=uri,type,label,attributes&max=200", filters)).executeJson().getAsJsonArray();
        } catch (CommandException e) {
            throw new FatalTestException("Unexpected error during search: " + e.getMessage(), e);
        }
    }

    protected JsonObject entitiesTotalSearch(String filters, String username) {
        try {
            return new Request(username, Request.Type.GET, parseString("{{tenantUrl}}/entities/_total?")
                    + String.format("filter=%s&select=uri,type,label,attributes&max=200", filters)).executeJson().getAsJsonObject();
        } catch (CommandException e) {
            throw new FatalTestException("Unexpected error during total search: " + e.getMessage(), e);
        }
    }

    protected JsonObject entitiesFacetSearch(String filters, String body ,String username) {
        try {
            return new Request(username, Request.Type.POST, parseString("{{tenantUrl}}/entities/_facets?")
                    + String.format("filter=%s&select=uri,label,attributes&max=200", filters), body).executeJson().getAsJsonObject();
        } catch (CommandException e) {
            throw new FatalTestException("Unexpected error during facet search: " + e.getMessage(), e);
        }
    }

    protected JsonObject entitiesFacetSearchGET(String facets, String filters, String username) {
        try {
            return new Request(username, Request.Type.GET, parseString("{{tenantUrl}}/entities/_facets?") + String.format("facet=%s", facets)
                    + String.format("&filter=%s&select=uri,label,attributes&max=200", filters)).executeJson().getAsJsonObject();
        } catch (CommandException e) {
            throw new FatalTestException("Unexpected error during facet search: " + e.getMessage(), e);
        }
    }

    protected JsonObject entitiesExportScan(String filters, String formats, String username){
        try {
            return new Request(username, Request.Type.POST, parseString("{{exportUrl}}/entities/_scan?")
                    + String.format("filter=%s", filters) + formats,"{}").executeJson().getAsJsonObject();
        } catch (CommandException e) {
            throw new FatalTestException("Unexpected error during export: " + e.getMessage(), e);
        }
    }

    protected JsonObject entitiesExportAll(String formats, String username){
        try {
            return new Request(username, Request.Type.POST, parseString("{{exportUrl}}/entities/_all") + formats,"").executeJson().getAsJsonObject();
        } catch (CommandException e) {
            throw new FatalTestException("Unexpected error during export: " + e.getMessage(), e);
        }
    }

    protected JsonObject entitiesExport(String formats, String body, String username){
        try {
            return new Request(username, Request.Type.POST, parseString("{{exportUrl}}/entities") + formats, body).executeJson().getAsJsonObject();
        } catch (CommandException e) {
            throw new FatalTestException("Unexpected error during export: " + e.getMessage(), e);
        }
    }

    protected JsonObject relationsExportAll(String formats, String username){
        try {
            return new Request(username, Request.Type.POST, parseString("{{exportUrl}}/relations/_all") + formats,"").executeJson().getAsJsonObject();
        } catch (CommandException e) {
            throw new FatalTestException("Unexpected error during export: " + e.getMessage(), e);
        }
    }

    protected JsonObject relationsExport(String formats, String body, String username){
        try {
            return new Request(username, Request.Type.POST, parseString("{{exportUrl}}/relations") + formats, body).executeJson().getAsJsonObject();
        } catch (CommandException e) {
            throw new FatalTestException("Unexpected error during export: " + e.getMessage(), e);
        }
    }


    protected JsonArray categorySearch(String filters) {
        return categorySearch(filters, null);
    }

    protected JsonArray categorySearch(String filters, String username) {
        try {
            return new Request(username, Request.Type.GET, parseString("{{tenantUrl}}/categories?")
                    + String.format("filter=%s&max=200", filters)).executeJson().getAsJsonArray();
        } catch (CommandException e) {
            throw new FatalTestException("Unexpected error during deleting attribute: " + e.getMessage(), e);
        }
    }

    protected JsonArray categorySearchExt(String filters, int searchMax, int searchOffset) {
        return categorySearchExt(filters, searchMax, searchOffset, null);
    }

    protected JsonArray categorySearchExt(String filters, int searchMax, int searchOffset, String username) {
        try {
            return new Request(username, Request.Type.GET, parseString("{{tenantUrl}}/categories?")
                    + String.format("filter=%s&max=%s&offset=%s", filters, String.valueOf(searchMax), String.valueOf(searchOffset))).executeJson().getAsJsonArray();
        } catch (CommandException e) {
            throw new FatalTestException("Unexpected error during deleting attribute: " + e.getMessage(), e);
        }
    }

    public static void compareJsons(String expected, String actual, String message) throws StepException {
        try {
            compareJsons(GsonUtils.getJsonParser().parse(expected), GsonUtils.getJsonParser().parse(actual), message);
        }catch (JsonSyntaxException e){
            throw new StepException("Json parsing error:\n" + e.getMessage());
        }
    }

    public static void compareJsons(JsonElement expected, JsonElement actual, String message) throws StepException {
        compareJsons(expected, actual, message, "");
    }
    public static void compareJsons(JsonElement expected, JsonElement actual, String message, String regex) throws StepException {
        JsonDiff jsonDiff = new JsonDiff(expected, actual);
        if (jsonDiff.isDifferent() && (regex.equals("") ? true : !jsonDiff.getDifferenceJson().matches(regex))) {
            throw new StepException(String.format("%s\n-=EXPECTED=-: %s\n-=ACTUAL=-: %s\n-=DIFFERENCE=-: %s", message,
                    expected.toString(),
                    actual.toString(),
                    jsonDiff.getDifferenceJson()));
        }
    }


    public static void saveJsonToFile(String path, String name, String data) throws IOException {
        String ppData = GsonUtils.getGsonPrettyPrint().toJson(GsonUtils.getJsonParser().parse(data));
        new File(path).mkdir();
        File f = new File(path + "//" + name);
        IOUtils.saveToFile(f, ppData);
    }

    protected String deleteEntity(String entityURI)throws CommandException
    {
        return deleteEntity(entityURI, null);
    }

    protected String deleteEntity(String entityURI, String username)throws CommandException
    {
        try {
            String response = new Request(username, Request.Type.DELETE, parseString("{{tenantUrl}}/") + entityURI).execute();
            JsonElement entity = GsonUtils.getJsonParser().parse(response);
            JsonElement entityElement = GsonUtils.getByPath(entity, "0/status");
            String result;
            if (entityElement != null) {
                result = entityElement.getAsString();
            }
            return response;
        } catch (CommandException e) {
            String msg = e.getMessage().substring(e.getMessage().indexOf('{'));
            JsonElement error = GsonUtils.getByPath(GsonUtils.getJsonParser().parse(msg), "errorDetailMessage");
            if (error != null) {
                return error.getAsString();
            } else {
                throw new FatalTestException("Object uri or security error not found");
            }
        }
    }

    protected void deleteAllEntities(String searchCriteria, boolean ignoreInconsistency) {
        deleteAllEntities(searchCriteria, ignoreInconsistency, null);
    }

    protected void deleteAllEntities(String searchCriteria, boolean ignoreInconsistency, String username) {
        int total;
        int max = 200;
        String filter = String.format("filter=%s&select=uri&max=200", searchCriteria.replaceAll(" ", "%20"));
        //System.out.print(parseString("{{tenantUrl}}/entities/_total?") + filter);
        try {
            JsonElement response = new Request(username, Request.Type.GET, parseString("{{tenantUrl}}/entities/_total?") + filter).executeJson();
            total = response.getAsJsonObject().get("total").getAsInt();
            if (total > 0) {
                int requestCount = total/max;
                if (total%max > 0) requestCount++;
                for (int i=0; i<requestCount; i++) {
                    int offcet = i * max;
                    response = new Request(username, Request.Type.GET, parseString("{{tenantUrl}}/entities?") + filter + "&offset=" + offcet).executeJson();
                    logger.trace("Search result: " + response);
                    for (JsonElement e : response.getAsJsonArray()) {
                        String uri = e.getAsJsonObject().get("uri").getAsString();
                        try {
                            new Request(username, Request.Type.DELETE, parseString("{{tenantUrl}}/") + uri).execute();
                        } catch (CommandException ex) {
                            if (ex.getMessage().contains(String.format("Object with id=%s not found", uri))) {
                                String message = String.format("ES and C* inconsistency for entity with URI=%s - exist in ES, but not exist in C* ", uri);
                                if (ignoreInconsistency) {
                                    logger.warn(message);
                                } else {
                                    throw new TestException(message);
                                }
                            } else {
                                throw ex;
                            }
                        }
                    }
                }
            }
        } catch (CommandException e) {
            throw new FatalTestException("Unexpected error during deleting attribute: " + e.getMessage(), e);
        }
    }

    protected void waitForAutomergeOfEntities(EntityModel... entities) throws ReltioObjectException, StepException {
        waitForEmptyEventsQueue();
        waitForEmptyMatchingQueue();

        for (EntityModel entity : entities) {
            entity.getByInitialUri();
        }

        int tries = 0;
        for (;;) {
            if (!isAllUrsIsSame(entities)) {
                if (++tries < getTriesCount()) {
                    sleep();
                    for (EntityModel entity : entities) {
                        entity.getByInitialUri();
                    }
                } else {
                    throw new StepException("Entities are not merged, when had to be");
                }
            } else {
                break;
            }
        }
    }

    protected boolean isAllUrsIsSame(EntityModel... entities) {
        List<String> uris = new ArrayList();
        for (EntityModel e : entities) {
            uris.add(e.getUri());
        }
        return ListUtils.getWithoutDuplicates(uris).size() == 1;
    }

    protected void runBatchAndWait(){
        //runBatchAndWait(2);
        sleep(5000);
    }

    protected void runBatchAndWait(int numberBatches){
        runBatchAndWait(numberBatches, null);
    }
    protected void runBatchAndWait(int numberBatches, String username){
//        try {
//            for (int i = 0;i < numberBatches;i++){
//                new Request(username, Request.Type.GET, parseString("{{connectorUrl}}/batch")).execute();
//                while (connectorIsNotWaitingState()){
//                    sleep();
//                }
//            }
//            sleep(5000);
//        } catch (CommandException e) {
//            throw new FatalTestException("Unexpected error during working with connector: " + e.getMessage(), e);
//        }
    	sleep(20000);
    }

    private boolean connectorIsNotWaitingState(){
        return connectorIsNotWaitingState(null);
    }

    private boolean connectorIsNotWaitingState(String username){
        try {
            String response = new Request(username, Request.Type.GET, parseString("{{connectorUrl}}/status")).execute();
            JsonElement resp = GsonUtils.getJsonParser().parse(response);
            return (! resp.getAsJsonObject().get("batchStatus").getAsJsonObject().get("state").getAsString().equals("Waiting"));
        } catch (CommandException e) {
            throw new FatalTestException("Unexpected error during working with connector: " + e.getMessage(), e);
        }
    }

    protected void rebuildMatchTables(String entityType)throws CommandException{
        rebuildMatchTables(null, entityType);
    }

    protected void rebuildMatchTables(String username, String entityType)throws CommandException{
        new Request(username, Request.Type.POST, parseString("{{service_uri}}/rebuildmatchtable?tenantId={{tenant_name}}&entityType=") + entityType, "").execute();
    }

}
