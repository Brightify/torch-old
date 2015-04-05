package org.brightify.torch.impl.filter;

import org.brightify.torch.ReadableRawContainer;
import org.brightify.torch.WritableRawContainer;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class IntegerPropertyImpl<OWNER> extends NumberPropertyImpl<OWNER, Integer> {

    public IntegerPropertyImpl(Class<OWNER> owner, String name, String safeName) {
        super(owner, Integer.class, name, safeName);
    }

    @Override
    public void readFromRawContainer(ReadableRawContainer container, OWNER entity) {
        set(entity, container.getInteger());
    }

    @Override
    public void writeToRawContainer(OWNER entity, WritableRawContainer container) {
        container.put(get(entity));
    }

    @Override
    public IntegerPropertyImpl<OWNER> defaultValue(Integer defaultValue) {
        super.defaultValue(defaultValue);
        return this;
    }

    @Override
    public IntegerPropertyImpl<OWNER> feature(Feature feature) {
        super.feature(feature);
        return this;
    }

}
