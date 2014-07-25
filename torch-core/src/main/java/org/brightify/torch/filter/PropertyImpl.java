package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class PropertyImpl<T> implements Property<T> {

    private final String name;
    private final String safeName;
    private final Class<T> type;
    private final Feature[] features;

    public PropertyImpl(String name, String safeName, Class<T> type, Feature... features) {
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
    public Class<T> getType() {
        return type;
    }

    @Override
    public Feature[] getFeatures() {
        return features;
    }

    @Override
    public EntityFilter equalTo(T value) {
        return EntityFilter.filter(getSafeName() + " = ?", value);
    }

    @Override
    public EntityFilter notEqualTo(T value) {
        return EntityFilter.filter(getSafeName() + " != ?", value);
    }

}
