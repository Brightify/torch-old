package org.brightify.torch.impl.filter;

import org.brightify.torch.ReadableRawContainer;
import org.brightify.torch.WritableRawContainer;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class FloatPropertyImpl<OWNER> extends NumberPropertyImpl<OWNER, Float> {

    public FloatPropertyImpl(Class<OWNER> owner, String name, String safeName) {
        super(owner, Float.class, name, safeName);
    }

    @Override
    public void readFromRawContainer(ReadableRawContainer container, OWNER entity) {
        set(entity, container.getFloat());
    }

    @Override
    public void writeToRawContainer(OWNER entity, WritableRawContainer container) {
        container.put(get(entity));
    }

    @Override
    public FloatPropertyImpl<OWNER> defaultValue(Float defaultValue) {
        super.defaultValue(defaultValue);
        return this;
    }

    @Override
    public FloatPropertyImpl<OWNER> feature(Feature feature) {
        super.feature(feature);
        return this;
    }
}
