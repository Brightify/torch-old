package org.brightify.torch.util;

import org.brightify.torch.filter.Property;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public final class PropertyUtil {

    private PropertyUtil() { }

    public static <T extends Property.Feature> T getFeature(Property<?> property, Class<T> featureClass) {
        Property.Feature[] features = property.getFeatures();

        for (Property.Feature feature : features) {
            if(featureClass.isAssignableFrom(feature.getClass())) {
                @SuppressWarnings("unchecked")
                T castFeature = (T) feature;
                return castFeature;
            }
        }
        return null;
    }

    public static <T extends Property.Feature> T getFeatureOrThrow(Property<?> property, Class<T> featureClass) {
        List<T> filteredFeatures = getFeatures(property, featureClass);

        if(filteredFeatures.size() != 1) {
            throw new IllegalStateException("There has to be exacly one feature of type " + featureClass.getName() +
                                            ". There were " + filteredFeatures.size());
        }

        return filteredFeatures.iterator().next();
    }

    public static <T extends Property.Feature> List<T> getFeatures(Property<?> property, Class<T> featureClass) {
        List<T> filteredFeatures = new ArrayList<T>();
        Property.Feature[] features = property.getFeatures();

        for (Property.Feature feature : features) {
            if(featureClass.isAssignableFrom(feature.getClass())) {
                @SuppressWarnings("unchecked")
                T castFeature = (T) feature;
                filteredFeatures.add(castFeature);
            }
        }

        return filteredFeatures;
    }

}
