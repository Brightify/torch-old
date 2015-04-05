package org.brightify.torch.impl.filter;

import org.brightify.torch.ReadableRawContainer;
import org.brightify.torch.WritableRawContainer;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class BytePropertyImpl<OWNER> extends NumberPropertyImpl<OWNER, Byte> {

    public BytePropertyImpl(Class<OWNER> owner, String name, String safeName) {
        super(owner, Byte.class, name, safeName);
    }

    @Override
    public void readFromRawContainer(ReadableRawContainer container, OWNER entity) {
        set(entity, container.getByte());
    }

    @Override
    public void writeToRawContainer(OWNER entity, WritableRawContainer container) {
        container.put(get(entity));
    }

    @Override
    public BytePropertyImpl<OWNER> defaultValue(Byte defaultValue) {
        super.defaultValue(defaultValue);
        return this;
    }

    @Override
    public BytePropertyImpl<OWNER> feature(Feature feature) {
        super.feature(feature);
        return this;
    }
}
