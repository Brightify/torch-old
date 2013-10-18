package com.brightgestures.brightify.parse;

import com.brightgestures.brightify.util.MigrationType;

import javax.lang.model.element.Element;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class EntityInfo {
    public Element element;

    public String name;
    public String fullName;
    public String tableName;

    public boolean delete;

    public int version;
    public MigrationType migrationType;

    public List<Property> properties;
    public Property idProperty;
}
