package org.brightify.torch.marshall.stream;

import org.brightify.torch.marshall.SymetricStreamMarshaller;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class DoubleStreamMarshaller implements SymetricStreamMarshaller<Double> {

    private static DoubleStreamMarshaller instance;

    @Override
    public void marshall(DataOutputStream outputStream, Double value) throws Exception {
        outputStream.writeDouble(value);
    }

    @Override
    public Double unmarshall(DataInputStream inputStream) throws Exception {
        return inputStream.readDouble();
    }

    public static DoubleStreamMarshaller getInstance() {
        if (instance == null) {
            instance = new DoubleStreamMarshaller();
        }
        return instance;
    }
}
