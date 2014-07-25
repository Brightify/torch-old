package org.brightify.torch.filter;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class BooleanPropertyImpl implements BooleanProperty {

    private final PropertyImpl<Integer> realColumn;

    public BooleanPropertyImpl(String name, String safeName, Feature... features) {
        realColumn = new PropertyImpl<Integer>(name, safeName, Integer.class, features);
    }

    @Override
    public String getName() {
        return realColumn.getName();
    }

    @Override
    public String getSafeName() {
        return realColumn.getSafeName();
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    @Override
    public Feature[] getFeatures() {
        return realColumn.getFeatures();
    }

    @Override
    public EntityFilter equalTo(Boolean value) {
        return realColumn.equalTo(convert(value));
    }

    @Override
    public EntityFilter notEqualTo(Boolean value) {
        return realColumn.notEqualTo(convert(value));
    }

    private Integer convert(Boolean value) {
        return value != null && value ? 1 : 0;
    }
}
