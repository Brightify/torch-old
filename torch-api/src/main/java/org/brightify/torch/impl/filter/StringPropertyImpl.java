package org.brightify.torch.impl.filter;

import org.brightify.torch.filter.ContainsStringFilter;
import org.brightify.torch.filter.EndsWithStringFilter;
import org.brightify.torch.filter.StartsWithStringFilter;
import org.brightify.torch.filter.StringProperty;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class StringPropertyImpl extends GenericPropertyImpl<String> implements StringProperty {

    public StringPropertyImpl(String name, String safeName, Feature... features) {
        super(name, safeName, String.class, features);
    }

    @Override
    public StartsWithStringFilter startsWith(String value) {
        // FIXME add escaping of input value!
        return new StartsWithStringFilter(this, value);
    }

    @Override
    public EndsWithStringFilter endsWith(String value) {
        return new EndsWithStringFilter(this, value);
    }

    @Override
    public ContainsStringFilter contains(String value) {
        return new ContainsStringFilter(this, value);
    }
}
