package com.thoughtworks.wheels;

public class ConstructorArg {
    final String ref;
    final String var;
    final Class<?> varType = String.class;
    BeanWrapper<?> refWrapper;

    public ConstructorArg(Elem elem) {
        this.ref = elem.getRef();
        this.var = elem.getVar();
    }

    public boolean isRef() {
        return this.ref != null;
    }

    public Class<?> getTypeClass() {
        return isRef() ? refWrapper.clazz : varType;
    }

    public Object getValueObject() {
        return isRef() ? refWrapper.instance : var;
    }

}