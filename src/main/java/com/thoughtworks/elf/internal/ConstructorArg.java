package com.thoughtworks.elf.internal;

import com.thoughtworks.elf.utils.Elem;

public class ConstructorArg {
    public final String ref;
    public final String var;
    public final Class<?> varType = String.class;
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