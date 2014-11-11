package org.brightify.torch.impl.filter;

import org.brightify.torch.filter.BooleanProperty;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class BooleanPropertyImpl extends PropertyImpl<Boolean> implements BooleanProperty {

    public BooleanPropertyImpl(String name, String safeName) {
        super(Boolean.class, name, safeName);
    }

    @Override
    public BooleanPropertyImpl defaultValue(Boolean defaultValue) {
        super.defaultValue(defaultValue);
        return this;
    }

    @Override
    public BooleanPropertyImpl feature(Feature feature) {
        super.feature(feature);
        return this;
    }
}
