package org.brightify.torch.compile;

import org.brightify.torch.annotation.Entity;
import org.brightify.torch.compile.migration.MigrationPath;

import javax.lang.model.element.Element;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class EntityMirrorImpl implements EntityMirror {
    private Element element;

    private String name;
    private String packageName;
    private String fullName;
    private String tableName;

    private boolean delete;
    private boolean ignored;

    private String version;
    private Entity.MigrationType migrationType;
    private List<MigrationPath> migrationPaths = new ArrayList<MigrationPath>();

    private List<PropertyMirror> properties = new ArrayList<PropertyMirror>();
    private PropertyMirror idPropertyMirror;

    @Override
    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    @Override
    public String getSimpleName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String getSafeName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    @Override
    public boolean isIgnored() {
        return ignored;
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public Entity.MigrationType getMigrationType() {
        return migrationType;
    }

    public void setMigrationType(Entity.MigrationType migrationType) {
        this.migrationType = migrationType;
    }

    @Override
    public List<MigrationPath> getMigrationPaths() {
        return migrationPaths;
    }

    public void setMigrationPaths(List<MigrationPath> migrationPaths) {
        this.migrationPaths = migrationPaths;
    }

    @Override
    public List<PropertyMirror> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyMirror> properties) {
        this.properties = properties;
    }

    @Override
    public PropertyMirror getIdPropertyMirror() {
        return idPropertyMirror;
    }

    public void setIdPropertyMirror(PropertyMirror idPropertyMirror) {
        this.idPropertyMirror = idPropertyMirror;
    }
}
