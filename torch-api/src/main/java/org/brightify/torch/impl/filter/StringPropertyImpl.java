package org.brightify.torch.impl.filter;

import org.brightify.torch.ReadableRawContainer;
import org.brightify.torch.WritableRawContainer;
import org.brightify.torch.filter.BaseFilter;
import org.brightify.torch.filter.SingleValueFilter;
import org.brightify.torch.filter.StringProperty;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class StringPropertyImpl<OWNER> extends GenericPropertyImpl<OWNER, String>
        implements StringProperty<OWNER> {

    public StringPropertyImpl(Class<OWNER> owner, String name, String safeName) {
        super(owner, String.class, name, safeName);
    }

    @Override
    public BaseFilter<OWNER, String> startsWith(String value) {
        // FIXME add escaping of input value!
        return new SingleValueFilter<OWNER, String>(this, BaseFilter.FilterType.STARTS_WITH_STRING, value);
    }

    @Override
    public BaseFilter<OWNER, String> endsWith(String value) {
        return new SingleValueFilter<OWNER, String>(this, BaseFilter.FilterType.ENDS_WITH_STRING, value);
    }

    @Override
    public BaseFilter<OWNER, String> contains(String value) {
        return new SingleValueFilter<OWNER, String>(this, BaseFilter.FilterType.CONTAINS_STRING, value);
    }

    @Override
    public void readFromRawContainer(ReadableRawContainer container, OWNER entity) {
        set(entity, container.getString());
    }

    @Override
    public void writeToRawContainer(OWNER entity, WritableRawContainer container) {
        container.put(get(entity));
    }

    @Override
    public StringPropertyImpl<OWNER> defaultValue(String defaultValue) {
        super.defaultValue(defaultValue);
        return this;
    }

    @Override
    public StringPropertyImpl<OWNER> feature(Feature feature) {
        super.feature(feature);
        return this;
    }


}
