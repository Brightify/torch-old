package org.brightify.torch.filter;

import org.brightify.torch.ReadableRawContainer;
import org.brightify.torch.WritableRawContainer;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface ValueProperty<OWNER, TYPE> extends Property<OWNER, TYPE> {

    void readFromRawContainer(ReadableRawContainer container, OWNER entity) throws Exception;

    void writeToRawContainer(OWNER entity, WritableRawContainer container) throws Exception;

}
