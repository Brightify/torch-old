package org.brightify.torch.marshall;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface Marshaller<T> {

    T unmarshall(DataInputStream inputStream) throws IOException;

    void marshall(DataOutputStream outputStream, T value) throws IOException;

}
