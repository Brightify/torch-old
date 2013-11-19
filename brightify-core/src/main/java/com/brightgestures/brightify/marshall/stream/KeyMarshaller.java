package com.brightgestures.brightify.marshall.stream;

import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.marshall.StreamMarshaller;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class KeyMarshaller<ENTITY> implements StreamMarshaller<Key<ENTITY>> {

    private final Class<ENTITY> entityClass;

    public KeyMarshaller(Class<ENTITY> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public void marshall(DataOutputStream outputStream, Key<ENTITY> value) {

    }

    @Override
    public Key<ENTITY> unmarshall(DataInputStream inputStream) {
        return null;
    }
}
