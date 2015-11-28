package com.qa.framework.helpers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.reltio.qa.exceptions.CommandException;
import com.qa.framework.exceptions.FatalTestException;
import com.reltio.qa.exceptions.ReltioObjectException;
import com.reltio.qa.request.Request;
import com.reltio.qa.utils.GsonUtils;

public class Entity extends ReltioObject {	

    private String tenantUrl;

    @SuppressWarnings("serial")
    private Set<String> excludeStartEndObjectXws = new HashSet<String>() {{
        add("(.*)/startObjectCrosswalks/");
        add("(.*)/endObjectCrosswalks/");
    }};

    public Entity(String entityJson, String tenantUrl) {
        this(GsonUtils.getJsonParser().parse(entityJson), tenantUrl);
    }

    public Entity(JsonElement entity, String tenantUrl) {
        jsonData = entity.getAsJsonObject();
        this.tenantUrl = tenantUrl;
        if (!this.tenantUrl.endsWith(URI_SEPARATOR)) this.tenantUrl += URI_SEPARATOR;
    }

    public String getUri() throws ReltioObjectException {
        if (jsonData.has(URI_KEY)) {
            return jsonData.get(URI_KEY).getAsString();
        } else {
            throw new ReltioObjectException("Uri not found");
        }
    }

    public String getId() throws ReltioObjectException {
        return getUri().split(URI_SEPARATOR)[1];
    }

    //return null if crosswalk not found
    public String getCrosswalkUri(Crosswalk crosswalk) throws ReltioObjectException {

        JsonObject xw = getCrosswalkJson(crosswalk);
        if (xw == null) {
            return null;
        }
        if (!xw.has("uri")) {
            throw new ReltioObjectException("There is no 'uri' property for crosswalk");
        }
        return xw.get("uri").getAsString();
    }

    //return null if crosswalk not found
    public JsonObject getCrosswalkJson(final Crosswalk crosswalk) {
        return GsonUtils.findByKeysValues(GsonUtils.cleanseJson(jsonData, null, excludeStartEndObjectXws), crosswalk.getPropertyMap());
    }

    /**
     * Change crosswalk property
     * @return return true if property was successfully changed, false if not
     * @throws ReltioObjectException
     * @throws CommandException
     */
    public boolean changeCrosswalkProperty(Crosswalk crosswalk, String property, String newValue) throws CommandException, ReltioObjectException {
        JsonElement response = new Request(Request.Type.PUT,
                tenantUrl + getCrosswalkUri(crosswalk) + URI_SEPARATOR + property,
                "{\"value\":\"" + newValue + "\"}").executeJson();

        return !(response == null || GsonUtils.findByKeyValue(response, SUCCESSFUL_KEY, "true") == null);
    }


    /**
     * Update attribute, uses only first attribute value
     * @param attribute
     * @return return true if attribute was successfully updated, false if not
     * @throws ReltioObjectException
     */
    @SuppressWarnings("serial")
    public boolean updateAttribute(Attribute attribute) throws ReltioObjectException {
        final Crosswalk xw = attribute.xw;
        Map<String, String> attributePostHeaders = new HashMap<String, String>() {{
            put("Source-System", xw.type);
        }};

        try {
            new Request(Request.Type.PUT, tenantUrl + getAttributeUri(attribute) + "?returnObjects=false&crosswalkValue=" + xw.value,
                    attributePostHeaders, "{\"value\":\"" + attribute.getSingleValue() + "\"}").execute();
        } catch (CommandException e) {
            throw new FatalTestException("Unexpected error during updating attribute: " + e.getMessage(), e);
        }
        return true;
    }

    /**
     * Add attribute, uses all attribute values
     * @param attribute
     * @return return true if attribute was successfully added, false if not
     * @throws ReltioObjectException
     */
    @SuppressWarnings("serial")
    public boolean addAttribute(Attribute attribute) throws ReltioObjectException {
        final Crosswalk xw = attribute.xw;
        Map<String, String> attributePostHeaders = new HashMap<String, String>() {{
            put("Source-System", xw.type);
        }};

        try {
            new Request(Request.Type.POST, tenantUrl + getAttributeUri(attribute) + "?returnObjects=false&crosswalkValue=" + xw.value,
                    attributePostHeaders, attribute.getJsonForPost()).execute();
        } catch (CommandException e) {
            throw new FatalTestException("Unexpected error during updating attribute: " + e.getMessage(), e);
        }
        return true;
    }

    /**
     *
     * @param attribute
     * @return attribute URI (for any of attribute values) or null if nothing was found
     */
    public String getAttributeUri(Attribute attribute) {
        JsonObject xwJson = getCrosswalkJson(attribute.xw);
        String regex = "(.*?)";
        if (attribute.name.contains(URI_SEPARATOR)) {
            for (String namePart : attribute.name.split(URI_SEPARATOR)) {
                regex += URI_SEPARATOR + namePart + URI_SEPARATOR + "(.*?)";
            }
        } else {
            regex += URI_SEPARATOR + attribute.name + URI_SEPARATOR + "(.*?)";
        }

        if (xwJson.has(ATTRIBUTES_KEY) && xwJson.get(ATTRIBUTES_KEY).isJsonArray()) {
            for (JsonElement attributeUri : xwJson.get(ATTRIBUTES_KEY).getAsJsonArray()) {
                String tmp = attributeUri.getAsString();
                if (tmp.matches(regex)) return tmp;
            }
        }

        if (xwJson.has(ATTRIBUTES_KEY_REF) && xwJson.get(ATTRIBUTES_KEY_REF).isJsonArray()) {
            for (JsonElement attributeUri : xwJson.get(ATTRIBUTES_KEY_REF).getAsJsonArray()) {
                String tmp = attributeUri.getAsString();
                if (tmp.matches(regex)) return tmp;
            }
        }

        return null;
    }

    public Entity refresh() throws CommandException, ReltioObjectException {
        return refresh(getUri());
    }

    public Entity refresh(String uri) throws CommandException, ReltioObjectException {
        jsonData = GsonUtils.getJsonParser().parse(new Request(Request.Type.GET, tenantUrl + uri).execute()).getAsJsonObject();
        return this;
    }

    public JsonObject getCrosswalkTree() throws ReltioObjectException {
        try {
            return new Request(Request.Type.GET, tenantUrl + getUri()
                    + "/_crosswalkTree").executeJson().getAsJsonObject();
        } catch (CommandException e) {
            throw new FatalTestException("Unexpected error during getting crosswalk tree: " + e.getMessage(), e);
        }
    }

    public boolean isOnlyOneContributor() throws ReltioObjectException {
        return GsonUtils.getElementWithKey("merges", getCrosswalkTree()) == null;
    }

    /**
     * Search for URI of loser in crosswalk tree with given crosswalk
     * @param phantomCrosswalk crosswalk of needed entity
     * @return URI of entity or null if nothing was found
     * @throws ReltioObjectException
     */
    public String getUriOfPhantomFromXwTree(Crosswalk phantomCrosswalk) throws ReltioObjectException {
        return getUriOfPhantomFromXwTree(phantomCrosswalk, null);
    }

    /**
     * Search for URI of loser in crosswalk tree with given crosswalk
     * @param phantomCrosswalk crosswalk of needed entity
     * @param crosswalkTree crosswalk tree where to search
     * @return URI of entity or null if nothing was found
     * @throws ReltioObjectException
     */
    public String getUriOfPhantomFromXwTree(Crosswalk phantomCrosswalk, JsonObject crosswalkTree) throws ReltioObjectException {
        if (crosswalkTree == null) {
            crosswalkTree = getCrosswalkTree();
        }
        for (JsonElement losers : GsonUtils.getAllSubElementsWithKey("losers", crosswalkTree)) {
            for (JsonElement loser : losers.getAsJsonArray()) {
                if (GsonUtils.findByKeysValues(loser, phantomCrosswalk.getPropertyMap()) != null) {
                    String res = getUriOfPhantomFromXwTree(phantomCrosswalk, loser.getAsJsonObject());
                    if (res == null) {
                        return loser.getAsJsonObject().get("uri").getAsString();
                    } else {
                        return res;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return GsonUtils.getGsonPrettyPrint().toJson(jsonData);
    }

    @Override
    public Entity clone() {
        return new Entity(GsonUtils.getGson().toJson(jsonData), tenantUrl);
    }
}
