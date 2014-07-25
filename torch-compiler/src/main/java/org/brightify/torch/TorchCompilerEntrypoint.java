package org.brightify.torch;

import com.google.inject.Injector;
import com.netflix.governator.guice.LifecycleInjector;

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

        LifecycleInjector lifecycleInjector = LifecycleInjector
                .builder()
                .withModules(new TorchCompilerModule(this, roundEnv))
                .build();

        try {
            lifecycleInjector.getLifecycleManager().start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Injector injector = lifecycleInjector.createInjector();

        TorchAnnotationProcessor processor = injector.getInstance(TorchAnnotationProcessor.class);

        boolean success = processor.process(annotations);

        lifecycleInjector.getLifecycleManager().close();

        return success;
    }

}
