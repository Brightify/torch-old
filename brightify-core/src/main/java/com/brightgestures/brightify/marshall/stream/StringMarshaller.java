package com.brightgestures.brightify.marshall.stream;

import com.brightgestures.brightify.marshall.SymetricStreamMarshaller;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class StringMarshaller implements SymetricStreamMarshaller<String> {

    private static StringMarshaller instance;

    @Override
    public void marshall(DataOutputStream outputStream, String value) throws Exception {
        outputStream.writeUTF(value);
    }

    @Override
    public String unmarshall(DataInputStream inputStream) throws Exception {
        return inputStream.readUTF();
    }

    public static StringMarshaller getInstance() {
        if(instance == null) {
            instance = new StringMarshaller();
        }
        return instance;
    }
}
