package com.brightgestures.brightify;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.Writer;

/**
* @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
*/
public abstract class SourceFile {
    protected ProcessingEnvironment processingEnv;

    private StringBuilder mBuilder = new StringBuilder();
    private int mLevel = 0;

    public SourceFile(ProcessingEnvironment processingEnvironment) {
        processingEnv = processingEnvironment;
    }

    public SourceFile append(Object value) {
        mBuilder.append(value);
        return this;
    }

    public SourceFile line(Object value) {
        emptyLine();

        for(int i = 0; i < mLevel; i++) {
            mBuilder.append("    ");
        }
        mBuilder.append(value);

        return this;
    }

    public SourceFile emptyLine() {
        mBuilder.append("\n");
        return this;
    }

    public SourceFile nest() {
        mBuilder.append(" {");
        mLevel++;
        return this;
    }

    public SourceFile newLineNest() {
        line("{");
        mLevel++;
        return this;
    }

    public SourceFile unNest() {
        mLevel--;
        line("}");
        return this;
    }

    public void save(String name) {
        try {
            Writer writer = processingEnv.getFiler().createSourceFile(name).openWriter();

            writer.write(mBuilder.toString());

            writer.flush();
            writer.close();

            mBuilder = new StringBuilder();
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }
}
