package com.brightgestures.brightify.marshall;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface StreamMarshaller<T> {

    void marshall(DataOutputStream outputStream, T value);

    T unmarshall(DataInputStream inputStream);

}
