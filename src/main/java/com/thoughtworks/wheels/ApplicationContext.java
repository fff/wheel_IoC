package com.thoughtworks.wheels;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class ApplicationContext {

    private static final String BEAN = "bean";
    private static final String BEAN_ID = "id";
    private static final String CLASS_FULL_NAME = "class";
    private static final String BEAN_PROPERTY = "property";
    private static final String BEAN_CONSTRUCTOR_ARGS = "constructor-args";
    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_VALUE = "var";
    private static final String SETTER_PREFIX = "set";

    private static final HashMap<String, Element> BEAN_ELEMENT_MAP = new HashMap<String, Element>(10);


    public ApplicationContext(String fileAddress) {
        File file = new File(fileAddress);
        try {
            this.init(XmlParser.parseXml(file));
        } catch (Exception e) {
            throw new RuntimeException("Bean Definition Loading failed.", e);
        }
        if (BEAN_ELEMENT_MAP.isEmpty()) {
            throw new RuntimeException("No definition found in file:" + fileAddress);
        }
    }

    protected void init(Document document) {
        NodeList nodeList = document.getDocumentElement().getElementsByTagName(BEAN);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element item = (Element) nodeList.item(i);
            BEAN_ELEMENT_MAP.put(item.getAttribute(BEAN_ID), item);
        }
    }

    public Object getBean(String beanId) {
        Object obj = null;
        try {
            String classFullName = getBeanElementById(beanId).getAttribute(CLASS_FULL_NAME);
            Class<?> clazz = Class.forName(classFullName);
            obj = clazz.newInstance();
            for (Element e : getPropertiesList(beanId)) {
                Method method = clazz.getMethod(SETTER_PREFIX + wrapAString(e.getAttribute(PROPERTY_NAME)), String.class);
                method.invoke(obj, e.getAttribute(PROPERTY_VALUE));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    private String wrapAString(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public ArrayList<Element> getConstructorArgsList(String beanId) {
        ArrayList<Element> constructorArgs = new ArrayList<>();
        NodeList constructorArgsList = getBeanElementById(beanId).getElementsByTagName(BEAN_CONSTRUCTOR_ARGS);
        for (int i = 0; i < constructorArgsList.getLength(); i++) {
            constructorArgs.add((Element) constructorArgsList.item(i));
        }
        return constructorArgs;
    }

    protected ArrayList<Element> getPropertiesList(String beanId) {
        ArrayList<Element> properties = new ArrayList<>();
        NodeList propertiesList = getBeanElementById(beanId).getElementsByTagName(BEAN_PROPERTY);
        for (int i = 0; i < propertiesList.getLength(); i++) {
            properties.add((Element) propertiesList.item(i));
        }
        return properties;
    }

    public ArrayList<String> getPropertyNames(String beanId) {
        ArrayList<String> properties = new ArrayList<>();
        NodeList propertiesList = getBeanElementById(beanId).getElementsByTagName(BEAN_PROPERTY);
        for (int i = 0; i < propertiesList.getLength(); i++) {
            properties.add(((Element) propertiesList.item(i)).getAttribute(PROPERTY_NAME));
        }
        return properties;
    }

    public ArrayList<String> getPropertyValues(String beanId) {
        ArrayList<String> properties = new ArrayList<>();
        NodeList propertiesList = getBeanElementById(beanId).getElementsByTagName(BEAN_PROPERTY);
        for (int i = 0; i < propertiesList.getLength(); i++) {
            properties.add(((Element) propertiesList.item(i)).getAttribute(PROPERTY_VALUE));
        }
        return properties;
    }

    protected Element getBeanElementById(String beanId) {
        return BEAN_ELEMENT_MAP.get(beanId);
    }

}
