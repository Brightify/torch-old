package com.brightgestures.brightify.generate;

import com.brightgestures.brightify.SourceFileGenerator;
import com.brightgestures.brightify.parse.EntityInfo;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Date;
import java.util.Set;

/**
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class EntityMetadataMapGenerator extends SourceFileGenerator {
    public EntityMetadataMapGenerator(ProcessingEnvironment processingEnvironment) {
        super(processingEnvironment);
    }

    public void generateEntities(Set<EntityInfo> entityInfoSet) {
        append("/* Generated on ").append(new Date()).append(" by EntityMetadataMapGenerator */");
        line("package com.brightgestures.brightify;");
        emptyLine();
        line("import java.util.Map;");
        line("import java.util.HashMap;");
        emptyLine();
        for(EntityInfo entity : entityInfoSet) {
            line("import ").append(entity.fullName).append(";");
            line("import com.brightgestures.brightify.metadata.").append(entity.name).append("Metadata;");
        }
        emptyLine();
        line("final class EntityMetadataMap").nest();
        line("static Map<Class<?>, EntityMetadata<?>> byClass = new HashMap<Class<?>, EntityMetadata<?>>();");
        line("static Map<String, EntityMetadata<?>> byTableName = new HashMap<String, EntityMetadata<?>>();");
        emptyLine();
        line("static").nest();
        for(EntityInfo entity : entityInfoSet) {
            newLineNest();
            line(entity.name).append("Metadata metadata = new ").append(entity.name).append("Metadata();");
            line("byClass.put(").append(entity.name).append(".class, metadata);");
            line("byTableName.put(\"").append(entity.tableName).append("\", metadata);");
            unNest();
        }
        unNest();
        unNest();

        save("com.brightgestures.brightify.EntityMetadataMap");
    }
}
