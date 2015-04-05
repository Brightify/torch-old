package org.brightify.torch.impl.filter;

import org.brightify.torch.ReadableRawContainer;
import org.brightify.torch.WritableRawContainer;
import org.brightify.torch.filter.BooleanProperty;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class BooleanPropertyImpl<OWNER> extends PropertyImpl<OWNER, Boolean>
        implements BooleanProperty<OWNER> {

    public BooleanPropertyImpl(Class<OWNER> owner, String name, String safeName) {
        super(owner, Boolean.class, name, safeName);
    }

    @Override
    public void readFromRawContainer(ReadableRawContainer container, OWNER entity) {
        set(entity, container.getBoolean());
    }

    @Override
    public void writeToRawContainer(OWNER entity, WritableRawContainer container) throws Exception {
        container.put(get(entity));
    }

    @Override
    public BooleanPropertyImpl<OWNER> defaultValue(Boolean defaultValue) {
        super.defaultValue(defaultValue);
        return this;
    }

    @Override
    public BooleanPropertyImpl<OWNER> feature(Feature feature) {
        super.feature(feature);
        return this;
    }
}
