package org.brightify.torch;

import com.google.inject.Inject;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.compile.EntityContext;
import org.brightify.torch.compile.EntityMirror;
import org.brightify.torch.compile.generate.EntityDescriptionGenerator;
import org.brightify.torch.compile.generate.SourceCodeWriter;
import org.brightify.torch.compile.parse.EntityParser;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.parse.EntityParseException;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
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
    private Messager messager;

    @Inject
    private EntityParser entityParser;

    @Inject
    private EntityContext entityContext;

    @Inject
    private EntityDescriptionGenerator metadataGenerator;

    public boolean process(Set<? extends TypeElement> annotations) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Entity.class);

        for (Element element : elements) {
            if (entityContext.containsEntity(element)) {
                messager.printMessage(Diagnostic.Kind.NOTE,
                                      "Entity " + element.getSimpleName() + "already on classpath.",
                                      element);
                continue;
            }

            try {
                EntityMirror entityMirror = entityParser.parseEntityElement(element);

                entityContext.registerEntityInfo(entityMirror);
            } catch (EntityParseException e) {
                if(e.getElements().size() > 0) {
                    for (Element elementInError : e.getElements()) {
                        environment.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), elementInError);
                    }
                } else {
                    environment.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                }
            }
        }
/*

        EntityInfoImpl entityInfo1 = new EntityInfoImpl();
        entityInfo1.setFullName("org.brightify.torch.TestEntity");
        entityInfo1.setName("TestEntity");

        PropertyImpl<Long> idProperty = new PropertyImpl<Long>();
        idProperty.setId(new Id() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Id.class;
            }

            @Override
            public boolean autoIncrement() {
                return true;
            }
        });
        idProperty.setName("id");

        entityInfo1.getProperties().add(idProperty);

        try {
            //entityGenerator.generate(entityInfo1).build(new SourceCodeWriter(environment.getFiler()));
        } catch (Exception e) {
            e.printStackTrace();
        }
*/


        for (EntityMirror entityMirror : entityContext.getEntityMirrors()) {


//            String metadataPostfix = MetadataSourceFileImpl.DESCRIPTION_POSTFIX;
//            String metadataName = entityInfo.getSimpleName() + metadataPostfix;
//            String metadataFullName = entityInfo.getFullName() + metadataPostfix;
//            String internalMetadataName = metadataFullName.replaceAll("\\.", "_");
//            String internalMetadataFullName = "com.brightgestures.brightify.metadata." + internalMetadataName;

            try {


                metadataGenerator.generate(entityMirror);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

//            metadataSourceFileProvider.get().withEntity(entityInfo).save(metadataFullName);

        }

        // No more
        //EntityMetadataMapGenerator entityMetadataMapGenerator = new EntityMetadataMapGenerator(processingEnv);
        //entityMetadataMapGenerator.generateEntities(entityInfoSet);

        try {
            CodeModelTypes.CODE_MODEL.build(new SourceCodeWriter(environment.getFiler()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

}
