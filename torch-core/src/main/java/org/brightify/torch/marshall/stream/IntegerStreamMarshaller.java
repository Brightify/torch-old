package org.brightify.torch.marshall.stream;

import org.brightify.torch.marshall.SymetricStreamMarshaller;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class IntegerStreamMarshaller implements SymetricStreamMarshaller<Integer> {

    private static IntegerStreamMarshaller instance;

    @Override
    public void marshall(DataOutputStream outputStream, Integer value) throws Exception {
        outputStream.writeInt(value);
    }

    @Override
    public Integer unmarshall(DataInputStream inputStream) throws Exception {
        return inputStream.readInt();
    }

    public static IntegerStreamMarshaller getInstance() {
        if (instance == null) {
            instance = new IntegerStreamMarshaller();
        }
        return instance;
    }
}
