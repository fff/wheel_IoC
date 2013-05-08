package com.thoughtworks.elf.utils;

import com.thoughtworks.elf.internal.Constants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Elem {
    private Element elem;

    public Elem(Element elem) {
        this.elem = elem;
    }

    public Elem(Node item) {
        this.elem = (Element) item;
    }

    public String getName() {
        return this.getProperty(Constants.PROPERTY_NAME);
    }

    public String getVar() {
        return this.getProperty(Constants.PROPERTY_VAR);
    }

    public String getRef() {
        return this.getProperty(Constants.PROPERTY_REF);
    }

    public NodeList getConstructorArgs() {
        return this.elem.getElementsByTagName(Constants.BEAN_CONSTRUCTOR_ARG);
    }

    public String getId() {
        return this.getProperty(Constants.BEAN_ID);
    }

    public String getType() {
        return this.getProperty(Constants.PROPERTY_TYPE);
    }

    private String getProperty(String propertyName) {
        final String value = this.elem.getAttribute(propertyName);
        return value.length() == 0 ? null : value;
    }

    public String getFullClassName() {
        return this.getProperty(Constants.CLASS_FULL_NAME);
    }
}
