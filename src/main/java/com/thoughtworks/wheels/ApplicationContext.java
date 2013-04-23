package com.thoughtworks.wheels;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class ApplicationContext {

    public static final String BEAN = "bean";
    public static final String BEAN_PROPERTY = "property";
    public static final String BEAN_CONSTRUCTOR_ARGS = "constructor-args";

    private final File file;

    public ApplicationContext(String fileAddress) {
        this.file = new File(fileAddress);
    }

    public Object getBean(String beanName) {
        Object obj = null;
        try {
            Element beanElement = getBeanElement(beanName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Element getBeanElement(String beanName) throws ParserConfigurationException, SAXException, IOException {
        return (Element) XmlParser.parseXml(file).getDocumentElement().getElementsByTagName(BEAN).item(0);
    }

    protected Element getConstructorArgsElement(String beanName) throws IOException, SAXException, ParserConfigurationException {
        return (Element) getBeanElement(beanName).getElementsByTagName(BEAN_CONSTRUCTOR_ARGS).item(0);
    }

    protected Element getPropertiesElement(String beanName) throws IOException, SAXException, ParserConfigurationException {
        return (Element) getBeanElement(beanName).getElementsByTagName(BEAN_PROPERTY).item(0);
    }
}
