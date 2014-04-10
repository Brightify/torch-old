package org.brightify.torch.marshall.stream;

import org.brightify.torch.marshall.SymetricStreamMarshaller;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class ShortStreamMarshaller implements SymetricStreamMarshaller<Short> {

    private static ShortStreamMarshaller instance;

    @Override
    public void marshall(DataOutputStream outputStream, Short value) throws Exception {
        outputStream.writeShort(value);
    }

    @Override
    public Short unmarshall(DataInputStream inputStream) throws Exception {
        return inputStream.readShort();
    }

    public static ShortStreamMarshaller getInstance() {
        if (instance == null) {
            instance = new ShortStreamMarshaller();
        }
        return instance;
    }
}
