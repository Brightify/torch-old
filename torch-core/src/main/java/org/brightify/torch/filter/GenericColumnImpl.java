package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class GenericColumnImpl<T> extends ColumnImpl<T> implements GenericColumn<T> {

    public GenericColumnImpl(String columnName) {
        super(columnName);
    }

    @Override
    public EntityFilter in(T... values) {
        StringBuilder builder = new StringBuilder(getName());
        builder.append(" IN (");
        for(int i = 0; i < values.length; i++) {
            if(i > 0) {
                builder.append(", ");
            }
            builder.append("?");
        }
        builder.append(")");
        return EntityFilter.filter(builder.toString(), values);
    }

    @Override
    public EntityFilter notIn(T... values) {
        StringBuilder builder = new StringBuilder(getName());
        builder.append(" NOT IN (");
        for(int i = 0; i < values.length; i++) {
            if(i>0) {
                builder.append(", ");
            }
            builder.append("?");
        }
        builder.append(")");
        return EntityFilter.filter(builder.toString(), values);
    }
}
