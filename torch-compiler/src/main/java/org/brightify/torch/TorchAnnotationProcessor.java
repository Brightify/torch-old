package org.brightify.torch;

import org.brightify.torch.annotation.Entity;
import org.brightify.torch.generate.EntityMetadataGenerator;
import org.brightify.torch.generate.MetadataSourceFile;
import org.brightify.torch.parse.EntityInfo;
import org.brightify.torch.parse.EntityParseException;
import org.brightify.torch.parse.EntityParser;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.*;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
@SupportedAnnotationTypes({ "org.brightify.torch.annotation.Entity" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class TorchAnnotationProcessor extends AbstractProcessor {

    private EntityParser parser;


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if(annotations.size() == 0) {
            return false;
        }



        parser = new EntityParser(processingEnv);

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Entity.class);

        EntityParser parser = new EntityParser(processingEnv);
        Set<EntityInfo> entityInfoSet = new HashSet<EntityInfo>();

        for(Element element : elements) {
            try {
                // TODO move this somewhere else, maybe to metadata generator?
                Class.forName(element.toString() + Constants.METADATA_SUFFIX, false, ClassLoader.getSystemClassLoader());
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                                                         "Entity " + element.getSimpleName() + " already on classpath.",
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
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), e.getElement());
            }
        }

        EntityMetadataGenerator metadataGenerator = new EntityMetadataGenerator(processingEnv);
        for(EntityInfo entityInfo : entityInfoSet) {
            metadataGenerator.generateMetadata(entityInfo);
        }
        return true;
    }

    private void scan() {

    }

}
