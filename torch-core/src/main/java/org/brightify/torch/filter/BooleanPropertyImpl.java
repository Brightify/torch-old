package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class BooleanPropertyImpl implements BooleanProperty {

    private final PropertyImpl<Integer> realColumn;

    public BooleanPropertyImpl(String columnName) {
        realColumn = new PropertyImpl<Integer>(columnName);
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
