package com.qa.framework.helpers;

import java.util.List;

public class NestedAttribute {

    public Crosswalk xw;
    public String name;
    public List<Attribute> attributes;

    public NestedAttribute(Crosswalk xw, String name, List<Attribute> attributes) {
        this.xw = xw;
        this.name = name;
        this.attributes = attributes;
    }

    public NestedAttribute addAttribute(Attribute attr) {
        attributes.add(attr);
        return this;
    }

}
