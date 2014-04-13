package org.brightify.torch;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;
import org.brightify.torch.compile.EntityContext;
import org.brightify.torch.compile.EntityInfo;
import org.brightify.torch.compile.EntityInfoImpl;
import org.brightify.torch.compile.Property;
import org.brightify.torch.compile.PropertyImpl;
import org.brightify.torch.compile.generate.EntityGenerator;
import org.brightify.torch.compile.generate.EntityMetadataGenerator;
import org.brightify.torch.compile.generate.SourceCodeWriter;
import org.brightify.torch.compile.parse.EntityParser;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.generate.MetadataSourceFileImpl;
import org.brightify.torch.marshall2.MarshallerProvider;
import org.brightify.torch.parse.EntityParseException;
import org.brightify.torch.util.TypeHelper;
import org.reflections.Reflections;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.lang.annotation.Annotation;
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
    private MarshallerProvider marshallerProvider;

    @Inject
    private Reflections reflections;

    @Inject
    private Provider<MetadataSourceFileImpl> metadataSourceFileProvider;

    @Inject
    private Messager messager;

    @Inject
    private EntityParser entityParser;

    @Inject
    private EntityContext entityContext;

    @Inject
    private EntityGenerator entityGenerator;

    @Inject
    private EntityMetadataGenerator metadataGenerator;

//    @Inject
//    private Injector injector;

    public boolean process(Set<? extends TypeElement> annotations) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Entity.class);

        for(Element element : elements) {
            if(entityContext.containsEntity(element)) {
                messager.printMessage(Diagnostic.Kind.NOTE,
                                      "Entity " + element.getSimpleName() + "already on classpath.",
                                      element);
                continue;
            }

            try {
                EntityInfo entityInfo = entityParser.parseEntityElement(element);

                entityContext.registerEntityInfo(entityInfo);
            } catch (EntityParseException e) {
                environment.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), e.getElement());
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


        for(EntityInfo entityInfo : entityContext.getEntityInfos()) {



            String metadataPostfix = MetadataSourceFileImpl.METADATA_POSTFIX;
            String metadataName = entityInfo.getSimpleName() + metadataPostfix;
            String metadataFullName = entityInfo.getFullName() + metadataPostfix;
            String internalMetadataName = metadataFullName.replaceAll("\\.", "_");
            String internalMetadataFullName = "com.brightgestures.brightify.metadata." + internalMetadataName;

            try {
                metadataGenerator.generate(entityInfo);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

//            metadataSourceFileProvider.get().withEntity(entityInfo).save(metadataFullName);

        }

        // No more
        //EntityMetadataMapGenerator entityMetadataMapGenerator = new EntityMetadataMapGenerator(processingEnv);
        //entityMetadataMapGenerator.generateEntities(entityInfoSet);

        try {
            CodeModelTypes.getCodeModel().build(new SourceCodeWriter(environment.getFiler()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return true;
    }

}
