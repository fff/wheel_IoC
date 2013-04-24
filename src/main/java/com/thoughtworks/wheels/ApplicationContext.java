package com.thoughtworks.wheels;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class ApplicationContext {

    public static final String BEAN = "bean";
    public static final String CLASS_FULL_NAME = "class";
    public static final String BEAN_PROPERTY = "property";
    public static final String BEAN_CONSTRUCTOR_ARGS = "constructor-args";
    public static final HashMap<String, Element> BEAN_ELEMENT_MAP = new HashMap<String, Element>();
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_VALUE = "var";


    public ApplicationContext(String fileAddress) {
        try {
            File file = new File(fileAddress);
            this.init(file);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(BEAN_ELEMENT_MAP.isEmpty()){
            throw new RuntimeException("No Bean defined.");
        }
    }

    protected void init(File file) throws ParserConfigurationException, SAXException, IOException {
        NodeList nodeList = XmlParser.parseXml(file).getDocumentElement().getElementsByTagName(BEAN);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element item = (Element) nodeList.item(i);
            BEAN_ELEMENT_MAP.put(item.getAttribute("id"), item);
        }
    }

    public Object getBean(String beanId) {
        Object obj = null;
        try {
            String classFullName = getBeanElementById(beanId).getAttribute(CLASS_FULL_NAME);
            Class<?> clazz = Class.forName(classFullName);
            obj = clazz.newInstance();
            for (Element e : getPropertiesList(beanId)) {
                Method method = clazz.getMethod("set" + wrapAString(e.getAttribute("name")), String.class);
                method.invoke(obj, e.getAttribute("var"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    private String wrapAString(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public ArrayList<Element> getConstructorArgsList(String beanId) throws IOException, SAXException, ParserConfigurationException {
        ArrayList<Element> constructorArgs = new ArrayList<>();
        NodeList constructorArgsList = getBeanElementById(beanId).getElementsByTagName(BEAN_CONSTRUCTOR_ARGS);
        for (int i = 0; i < constructorArgsList.getLength(); i++) {
            constructorArgs.add((Element) constructorArgsList.item(i));
        }
        return constructorArgs;
    }

    protected ArrayList<Element> getPropertiesList(String beanId) throws IOException, SAXException, ParserConfigurationException {
        ArrayList<Element> properties = new ArrayList<>();
        NodeList propertiesList = getBeanElementById(beanId).getElementsByTagName(BEAN_PROPERTY);
        for (int i = 0; i < propertiesList.getLength(); i++) {
            properties.add((Element) propertiesList.item(i));
        }
        return properties;
    }

    public ArrayList<String> getPropertyNames(String beanId) throws IOException, SAXException, ParserConfigurationException {
        ArrayList<String> properties = new ArrayList<>();
        NodeList propertiesList = getBeanElementById(beanId).getElementsByTagName(BEAN_PROPERTY);
        for (int i = 0; i < propertiesList.getLength(); i++) {
            properties.add(((Element) propertiesList.item(i)).getAttribute(PROPERTY_NAME));
        }
        return properties;
    }

    public ArrayList<String> getPropertyValues(String beanId) throws IOException, SAXException, ParserConfigurationException {
        ArrayList<String> properties = new ArrayList<>();
        NodeList propertiesList = getBeanElementById(beanId).getElementsByTagName(BEAN_PROPERTY);
        for (int i = 0; i < propertiesList.getLength(); i++) {
            properties.add(((Element) propertiesList.item(i)).getAttribute(PROPERTY_VALUE));
        }
        return properties;
    }

    protected Element getBeanElementById(String beanId) throws ParserConfigurationException, SAXException, IOException {
        return BEAN_ELEMENT_MAP.get(beanId);
    }

}
