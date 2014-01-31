package org.brightify.torch.parse;

import org.brightify.torch.annotation.Id;
import org.brightify.torch.annotation.Index;
import org.brightify.torch.annotation.NotNull;
import org.brightify.torch.annotation.Unique;

import javax.lang.model.type.TypeMirror;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public abstract class Property {

    private Id id;
    private Index index;
    private NotNull notNull;
    private Unique unique;
    private String columnName;
    private TypeMirror type;

    public abstract String setValue(String value);

    public abstract String getValue();

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Index getIndex() {
        return index;
    }

    public void setIndex(Index index) {
        this.index = index;
    }

    public NotNull getNotNull() {
        return notNull;
    }

    public void setNotNull(NotNull notNull) {
        this.notNull = notNull;
    }

    public Unique getUnique() {
        return unique;
    }

    public void setUnique(Unique unique) {
        this.unique = unique;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public TypeMirror getType() {
        return type;
    }

    public void setType(TypeMirror type) {
        this.type = type;
    }
}
