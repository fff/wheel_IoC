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
    public static final HashMap BEAN_MAP = new HashMap<String, Integer>();
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_VALUE = "var";

    private final File file;

    public ApplicationContext(String fileAddress) {
        this.file = new File(fileAddress);
    }

    public Object getBean(String beanName) {
        Object obj = null;
        try {
            String classFullName = getBeanElementByName(beanName).getAttribute(CLASS_FULL_NAME);
            Class<?> clazz = Class.forName(classFullName);
            obj = clazz.newInstance();
            for (Element e : getPropertiesList(beanName)) {
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

    public ArrayList<Element> getConstructorArgsList(String beanName) throws IOException, SAXException, ParserConfigurationException {
        ArrayList<Element> constructorArgs = new ArrayList<>();
        NodeList constructorArgsList = getBeanElementByName(beanName).getElementsByTagName(BEAN_CONSTRUCTOR_ARGS);
        for (int i = 0; i < constructorArgsList.getLength(); i++) {
            constructorArgs.add((Element) constructorArgsList.item(i));
        }
        return constructorArgs;
    }

    protected ArrayList<Element> getPropertiesList(String beanName) throws IOException, SAXException, ParserConfigurationException {
        ArrayList<Element> properties = new ArrayList<>();
        NodeList propertiesList = getBeanElementByName(beanName).getElementsByTagName(BEAN_PROPERTY);
        for (int i = 0; i < propertiesList.getLength(); i++) {
            properties.add((Element) propertiesList.item(i));
        }
        return properties;
    }

    public ArrayList<String> getPropertyNames(String beanName) throws IOException, SAXException, ParserConfigurationException {
        ArrayList<String> properties = new ArrayList<>();
        NodeList propertiesList = getBeanElementByName(beanName).getElementsByTagName(BEAN_PROPERTY);
        for (int i = 0; i < propertiesList.getLength(); i++) {
            properties.add(((Element) propertiesList.item(i)).getAttribute(PROPERTY_NAME));
        }
        return properties;
    }

    public ArrayList<String> getPropertyValues(String beanName) throws IOException, SAXException, ParserConfigurationException {
        ArrayList<String> properties = new ArrayList<>();
        NodeList propertiesList = getBeanElementByName(beanName).getElementsByTagName(BEAN_PROPERTY);
        for (int i = 0; i < propertiesList.getLength(); i++) {
            properties.add(((Element) propertiesList.item(i)).getAttribute(PROPERTY_VALUE));
        }
        return properties;
    }

    protected Element getBeanElementByName(String beanName) throws ParserConfigurationException, SAXException, IOException {
        putBeanIntoBeanMap();
        return (Element) getBeanElements().item((Integer) BEAN_MAP.get(beanName));
    }

    protected void putBeanIntoBeanMap() throws ParserConfigurationException, SAXException, IOException {
        NodeList nodeList = getBeanElements();

        for (int i = 0; i < nodeList.getLength(); i++) {
            BEAN_MAP.put(((Element) (nodeList.item(i))).getAttribute("id"), i);
        }
    }

    private NodeList getBeanElements() throws ParserConfigurationException, SAXException, IOException {
        return XmlParser.parseXml(file).getDocumentElement().getElementsByTagName(BEAN);
    }
}
