package org.brightify.torch.compile.generate;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class SourceCodeWriter extends CodeWriter {

    private final Filer filer;
    private final Element[] originatingElements;
    private OutputStream outputStream;

    public SourceCodeWriter(Filer filer, Element... originatingElements) {
        this.filer = filer;
        this.originatingElements = originatingElements;
    }

    @Override
    public OutputStream openBinary(JPackage jPackage, String filename) throws IOException {
        String fullQualifiedClassName = fullyQualifiedClassName(jPackage, filename);

        JavaFileObject fileObject = filer.createSourceFile(fullQualifiedClassName, originatingElements);

        outputStream = fileObject.openOutputStream();

        return outputStream;
    }

    @Override
    public void close() throws IOException {
        if(outputStream != null) {
            outputStream.close();
        }
    }

    private String fullyQualifiedClassName(JPackage jPackage, String filename) {
        String className = filename.substring(0, filename.lastIndexOf('.'));
        return jPackage.name() + "." + className;
    }
}
