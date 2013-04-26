package com.thoughtworks.wheels;

import com.google.common.collect.ImmutableMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static com.thoughtworks.wheels.Constants.BEAN;
import static com.thoughtworks.wheels.Constants.BEAN_ID;

public class ApplicationContext {


    private Map<String, BeanWrapper<Object>> idWrapperMap;
    private Stack circleDependencyLock = new Stack();

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
        // step1 simple object
        for (int i = 0; i < beanList.getLength(); i++) {
            Element item = (Element) beanList.item(i);
            builder.put(item.getAttribute(BEAN_ID), new BeanWrapper(item));
        }
        idWrapperMap = builder.build();

        // step2 constructor ref
        for (BeanWrapper wrap : idWrapperMap.values()) {
            initialBeansIfHasConstructorArgs(wrap);
        }

        // step3 setter ref
        for (BeanWrapper wrap : idWrapperMap.values()) {
            final Map<String, String> refs = wrap.getSetterRefs();
            if (refs.isEmpty()) continue;
            for (String name : refs.keySet()) {
                final BeanWrapper<Object> ref = getWrapperById(name);
                wrap.setProperty(name, ref.clazz, ref.instance);
            }
        }
    }

    protected BeanWrapper initialBeansIfHasConstructorArgs(BeanWrapper wrap) {
        if (wrap.instance != null) return wrap;
        if (circleDependencyLock.search(wrap) > 0) {
            throw new RuntimeException("circle dependency found:" + wrap.clazz.toString());
        }
        circleDependencyLock.push(wrap);
        final List<ConstructorArg> constructorRefs = wrap.getConstructorRefs();
        for (ConstructorArg arg : constructorRefs) {
            initialBeansIfHasConstructorArgs(getWrapperById(arg.ref));
        }
        wrap.initialByConstructorArgs(idWrapperMap);
        this.circleDependencyLock.pop();
        return wrap;
    }

    public <T extends Object> T getBean(String beanId) {
        return (T) getWrapperById(beanId).instance;
    }

    protected BeanWrapper getWrapperById(String beanId) {
        if (!idWrapperMap.containsKey(beanId)) throw new RuntimeException("No Value with id:" + beanId + " exist.");
        return idWrapperMap.get(beanId);
    }

}
