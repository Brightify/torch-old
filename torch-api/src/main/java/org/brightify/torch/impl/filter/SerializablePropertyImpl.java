package org.brightify.torch.impl.filter;

import org.brightify.torch.ReadableRawContainer;
import org.brightify.torch.WritableRawContainer;
import org.brightify.torch.util.Serializer;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class SerializablePropertyImpl<OWNER, TYPE extends Serializable> extends GenericPropertyImpl<OWNER, TYPE> {
    public SerializablePropertyImpl(Class<OWNER> owner, Class<TYPE> type, String name, String safeName) {
        super(owner, type, name, safeName);
    }

    @Override
    public void readFromRawContainer(ReadableRawContainer container, OWNER entity) {
        TYPE value = null;
        try {
            value = Serializer.deserialize(container.getBlob(), getType());
        } catch (IOException e) {
            // FIXME what should we do?
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // FIXME what should we do?
            e.printStackTrace();
        }
        set(entity, value);
    }

    @Override
    public void writeToRawContainer(OWNER entity, WritableRawContainer container) {
        byte[] bytes = new byte[0];
        try {
            bytes = Serializer.serialize(get(entity));
        } catch (IOException e) {
            // FIXME what should we do?
            e.printStackTrace();
        }

        container.put(bytes);
    }

    @Override
    public SerializablePropertyImpl<OWNER, TYPE> defaultValue(TYPE defaultValue) {
        super.defaultValue(defaultValue);
        return this;
    }

    @Override
    public SerializablePropertyImpl<OWNER, TYPE> feature(Feature feature) {
        super.feature(feature);
        return this;
    }
}
