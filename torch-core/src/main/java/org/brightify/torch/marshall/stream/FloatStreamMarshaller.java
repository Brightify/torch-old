package org.brightify.torch.marshall.stream;

import org.brightify.torch.marshall.SymetricStreamMarshaller;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class FloatStreamMarshaller implements SymetricStreamMarshaller<Float> {

    private static FloatStreamMarshaller instance;

    @Override
    public void marshall(DataOutputStream outputStream, Float value) throws Exception {
        outputStream.writeFloat(value);
    }

    @Override
    public Float unmarshall(DataInputStream inputStream) throws Exception {
        return inputStream.readFloat();
    }

    public static FloatStreamMarshaller getInstance() {
        if (instance == null) {
            instance = new FloatStreamMarshaller();
        }
        return instance;
    }
}
