package org.brightify.torch.impl.filter;

import org.brightify.torch.ReadableRawContainer;
import org.brightify.torch.WritableRawContainer;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class LongPropertyImpl<OWNER> extends NumberPropertyImpl<OWNER, Long> {
    public LongPropertyImpl(Class<OWNER> owner, String name, String safeName) {
        super(owner, Long.class, name, safeName);
    }

    @Override
    public void readFromRawContainer(ReadableRawContainer container, OWNER entity) {
        set(entity, container.getLong());
    }

    @Override
    public void writeToRawContainer(OWNER entity, WritableRawContainer container) {
        container.put(get(entity));
    }

    @Override
    public LongPropertyImpl<OWNER> defaultValue(Long defaultValue) {
        super.defaultValue(defaultValue);
        return this;
    }

    @Override
    public LongPropertyImpl<OWNER> feature(Feature feature) {
        super.feature(feature);
        return this;
    }
}
