package org.brightify.torch.impl.filter;

import org.brightify.torch.filter.EqualToFilter;
import org.brightify.torch.filter.NotEqualToFilter;
import org.brightify.torch.filter.Property;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class PropertyImpl<TYPE> implements Property<TYPE> {

    private final Class<TYPE> type;
    private final String name;
    private final String safeName;
    private Set<Feature> features = new HashSet<Feature>();
    private TYPE defaultValue;

    public PropertyImpl(Class<TYPE> type, String name, String safeName) {
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
    public EqualToFilter<?> equalTo(TYPE value) {
        return new EqualToFilter<TYPE>(this, value);
    }

    @Override
    public NotEqualToFilter<?> notEqualTo(TYPE value) {
        return new NotEqualToFilter<TYPE>(this, value);
    }

    public PropertyImpl<TYPE> feature(Feature feature) {
        features.add(feature);
        return this;
    }

    public PropertyImpl<TYPE> defaultValue(TYPE defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

}
