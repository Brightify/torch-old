package org.brightify.torch;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.generate.EntityMetadataGenerator;
import org.brightify.torch.generate.MetadataSourceFile;
import org.brightify.torch.generate.MetadataSourceFileImpl;
import org.brightify.torch.marshall.MarshallerProvider2;
import org.brightify.torch.parse.EntityInfo;
import org.brightify.torch.parse.EntityParseException;
import org.brightify.torch.parse.EntityParser;
import org.brightify.torch.util.TypeHelper;
import org.reflections.Reflections;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class TorchAnnotationProcessor {

    @Inject
    private ProcessingEnvironment environment;

    @Inject
    private RoundEnvironment roundEnvironment;

    @Inject
    private TypeHelper typeHelper;

    @Inject
    private MarshallerProvider2 marshallerProvider2;

    @Inject
    private Reflections reflections;

    @Inject
    private Provider<MetadataSourceFileImpl> metadataSourceFileProvider;

//    @Inject
//    private Injector injector;

    public boolean process(Set<? extends TypeElement> annotations) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Entity.class);

        EntityParser parser = new EntityParser(environment);
        Set<EntityInfo> entityInfoSet = new HashSet<EntityInfo>();



        for(Element element : elements) {
            try {
                // TODO move this somewhere else, maybe to metadata generator?
                Class.forName(element.toString() + MetadataSourceFileImpl.METADATA_POSTFIX, false, ClassLoader.getSystemClassLoader());
                environment.getMessager().printMessage(Diagnostic.Kind.NOTE,
                                                         "Entity " + element.getSimpleName() + "already on classpath.",
                                                         element);
                continue;
            } catch (ClassNotFoundException e) {
                // Not found means we need to create it.
            }

            try {
                EntityInfo entityInfo = parser.parseEntity(element);
                if(entityInfo != null) {
                    entityInfoSet.add(entityInfo);
                }
            } catch (EntityParseException e) {
                environment.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), e.getElement());
            }
        }

        for(EntityInfo entityInfo : entityInfoSet) {
            String metadataPostfix = MetadataSourceFileImpl.METADATA_POSTFIX;
            String metadataName = entityInfo.name + metadataPostfix;
            String metadataFullName = entityInfo.fullName + metadataPostfix;
            String internalMetadataName = metadataFullName.replaceAll("\\.", "_");
            String internalMetadataFullName = "com.brightgestures.brightify.metadata." + internalMetadataName;

            metadataSourceFileProvider.get().withEntity(entityInfo).save(metadataFullName);

        }

        // No more
        //EntityMetadataMapGenerator entityMetadataMapGenerator = new EntityMetadataMapGenerator(processingEnv);
        //entityMetadataMapGenerator.generateEntities(entityInfoSet);

        return true;
    }

}
