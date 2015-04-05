package org.brightify.torch.impl.filter;

import org.brightify.torch.ReadableRawContainer;
import org.brightify.torch.WritableRawContainer;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class ShortPropertyImpl<OWNER> extends NumberPropertyImpl<OWNER, Short> {

    public ShortPropertyImpl(Class<OWNER> owner, String name, String safeName) {
        super(owner, Short.class, name, safeName);
    }

    @Override
    public void readFromRawContainer(ReadableRawContainer container, OWNER entity) {
        set(entity, container.getShort());
    }

    @Override
    public void writeToRawContainer(OWNER entity, WritableRawContainer container) {
        container.put(get(entity));
    }

    @Override
    public ShortPropertyImpl<OWNER> defaultValue(Short defaultValue) {
        super.defaultValue(defaultValue);
        return this;
    }

    @Override
    public ShortPropertyImpl<OWNER> feature(Feature feature) {
        super.feature(feature);
        return this;
    }
}
