package org.brightify.torch.filter;

import org.brightify.torch.action.load.EntityFilter;
import org.brightify.torch.filter.Column;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class ColumnImpl<T> implements Column<T> {

    private final String columnName;

    public ColumnImpl(String columnName) {
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
