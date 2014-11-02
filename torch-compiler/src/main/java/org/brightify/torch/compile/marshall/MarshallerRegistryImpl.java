package org.brightify.torch.compile.marshall;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.brightify.torch.compile.PropertyMirror;
import org.reflections.Reflections;

import javax.annotation.PostConstruct;
import javax.annotation.processing.Messager;
import javax.lang.model.type.TypeMirror;
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
public class MarshallerRegistryImpl implements MarshallerRegistry {

    private final List<Marshaller> marshallers = new ArrayList<Marshaller>();

    @Inject
    private Reflections reflections;

    @Inject
    private Injector injector;

    @Inject
    private Messager messager;

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
                messager.printMessage(Diagnostic.Kind.ERROR,
                                      "Couldn't instantiate cursor marshaller provider " + marshallerClass.getSimpleName() + "!");
                e.printStackTrace();
            }
        }

        Collections.sort(marshallers, new Comparator<Marshaller>() {
            @Override
            public int compare(Marshaller o1, Marshaller o2) {
                // We're sorting from highest priority to lowest

                int lhs = o2.getPriority();
                int rhs = o1.getPriority();
                int result = lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
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
    public Marshaller getMarshaller(PropertyMirror propertyMirror) {
        return getMarshaller(propertyMirror.getType());
    }

    @Override
    public Marshaller getMarshallerOrThrow(PropertyMirror propertyMirror) {
        return getMarshallerOrThrow(propertyMirror.getType());
    }

    @Override
    public Marshaller getMarshallerOrThrow(TypeMirror type) {
        Marshaller marshaller = getMarshaller(type);
        if(marshaller == null) { // FIXME change to GenerationException
            StringBuilder builder = new StringBuilder("Supported marshallers: ");
            for (Marshaller marshaller1 : marshallers) {
                builder.append(marshaller1.getClass().getSimpleName()).append("\n");
            }
            throw new IllegalStateException(LongMarshaller.class.getCanonicalName() + " Unsupported type: " + type + ". " + builder.toString());
        }
        return marshaller;
    }

    @Override
    public Marshaller getMarshaller(TypeMirror type) {
        for (Marshaller marshaller : marshallers) {
            if(marshaller.accepts(type)) {
                return marshaller;
            }
        }

        return null;
    }
}
