package org.brightify.torch.marshall;

import org.brightify.torch.ReadableRawEntity;
import org.brightify.torch.sql.TypeAffinity;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface CursorMarshaller<INPUT_TYPE, OUTPUT_TYPE extends INPUT_TYPE> {

    void marshall(ReadableRawEntity readableRawEntity, String propertyName, INPUT_TYPE value) throws Exception;

    OUTPUT_TYPE unmarshall(ReadableRawEntity readableRawEntity, String propertyName) throws Exception;

    TypeAffinity getAffinity();

}
