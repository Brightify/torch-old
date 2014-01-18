package com.brightgestures.brightify.parse;

import com.brightgestures.brightify.annotation.Entity;

import javax.lang.model.element.Element;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class EntityInfo {
    public Element element;

    public String name;
    public String packageName;
    public String fullName;
    public String tableName;

    public boolean delete;

    public String version;
    public Entity.MigrationType migrationType;
    public List<MigrationMethod> migrationMethods = new ArrayList<MigrationMethod>();
    public List<MigrationPath> migrationPaths;

    public List<Property> properties;
    public Property idProperty;
}
