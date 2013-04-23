package com.thoughtworks.wheels;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ApplicationContext {

    public static final String BEAN = "bean";
    public static final String CLASS_FULL_NAME = "class";
    public static final String BEAN_PROPERTY = "property";
    public static final String BEAN_CONSTRUCTOR_ARGS = "constructor-args";
    public static final HashMap BEAN_MAP = new HashMap<String, Integer>();

    private final File file;

    public ApplicationContext(String fileAddress) {
        this.file = new File(fileAddress);
    }

    public Object getBean(String beanName) {
        Object obj = null;
        try {
            Element beanElement = getBeanElementByName(beanName);
            String classFullName = beanElement.getAttribute(CLASS_FULL_NAME);
            obj = Class.forName(classFullName).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    protected void putBeanIntoBeanMap() throws ParserConfigurationException, SAXException, IOException {
        NodeList nodeList = getBeanElements();

        for (int i = 0; i < nodeList.getLength(); i++) {
            BEAN_MAP.put(((Element) (nodeList.item(i))).getAttribute("id"), i);
        }
    }

    protected Element getBeanElementByName(String beanName) throws ParserConfigurationException, SAXException, IOException {
        putBeanIntoBeanMap();
        return (Element) getBeanElements().item((Integer) BEAN_MAP.get(beanName));
    }

    protected Element getConstructorArgsElement(String beanName) throws IOException, SAXException, ParserConfigurationException {
        return (Element) getBeanElementByName(beanName).getElementsByTagName(BEAN_CONSTRUCTOR_ARGS).item(0);
    }

    protected Element getPropertyElements(String beanName) throws IOException, SAXException, ParserConfigurationException {
        return (Element) getBeanElementByName(beanName).getElementsByTagName(BEAN_PROPERTY).item(0);
    }

    private NodeList getBeanElements() throws ParserConfigurationException, SAXException, IOException {
        return XmlParser.parseXml(file).getDocumentElement().getElementsByTagName(BEAN);
    }
}
