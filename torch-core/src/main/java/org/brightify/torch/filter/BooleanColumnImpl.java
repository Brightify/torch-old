package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class BooleanColumnImpl implements BooleanColumn {

    private final ColumnImpl<Integer> realColumn;

    public BooleanColumnImpl(String columnName) {
        realColumn = new ColumnImpl<Integer>(columnName);
    }

    @Override
    public String getName() {
        return realColumn.getName();
    }

    @Override
    public EntityFilter equalTo(Boolean value) {
        return realColumn.equalTo(convert(value));
    }

    @Override
    public EntityFilter notEqualTo(Boolean value) {
        return realColumn.notEqualTo(convert(value));
    }

    private Integer convert(Boolean value) {
        return value != null && value ? 1 : 0;
    }
}
