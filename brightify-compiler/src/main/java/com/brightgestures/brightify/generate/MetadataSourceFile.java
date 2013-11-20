package com.brightgestures.brightify.generate;

import com.brightgestures.brightify.SourceFile;
import com.brightgestures.brightify.SourceFileGenerator;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class MetadataSourceFile extends SourceFile {

    private String targetPackage;
    private List<String> imports = new ArrayList<>();
    private String entityClassName;
    private String metadataClassName;
    private List<Field> fields = new ArrayList<>();

    public MetadataSourceFile(ProcessingEnvironment processingEnvironment) {
        super(processingEnvironment);
    }

    public String getTargetPackage() {
        return targetPackage;
    }

    public MetadataSourceFile setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
        return this;
    }

    public MetadataSourceFile addImport(Class<?> importClass) {
        addImport(importClass.getName());
        return this;
    }

    public MetadataSourceFile addImport(String importName) {
        imports.add(importName);
        return this;
    }

    public String getEntityClassName() {
        return entityClassName;
    }

    public MetadataSourceFile setEntityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
        metadataClassName = entityClassName + "Metadata";
        return this;
    }

    public MetadataSourceFile addField(Field field) {
        fields.add(field);
        return this;
    }


    @Override
    public void save(String name) {
        append("/* Generated on ").append(new Date()).append(" by Brightify */");

        line("package ").append(targetPackage).append(";");
        emptyLine();
        for (String importItem : imports) {
            line("import ").append(importItem).append(";");
        }

        emptyLine();
        // TODO should the class be final?
        append("public final class ")
                .append(entityClassName)
                .append("Metadata extends EntityMetadata<")
                .append(entityClassName)
                .append(">")
                .nest();

        for (Field field : fields) {
            field.write(this);
            emptyLine();
        }



        emptyLine();
        line("public static ").append(metadataClassName).append(" create()").nest();
        line("return new ").append(metadataClassName).append("();");
        unNest();

        sourceFileGenerator.unNest();
        super.save(name);
    }

}
