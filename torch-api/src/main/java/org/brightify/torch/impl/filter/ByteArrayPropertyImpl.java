package org.brightify.torch.impl.filter;

import org.brightify.torch.ReadableRawContainer;
import org.brightify.torch.WritableRawContainer;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class ByteArrayPropertyImpl<OWNER> extends GenericPropertyImpl<OWNER, byte[]> {
    public ByteArrayPropertyImpl(Class<OWNER> owner, String name, String safeName) {
        super(owner, byte[].class, name, safeName);
    }

    @Override
    public void readFromRawContainer(ReadableRawContainer container, OWNER entity) {
        set(entity, container.getBlob());
    }

    @Override
    public void writeToRawContainer(OWNER entity, WritableRawContainer container) {
        container.put(get(entity));
    }

    @Override
    public ByteArrayPropertyImpl<OWNER> defaultValue(byte[] defaultValue) {
        super.defaultValue(defaultValue);
        return this;
    }

    @Override
    public ByteArrayPropertyImpl<OWNER> feature(Feature feature) {
        super.feature(feature);
        return this;
    }
}
