package com.thoughtworks.wheels;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanWrapper<T extends Object> {
    final Element element;
    final Class<T> clazz;
    final List<ConstructorArg> constructorRefs = new ArrayList<>(0);
    final List<ConstructorArg> constructorArgs = new ArrayList<>(0);
    final Map<String, String> setterRefs = new HashMap<>(0);
    final String beanId;
    T instance;

    public BeanWrapper(String name, Element element) {
        this.beanId = name;
        this.element = element;
        try {
            final Elem beanElem = new Elem(element);
            this.clazz = (Class<T>) Class.forName(beanElem.getFullClassName());
            if (beanElem.getConstructorArgs().getLength() > 0) {
                this.initialByConstructorArgs(beanElem.getConstructorArgs());
            } else {
                this.initialByProperties();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //constructor
    private void initialByConstructorArgs(NodeList constructorArgs) {
        boolean hasDependency = false;
        Class<?>[] typeArray = new Class<?>[constructorArgs.getLength()];
        Object[] valueArray = new Object[constructorArgs.getLength()];
        for (int i = 0; i < constructorArgs.getLength(); i++) {
            Elem prop = new Elem(constructorArgs.item(i));
            final ConstructorArg arg = new ConstructorArg(prop);
            this.constructorArgs.add(arg);
            if (arg.isRef()) {
                this.constructorRefs.add(arg);
                hasDependency = true;
            } else {
                typeArray[i] = arg.getTypeClass();
                valueArray[i] = arg.getValueObject();
            }
        }
        if (hasDependency) return;
        try {
            this.instance = this.clazz.getConstructor(typeArray).newInstance(valueArray);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public BeanWrapper<T> initialByConstructorArgs(Map<String, BeanWrapper<Object>> argRefs) {
        Class<?>[] typeArray = new Class<?>[this.constructorArgs.size()];
        Object[] valueArray = new Object[this.constructorArgs.size()];
        for (int i = 0; i < this.constructorArgs.size(); i++) {
            final ConstructorArg arg = this.constructorArgs.get(i);
            if (arg.isRef()) {
                final BeanWrapper<?> argRef = argRefs.get(arg.ref);
                if (argRef == null || argRef.instance == null) {
                    throw new RuntimeException("Arg-ref[" + arg.ref + " was not created");
                }
                typeArray[i] = argRef.clazz;
                valueArray[i] = argRef.instance;
            } else {
                typeArray[i] = arg.getTypeClass();
                valueArray[i] = arg.getValueObject();
            }
        }
        try {
            this.instance = this.clazz.getConstructor(typeArray).newInstance(valueArray);
            return this;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    //setter
    protected void initialByProperties() {
        try {
            this.instance = (T) clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        final NodeList properties = element.getElementsByTagName(Constants.BEAN_PROPERTY);
        for (int i = 0; i < properties.getLength(); i++) {
            Elem prop = new Elem(properties.item(i));
            if (prop.getRef() != null) {
                this.setterRefs.put(prop.getName(), prop.getRef());
                continue;
            }
            final String typeInString = prop.getType();
            final Class<?> propertyType;
            try {
                propertyType = typeInString != null ? Class.forName(typeInString) : String.class;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            this.setProperty(prop.getName(), propertyType, prop.getVar());
        }
    }

    protected Method getSetter(String propertyName, Class<?> propertyClass) {
        try {
            return clazz.getMethod(Constants.SETTER_PREFIX + wrapAString(propertyName), propertyClass);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private String wrapAString(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public void setProperty(String propertyName, Class<?> propertyClass, Object... propertyValue) {
        try {
            getSetter(propertyName, propertyClass).invoke(instance, propertyValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(propertyName + "," + propertyClass.getCanonicalName() + "," + propertyValue, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(propertyName + "," + propertyClass.getCanonicalName() + "," + propertyValue, e);
        }
    }

    public Map<String, String> getSetterRefs() {
        return setterRefs;
    }

    public List<ConstructorArg> getConstructorRefs() {
        return constructorRefs;
    }
}
