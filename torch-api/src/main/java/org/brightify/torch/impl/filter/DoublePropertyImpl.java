package org.brightify.torch.impl.filter;

import org.brightify.torch.ReadableRawContainer;
import org.brightify.torch.WritableRawContainer;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class DoublePropertyImpl<OWNER> extends NumberPropertyImpl<OWNER, Double> {
    public DoublePropertyImpl(Class<OWNER> owner, String name, String safeName) {
        super(owner, Double.class, name, safeName);
    }

    @Override
    public void readFromRawContainer(ReadableRawContainer container, OWNER entity) {
        set(entity, container.getDouble());
    }

    @Override
    public void writeToRawContainer(OWNER entity, WritableRawContainer container) {
        container.put(get(entity));
    }

    @Override
    public DoublePropertyImpl<OWNER> defaultValue(Double defaultValue) {
        super.defaultValue(defaultValue);
        return this;
    }

    @Override
    public DoublePropertyImpl<OWNER> feature(Feature feature) {
        super.feature(feature);
        return this;
    }
}
