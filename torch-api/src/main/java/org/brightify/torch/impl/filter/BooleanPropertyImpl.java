package org.brightify.torch.impl.filter;

import org.brightify.torch.filter.BooleanProperty;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class BooleanPropertyImpl extends PropertyImpl<Boolean> implements BooleanProperty {

    public BooleanPropertyImpl(String name, String safeName, Feature... features) {
        super(name, safeName, Boolean.class, features);
    }

}
