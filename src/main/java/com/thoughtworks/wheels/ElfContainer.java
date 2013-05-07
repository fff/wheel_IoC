package com.thoughtworks.wheels;

import com.google.common.collect.ImmutableMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static com.thoughtworks.wheels.Constants.BEAN;
import static com.thoughtworks.wheels.Constants.BEAN_ID;

public class ElfContainer {
    protected ElfContainer father;
    private Map<String, BeanWrapper<Object>> idWrapperMap;
    private Stack circleDependencyLock = new Stack();

    public ElfContainer(String fileAddress) {
        this(null, fileAddress);
    }

    public ElfContainer(ElfContainer father, String fileAddress) {
        this.father = father;
        File file = new File(fileAddress);
        try {
            this.initial(XmlParser.parseXml(file));
        } catch (Exception e) {
            throw new RuntimeException("Bean Definition Loading failed.", e);
        }
        relate();
    }

    private void initial(Document document) {
        NodeList beanList = document.getDocumentElement().getElementsByTagName(BEAN);
        final ImmutableMap.Builder<String, BeanWrapper<Object>> builder = ImmutableMap.builder();
        // step1 simple object
        for (int i = 0; i < beanList.getLength(); i++) {
            Element item = (Element) beanList.item(i);
            final String beanId = item.getAttribute(BEAN_ID);
            builder.put(beanId, new BeanWrapper(beanId, item));
        }
        idWrapperMap = builder.build();
    }

    private void relate() {
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

    private BeanWrapper initialBeansIfHasConstructorArgs(BeanWrapper wrap) {
        if (wrap.instance != null) return wrap;
        if (circleDependencyLock.search(wrap) > 0) {
            throw new CircleDependencyException("circle dependency found:" + wrap.beanId);
        }
        circleDependencyLock.push(wrap);
        final List<ConstructorArg> constructorRefs = wrap.getConstructorRefs();
        final Map<String, BeanWrapper<?>> argRefs = new HashMap<>(constructorRefs.size());
        for (ConstructorArg arg : constructorRefs) {
            argRefs.put(arg.ref, initialBeansIfHasConstructorArgs(getWrapperById(arg.ref)));
        }
        wrap.initialByConstructorArgs(argRefs);
        this.circleDependencyLock.pop();
        return wrap;
    }

    public ElfContainer createChild(String childConfigFilePath) {
        return new ElfContainer(this, childConfigFilePath);
    }

    public <T> T getBean(String beanId) {
        return (T) getWrapperById(beanId).instance;
    }

    protected BeanWrapper getWrapperById(String beanId) {
        if (this.idWrapperMap.containsKey(beanId)) return this.idWrapperMap.get(beanId);
        if (this.father == null) throw new RuntimeException("No Value with id:" + beanId + " exist.");
        return this.father.getWrapperById(beanId);
    }

}
