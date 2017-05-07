package com.xqh.financial.utils.zkconf;

import java.lang.reflect.Field;

/**
 * Created by hssh on 2017/2/16.
 */
public class ObjectField {

    private Object object;

    private Field field;

    public ObjectField(Object object, Field field) {
        this.object = object;
        this.field = field;
    }

    public Object getObject() {

        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }
}
