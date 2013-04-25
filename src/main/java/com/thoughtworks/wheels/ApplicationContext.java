package com.thoughtworks.wheels;

import com.google.common.collect.ImmutableMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import static com.thoughtworks.wheels.Constants.*;

public class ApplicationContext {


    private Map<String, BeanWrapper<Object>> ID_WRAPPER_MAP;


    public ApplicationContext(String fileAddress) {
        File file = new File(fileAddress);
        try {
            this.init(XmlParser.parseXml(file));
        } catch (Exception e) {
            throw new RuntimeException("Bean Definition Loading failed.", e);
        }
    }

    protected void init(Document document) {
        NodeList beanList = document.getDocumentElement().getElementsByTagName(BEAN);
        final ImmutableMap.Builder<String, BeanWrapper<Object>> builder = ImmutableMap.builder();
        for (int i = 0; i < beanList.getLength(); i++) {
            Element item = (Element) beanList.item(i);
            builder.put(item.getAttribute(BEAN_ID), new BeanWrapper(item));
        }
        ID_WRAPPER_MAP = builder.build();
        //var

        //ref
    }


    public <T extends Object> T getBean(String beanId) {
        final BeanWrapper<Object> wrapper = getWrapperById(beanId);

        //var TODO more than string
        for (Element e : getPropertiesList(beanId)) {
            wrapper.setProperty(e.getAttribute(PROPERTY_NAME), String.class, e.getAttribute(PROPERTY_VAR));
        }
        //ref
        return (T) wrapper.instance;
    }


    @Deprecated
    public ArrayList<Element> getConstructorArgsList(String beanId) {
        ArrayList<Element> constructorArgs = new ArrayList<>();
        NodeList constructorArgsList = getWrapperById(beanId).element.getElementsByTagName(BEAN_CONSTRUCTOR_ARGS);
        for (int i = 0; i < constructorArgsList.getLength(); i++) {
            constructorArgs.add((Element) constructorArgsList.item(i));
        }
        return constructorArgs;
    }

    protected ArrayList<Element> getPropertiesList(String beanId) {
        ArrayList<Element> properties = new ArrayList<>();
        NodeList propertiesList = getWrapperById(beanId).element.getElementsByTagName(BEAN_PROPERTY);
        for (int i = 0; i < propertiesList.getLength(); i++) {
            properties.add((Element) propertiesList.item(i));
        }
        return properties;
    }

    protected BeanWrapper<Object> getWrapperById(String beanId) {
        if (!ID_WRAPPER_MAP.containsKey(beanId)) throw new RuntimeException("No Value with id:" + beanId + " exist.");
        return ID_WRAPPER_MAP.get(beanId);
    }

}
