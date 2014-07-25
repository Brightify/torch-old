package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class StringPropertyImpl extends GenericPropertyImpl<String> implements StringProperty {

    public StringPropertyImpl(String name, String safeName, Feature... features) {
        super(name, safeName, String.class, features);
    }

    @Override
    public EntityFilter startsWith(String value) {
        // FIXME add escaping of input value!
        return EntityFilter.filter(getName() + " LIKE ?", value + "%");
    }

    @Override
    public EntityFilter endsWith(String value) {
        return EntityFilter.filter(getSafeName() + " LIKE ?", "%" + value);
    }

    @Override
    public EntityFilter contains(String value) {
        return EntityFilter.filter(getSafeName() + " LIKE ?", "%" + value + "%");
    }
}
