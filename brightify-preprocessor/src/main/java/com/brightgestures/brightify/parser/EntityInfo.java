package com.brightgestures.brightify.parser;

import com.brightgestures.brightify.util.MigrationType;

import javax.lang.model.element.Element;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class EntityInfo {
    public Element entityElement;

    public String entityName;
    public String entityFullName;
    public String tableName;

    public boolean delete;

    public int version;
    public MigrationType migrationType;

    public List<Property> properties;
    public Property idProperty;
}
