package com.brightgestures.brightify.parse;

import com.brightgestures.brightify.annotation.Id;

import javax.lang.model.type.TypeMirror;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public abstract class Property {

    public Id id;
    public boolean index;
    public boolean notNull;
    public boolean unique;
    public String columnName;
    public TypeMirror type;

    public abstract String setValue(String value);

    public abstract String getValue();

}
