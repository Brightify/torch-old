package org.brightify.torch;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.brightify.torch.marshall.MarshallerProvider2;
import org.brightify.torch.marshall.MarshallerProvider2Impl;
import org.brightify.torch.util.TypeHelper;
import org.brightify.torch.util.TypeHelperImpl;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class TorchCompilerModule extends AbstractModule {

    private final TorchCompilerEntrypoint entrypoint;
    private final RoundEnvironment roundEnvironment;

    public TorchCompilerModule(TorchCompilerEntrypoint entrypoint,
                               RoundEnvironment roundEnvironment) {
        this.entrypoint = entrypoint;
        this.roundEnvironment = roundEnvironment;
    }

    @Override
    protected void configure() {
        bind(ProcessingEnvironment.class).toInstance(entrypoint.getProcessingEnvironment());
        bind(RoundEnvironment.class).toInstance(roundEnvironment);
        bind(Reflections.class).toInstance(
                new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forClassLoader())));
        bind(TypeHelper.class).to(TypeHelperImpl.class);
        bind(MarshallerProvider2.class).to(MarshallerProvider2Impl.class).in(Singleton.class);
    }
}
