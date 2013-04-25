package com.thoughtworks.wheels;

import com.google.common.collect.ImmutableMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.Map;

import static com.thoughtworks.wheels.Constants.BEAN;
import static com.thoughtworks.wheels.Constants.BEAN_ID;

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
        for (BeanWrapper wrap : ID_WRAPPER_MAP.values()) {
            final Map<String, String> refs = wrap.getRefs();
            if (refs.isEmpty()) continue;
            for (String name : refs.keySet()) {
                final BeanWrapper<Object> ref = getWrapperById(name);
                wrap.setProperty(name, ref.clazz, ref.instance);
            }
        }
    }

    public <T extends Object> T getBean(String beanId) {
        return (T) getWrapperById(beanId).instance;
    }

    protected BeanWrapper getWrapperById(String beanId) {
        if (!ID_WRAPPER_MAP.containsKey(beanId)) throw new RuntimeException("No Value with id:" + beanId + " exist.");
        return ID_WRAPPER_MAP.get(beanId);
    }

}
