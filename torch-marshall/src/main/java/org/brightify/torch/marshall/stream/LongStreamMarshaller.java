package org.brightify.torch.marshall.stream;

import org.brightify.torch.marshall.SymetricStreamMarshaller;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class LongStreamMarshaller implements SymetricStreamMarshaller<Long> {

    private static LongStreamMarshaller instance;

    @Override
    public void marshall(DataOutputStream outputStream, Long value) throws Exception {
        outputStream.writeLong(value);
    }

    @Override
    public Long unmarshall(DataInputStream inputStream) throws Exception {
        return inputStream.readLong();
    }

    public static LongStreamMarshaller getInstance() {
        if (instance == null) {
            instance = new LongStreamMarshaller();
        }
        return instance;
    }
}
