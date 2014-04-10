package org.brightify.torch;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.generate.EntityMetadataGenerator;
import org.brightify.torch.generate.MetadataSourceFile;
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

//    @Inject
//    private Injector injector;

    public boolean process(Set<? extends TypeElement> annotations) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Entity.class);

        EntityParser parser = new EntityParser(environment);
        Set<EntityInfo> entityInfoSet = new HashSet<EntityInfo>();



        for(Element element : elements) {
            try {
                // TODO move this somewhere else, maybe to metadata generator?
                Class.forName(element.toString() + MetadataSourceFile.METADATA_POSTFIX, false, ClassLoader.getSystemClassLoader());
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

        EntityMetadataGenerator metadataGenerator = new EntityMetadataGenerator(environment, reflections);
        for(EntityInfo entityInfo : entityInfoSet) {
            metadataGenerator.generateMetadata(entityInfo);
        }

        // No more
        //EntityMetadataMapGenerator entityMetadataMapGenerator = new EntityMetadataMapGenerator(processingEnv);
        //entityMetadataMapGenerator.generateEntities(entityInfoSet);

        return true;
    }

}
