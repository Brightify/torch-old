package org.brightify.torch.marshall2;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.brightify.torch.compile.EntityContext;
import org.brightify.torch.compile.Property;
import org.brightify.torch.marshall2.Marshaller;
import org.brightify.torch.marshall2.MarshallerProvider;
import org.brightify.torch.util.TypeHelper;
import org.reflections.Reflections;

import javax.annotation.PostConstruct;
import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class MarshallerProviderImpl implements MarshallerProvider {

    private final List<Marshaller> marshallers = new ArrayList<Marshaller>();

    @Inject
    private Reflections reflections;

    @Inject
    private Injector injector;

    @PostConstruct
    private void init() {
        Set<Class<? extends Marshaller>> marshallerClasses = reflections.getSubTypesOf(Marshaller.class);

        for (Class<? extends Marshaller> marshallerClass : marshallerClasses) {
            if (Modifier.isAbstract(marshallerClass.getModifiers())) {
                continue;
            }
            try {
                Marshaller marshaller = marshallerClass.newInstance();
                injector.injectMembers(marshaller);
                marshallers.add(marshaller);
            } catch (Exception e) {
                // FIXME we shouldn't throw an exception because of Guice!
                throw new RuntimeException(
                        "Couldn't instantiate cursor marshaller provider " + marshallerClass.getSimpleName() + "!", e);
            }
        }

        Collections.sort(marshallers, new Comparator<Marshaller>() {
            @Override
            public int compare(Marshaller o1, Marshaller o2) {
                // We're sorting from highest priority to lowest
                int result = Integer.compare(o2.getPriority(), o1.getPriority());;
                if(result == 0) {
                    // And if the priorities are the same, then from A to Z
                    result = o1.getClass().getCanonicalName().compareTo(o2.getClass().getCanonicalName());
                }
                return result;
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public Marshaller getMarshaller(Property property) {
        for (Marshaller marshaller : marshallers) {
            if(marshaller.accepts(property)) {
                return marshaller;
            }
        }

        return null;

    }

    @Override
    public Marshaller getMarshallerOrThrow(Property property) {
        Marshaller marshaller = getMarshaller(property);
        if(marshaller == null) { // FIXME change to GenerationException
            throw new IllegalStateException("Unsupported type: " + property.getType());
        }
        return marshaller;
    }
}
