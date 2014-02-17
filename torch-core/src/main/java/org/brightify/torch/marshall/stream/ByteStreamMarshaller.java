package org.brightify.torch.marshall.stream;

import org.brightify.torch.marshall.SymetricStreamMarshaller;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class ByteStreamMarshaller implements SymetricStreamMarshaller<Byte> {

    private static ByteStreamMarshaller instance;

    @Override
    public void marshall(DataOutputStream outputStream, Byte value) throws Exception {
        outputStream.writeByte(value);
    }

    @Override
    public Byte unmarshall(DataInputStream inputStream) throws Exception {
        return inputStream.readByte();
    }

    public static ByteStreamMarshaller getInstance() {
        if (instance == null) {
            instance = new ByteStreamMarshaller();
        }
        return instance;
    }
}
