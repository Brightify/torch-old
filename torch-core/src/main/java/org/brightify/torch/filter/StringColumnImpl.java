package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class StringColumnImpl extends GenericColumnImpl<String> implements StringColumn {

    public StringColumnImpl(String columnName) {
        super(columnName);
    }

    @Override
    public EntityFilter startsWith(String value) {
        // FIXME add escaping of input value!
        return EntityFilter.filter(getName() + " LIKE ?", value + "%");
    }

    @Override
    public EntityFilter endsWith(String value) {
        return EntityFilter.filter(getName() + " LIKE ?", "%" + value);
    }

    @Override
    public EntityFilter contains(String value) {
        return EntityFilter.filter(getName() + " LIKE ?", "%" + value + "%");
    }
}
