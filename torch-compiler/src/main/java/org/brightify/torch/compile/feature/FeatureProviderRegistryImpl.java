package org.brightify.torch.compile.feature;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.sun.codemodel.JExpression;
import org.brightify.torch.compile.PropertyMirror;
import org.reflections.Reflections;

import javax.annotation.PostConstruct;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class FeatureProviderRegistryImpl implements FeatureProviderRegistry {

    private final List<FeatureProvider> featureProviders = new LinkedList<FeatureProvider>();

    @Inject
    private Reflections reflections;

    @Inject
    private Injector injector;

    @PostConstruct
    private void init() {
        Set<Class<? extends FeatureProvider>> featureProviderClasses = reflections.getSubTypesOf(FeatureProvider.class);

        for (Class<? extends FeatureProvider> featureProviderClass : featureProviderClasses) {
            if(Modifier.isAbstract(featureProviderClass.getModifiers())) {
                continue;
            }

            try {
                FeatureProvider featureProvider = featureProviderClass.newInstance();
                injector.injectMembers(featureProvider);
                featureProviders.add(featureProvider);
            } catch (Exception e) {
                throw new RuntimeException("Couldn't instantiate cursor marshaller provider " +
                                           featureProviderClass.getSimpleName() + "!", e);
            }
        }
    }

    @Override
    public List<JExpression> getFeatureConstructions(PropertyMirror propertyMirror) {
        List<JExpression> featureConstructions = new LinkedList<JExpression>();

        for (FeatureProvider featureProvider : featureProviders) {
            featureProvider.constructFeatures(propertyMirror, featureConstructions);
        }

        return featureConstructions;
    }
}
