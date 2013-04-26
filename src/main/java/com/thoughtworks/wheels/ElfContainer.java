package com.thoughtworks.wheels;

import com.google.common.collect.ImmutableMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.*;

import static com.thoughtworks.wheels.Constants.BEAN;
import static com.thoughtworks.wheels.Constants.BEAN_ID;

public class ElfContainer {
    protected ElfContainer father;
    private Map<String, BeanWrapper<Object>> idWrapperMap;
    private Stack circleDependencyLock = new Stack();
    private List<ElfContainer> children = new ArrayList<>();

    public ElfContainer(String fileAddress) {
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
            final String beanId = item.getAttribute(BEAN_ID);
            builder.put(beanId, new BeanWrapper(beanId, item));
        }
        idWrapperMap = builder.build();
    }

    public ElfContainer addChild(ElfContainer child) {
        child.father = this;
        this.children.add(child);
        return this;
    }

    public ElfContainer start() {
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

        // start children
        for (ElfContainer child : this.children) {
            child.start();
        }
        return this;
    }

    protected BeanWrapper initialBeansIfHasConstructorArgs(BeanWrapper wrap) {
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

    public <T extends Object> T getBean(String beanId) {
        return (T) getWrapperById(beanId).instance;
    }

    protected BeanWrapper getWrapperById(String beanId) {
        if (this.idWrapperMap.containsKey(beanId)) return this.idWrapperMap.get(beanId);
        if (this.father == null) throw new RuntimeException("No Value with id:" + beanId + " exist.");
        return this.father.getWrapperById(beanId);
    }


}
