package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class PropertyImpl<T> implements Property<T> {

    private final String columnName;

    public PropertyImpl(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String getName() {
        return columnName;
    }

    @Override
    public EntityFilter equalTo(T value) {
        return EntityFilter.filter(getName() + " = ?", value);
    }

    @Override
    public EntityFilter notEqualTo(T value) {
        return EntityFilter.filter(getName() + " != ?", value);
    }


}
