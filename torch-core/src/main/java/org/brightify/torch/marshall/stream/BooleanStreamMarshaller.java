package org.brightify.torch.marshall.stream;

import org.brightify.torch.marshall.SymetricStreamMarshaller;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class BooleanStreamMarshaller implements SymetricStreamMarshaller<Boolean> {

    private static BooleanStreamMarshaller instance;

    @Override
    public void marshall(DataOutputStream outputStream, Boolean value) throws Exception {
        outputStream.writeBoolean(value);
    }

    @Override
    public Boolean unmarshall(DataInputStream inputStream) throws Exception {
        return inputStream.readBoolean();
    }

    public static BooleanStreamMarshaller getInstance() {
        if (instance == null) {
            instance = new BooleanStreamMarshaller();
        }
        return instance;
    }
}
