package org.brightify.torch.marshall.stream;

import org.brightify.torch.marshall.SymetricStreamMarshaller;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class StringStreamMarshaller implements SymetricStreamMarshaller<String> {

    private static StringStreamMarshaller instance;

    @Override
    public void marshall(DataOutputStream outputStream, String value) throws Exception {
        outputStream.writeUTF(value);
    }

    @Override
    public String unmarshall(DataInputStream inputStream) throws Exception {
        return inputStream.readUTF();
    }

    public static StringStreamMarshaller getInstance() {
        if(instance == null) {
            instance = new StringStreamMarshaller();
        }
        return instance;
    }
}
