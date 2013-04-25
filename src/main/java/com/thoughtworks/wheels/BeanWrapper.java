package com.thoughtworks.wheels;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.wheels.Constants.*;

/**
 * User: fff
 * Date: 4/25/13
 * Time: 7:31 PM
 */
public class BeanWrapper<T extends Object> {
    final Element element;
    final Class<T> clazz;
    final T instance;
    final List<String> refs = new ArrayList<>(0);

    public BeanWrapper(Element element) {
        this.element = element;
        try {
            final Class clazz = Class.forName(element.getAttribute(Constants.CLASS_FULL_NAME));
            this.clazz = clazz;
            this.instance = (T) clazz.newInstance();
            this.initialProperties();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected void initialProperties() {
        final NodeList properties = element.getElementsByTagName(Constants.BEAN_PROPERTY);
        for (int i = 0; i < properties.getLength(); i++) {
            final Element property = (Element) properties.item(i);
            if (property.getAttribute(PROPERTY_REF).length() > 0) {
                this.refs.add(property.getAttribute(PROPERTY_REF));
                continue;
            }
            final String typeInString = property.getAttribute(PROPERTY_TYPE);
            final Class<?> propertyType;
            try {
                propertyType = typeInString.length() > 0 ? Class.forName(typeInString) : String.class;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            this.setProperty(property.getAttribute(PROPERTY_NAME), propertyType, property.getAttribute(PROPERTY_VAR));
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

    public List<String> getRefs() {
        return refs;
    }
}
