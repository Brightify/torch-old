package org.brightify.torch;

import com.google.inject.Guice;
import com.google.inject.Injector;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
@SupportedAnnotationTypes({ "org.brightify.torch.annotation.Entity" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class TorchCompilerEntrypoint extends AbstractProcessor {

    public ProcessingEnvironment getProcessingEnvironment() {
        return processingEnv;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if(annotations.size() == 0) {
            return false;
        }

        Injector injector = Guice.createInjector(new TorchCompilerModule(this, roundEnv));

        TorchAnnotationProcessor processor = injector.getInstance(TorchAnnotationProcessor.class);

        return processor.process(annotations);
    }

}
