package com.thoughtworks.wheels;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.MutableTypeToInstanceMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {

    private static final String BEAN = "bean";
    private static final String BEAN_ID = "id";
    private static final String CLASS_FULL_NAME = "class";
    private static final String BEAN_PROPERTY = "property";
    private static final String BEAN_CONSTRUCTOR_ARGS = "constructor-args";
    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_VAR = "var";
    private static final String PROPERTY_REF = "ref";
    private static final String SETTER_PREFIX = "set";

    private Map<String, Element> ID_ELEMENT_MAP;
    private Map<String, Class<?>> ID_CLASS_MAP = new HashMap<>();
    private MutableTypeToInstanceMap<Object> CLASS_INSTANCE_MAP = new MutableTypeToInstanceMap();


    public ApplicationContext(String fileAddress) {
        File file = new File(fileAddress);
        try {
            this.init(XmlParser.parseXml(file));
        } catch (Exception e) {
            throw new RuntimeException("Bean Definition Loading failed.", e);
        }
    }

    protected void init(Document document) {
        NodeList nodeList = document.getDocumentElement().getElementsByTagName(BEAN);
        final ImmutableMap.Builder<String, Element> builder = ImmutableMap.builder();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element item = (Element) nodeList.item(i);
            builder.put(item.getAttribute(BEAN_ID), item);
        }
        ID_ELEMENT_MAP = builder.build();
    }

    public <T extends Object> T getBean(String beanId) {
       T bean = loadBeanById(beanId);

        //var
        for (Element e : getPropertiesList(beanId)) {
            try {
                ID_CLASS_MAP.get(beanId).getMethod(SETTER_PREFIX + wrapAString(e.getAttribute(PROPERTY_NAME)),
                        String.class).invoke(bean, e.getAttribute(PROPERTY_VAR));
            } catch (NoSuchMethodException e1) {
                throw new RuntimeException(e1);
            } catch (InvocationTargetException e1) {
                 throw new RuntimeException(e1);
            } catch (IllegalAccessException e1) {
                throw new RuntimeException(e1);
            }
        }
        //ref
        return bean;
    }


    private <T extends Object> T loadBeanById(String beanId) {
        T bean;
        if (ID_CLASS_MAP.containsKey(beanId)) {
            bean = (T) CLASS_INSTANCE_MAP.get(ID_CLASS_MAP.get(beanId));
        } else {
            bean = createPlainBean(beanId);
        }
        return bean;
    }

    private <T extends Object> T createPlainBean(String beanId) {
        try {
            final Class clazz = Class.forName(getBeanElementById(beanId).getAttribute(CLASS_FULL_NAME));
            ID_CLASS_MAP.put(beanId, clazz);
            final Object bean = clazz.newInstance();
            CLASS_INSTANCE_MAP.putInstance(clazz, bean);
            return (T) bean;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
            properties.add(((Element) propertiesList.item(i)).getAttribute(PROPERTY_VAR));
        }
        return properties;
    }

    protected Element getBeanElementById(String beanId) {
        return getById(ID_ELEMENT_MAP, beanId);
    }

    private <T extends Object> T getById(Map<String, T> map, String id) {
        if (!map.containsKey(id)) throw new RuntimeException("No Value with id:" + id + " exist.");
        return map.get(id);
    }

}
