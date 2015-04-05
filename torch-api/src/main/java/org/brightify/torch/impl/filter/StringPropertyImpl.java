package org.brightify.torch.impl.filter;

import org.brightify.torch.ReadableRawContainer;
import org.brightify.torch.WritableRawContainer;
import org.brightify.torch.filter.ContainsStringFilter;
import org.brightify.torch.filter.EndsWithStringFilter;
import org.brightify.torch.filter.StartsWithStringFilter;
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
    public StartsWithStringFilter<OWNER> startsWith(String value) {
        // FIXME add escaping of input value!
        return new StartsWithStringFilter<OWNER>(this, value);
    }

    @Override
    public EndsWithStringFilter<OWNER> endsWith(String value) {
        return new EndsWithStringFilter<OWNER>(this, value);
    }

    @Override
    public ContainsStringFilter<OWNER> contains(String value) {
        return new ContainsStringFilter<OWNER>(this, value);
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
