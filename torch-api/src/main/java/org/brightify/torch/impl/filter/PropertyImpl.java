package org.brightify.torch.impl.filter;

import org.brightify.torch.filter.EqualToFilter;
import org.brightify.torch.filter.NotEqualToFilter;
import org.brightify.torch.filter.Property;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class PropertyImpl<OWNER, TYPE> implements Property<OWNER, TYPE> {

    private final Class<OWNER> owner;
    private final Class<TYPE> type;
    private final String name;
    private final String safeName;
    private Set<Feature> features = new HashSet<Feature>();
    private TYPE defaultValue;

    public PropertyImpl(Class<OWNER> owner, Class<TYPE> type, String name, String safeName) {
        this.owner = owner;
        this.type = type;
        this.name = name;
        this.safeName = safeName;
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
    public Class<OWNER> getOwnerType() {
        return owner;
    }

    @Override
    public Class<TYPE> getType() {
        return type;
    }

    @Override
    public Set<Feature> getFeatures() {
        return features;
    }

    @Override
    public TYPE getDefaultValue() {
        return defaultValue;
    }

    @Override
    public EqualToFilter<OWNER, TYPE> equalTo(TYPE value) {
        return new EqualToFilter<OWNER, TYPE>(this, value);
    }

    @Override
    public NotEqualToFilter<OWNER, TYPE> notEqualTo(TYPE value) {
        return new NotEqualToFilter<OWNER, TYPE>(this, value);
    }

    public PropertyImpl<OWNER, TYPE> feature(Feature feature) {
        features.add(feature);
        return this;
    }

    public PropertyImpl<OWNER, TYPE> defaultValue(TYPE defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

}
