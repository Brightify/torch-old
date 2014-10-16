package org.brightify.torch.impl.filter;

import org.brightify.torch.filter.EqualToFilter;
import org.brightify.torch.filter.NotEqualToFilter;
import org.brightify.torch.filter.Property;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class PropertyImpl<TYPE> implements Property<TYPE> {

    private final String name;
    private final String safeName;
    private final Class<TYPE> type;
    private final Feature[] features;

    public PropertyImpl(String name, String safeName, Class<TYPE> type, Feature... features) {
        this.name = name;
        this.safeName = safeName;
        this.type = type;
        this.features = features;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSafeName() {
        return safeName;
    }

    @Override
    public Class<TYPE> getType() {
        return type;
    }

    @Override
    public Feature[] getFeatures() {
        return features;
    }

    @Override
    public EqualToFilter<?> equalTo(TYPE value) {
        return new EqualToFilter<TYPE>(this, value);
    }

    @Override
    public NotEqualToFilter<?> notEqualTo(TYPE value) {
        return new NotEqualToFilter<TYPE>(this, value);
    }

}
