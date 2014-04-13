package org.brightify.torch.compile.filter;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.brightify.torch.compile.Property;
import org.reflections.Reflections;

import javax.annotation.PostConstruct;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class ColumnRegistryImpl implements ColumnRegistry {

    private final List<ColumnProvider> columnProviders = new ArrayList<ColumnProvider>();

    @Inject
    private Reflections reflections;

    @Inject
    private Injector injector;

    @PostConstruct
    private void init() {
        Set<Class<? extends ColumnProvider>> columnProviderClasses = reflections.getSubTypesOf(ColumnProvider.class);

        for (Class<? extends ColumnProvider> columnProviderClass : columnProviderClasses) {
            if (Modifier.isAbstract(columnProviderClass.getModifiers())) {
                continue;
            }

            try {
                ColumnProvider columnProvider = columnProviderClass.newInstance();
                injector.injectMembers(columnProvider);
                columnProviders.add(columnProvider);
            } catch (Exception e) {
                throw new RuntimeException(
                        "Could not instantiate column provider " + columnProviderClass.getSimpleName() + "!", e
                );
            }
        }

        Collections.sort(columnProviders, new Comparator<ColumnProvider>() {
            @Override
            public int compare(ColumnProvider o1, ColumnProvider o2) {
                // We're sorting from highest priority to lowest
                int result = Integer.compare(o2.getPriority(), o1.getPriority());
                if(result == 0) {
                    // And if the priorities are the same, then from A to Z
                    result = o1.getClass().getCanonicalName().compareTo(o2.getClass().getCanonicalName());
                }
                return result;
            }
        });
    }

    @Override
    public ColumnProvider getColumnProvider(Property property) {
        for (ColumnProvider columnProvider : columnProviders) {
            if (columnProvider.accepts(property)) {
                return columnProvider;
            }
        }

        return null;
    }
}
