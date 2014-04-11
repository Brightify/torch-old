package org.brightify.torch;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.brightify.torch.compile.parse.EntityParser;
import org.brightify.torch.compile.parse.EntityParserImpl;
import org.brightify.torch.compile.parse.PropertyParser;
import org.brightify.torch.compile.parse.PropertyParserImpl;
import org.brightify.torch.marshall.MarshallerProvider2;
import org.brightify.torch.marshall.MarshallerProvider2Impl;
import org.brightify.torch.util.TypeHelper;
import org.brightify.torch.util.TypeHelperImpl;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

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
        ProcessingEnvironment environment = entrypoint.getProcessingEnvironment();

        bind(ProcessingEnvironment.class).toInstance(environment);
        bind(Messager.class).toInstance(environment.getMessager());
        bind(Filer.class).toInstance(environment.getFiler());
        bind(Types.class).toInstance(environment.getTypeUtils());
        bind(Elements.class).toInstance(environment.getElementUtils());

        bind(RoundEnvironment.class).toInstance(roundEnvironment);

        bind(Reflections.class).toInstance(
                new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forClassLoader())));

        bind(TypeHelper.class).to(TypeHelperImpl.class);
        bind(MarshallerProvider2.class).to(MarshallerProvider2Impl.class);

        bind(EntityParser.class).to(EntityParserImpl.class);
        bind(PropertyParser.class).to(PropertyParserImpl.class);
    }
}