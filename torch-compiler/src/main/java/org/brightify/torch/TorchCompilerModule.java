package org.brightify.torch;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.brightify.torch.compile.EntityContext;
import org.brightify.torch.compile.EntityContextImpl;
import org.brightify.torch.compile.feature.FeatureProviderRegistry;
import org.brightify.torch.compile.feature.FeatureProviderRegistryImpl;
import org.brightify.torch.compile.generate.EntityDescriptionGenerator;
import org.brightify.torch.compile.generate.EntityDescriptionGeneratorImpl;
import org.brightify.torch.compile.generate.EntityGenerator;
import org.brightify.torch.compile.generate.EntityGeneratorImpl;
import org.brightify.torch.compile.marshall.MarshallerRegistry;
import org.brightify.torch.compile.marshall.MarshallerRegistryImpl;
import org.brightify.torch.compile.parse.EntityParser;
import org.brightify.torch.compile.parse.EntityParserImpl;
import org.brightify.torch.compile.parse.MigrationParser;
import org.brightify.torch.compile.parse.MigrationParserImpl;
import org.brightify.torch.compile.parse.PropertyParser;
import org.brightify.torch.compile.parse.PropertyParserImpl;
import org.brightify.torch.compile.util.TypeHelper;
import org.brightify.torch.compile.util.TypeHelperImpl;
import org.brightify.torch.compile.verify.EntityVerifier;
import org.brightify.torch.compile.verify.EntityVerifierImpl;
import org.brightify.torch.compile.verify.PropertyVerifier;
import org.brightify.torch.compile.verify.PropertyVerifierImpl;
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
    private final Reflections reflections;

    public TorchCompilerModule(TorchCompilerEntrypoint entrypoint,
                               RoundEnvironment roundEnvironment) {
        this.entrypoint = entrypoint;
        this.roundEnvironment = roundEnvironment;


        ConfigurationBuilder configurationBuilder =  new ConfigurationBuilder();
        configurationBuilder.setUrls(ClasspathHelper.forClassLoader());
        configurationBuilder.addUrls(TorchCompilerModule.class.getProtectionDomain().getCodeSource().getLocation());
        reflections = new Reflections(configurationBuilder);
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

        bind(Reflections.class).toInstance(reflections);

        bind(TypeHelper.class).to(TypeHelperImpl.class);
        bind(MarshallerRegistry.class).to(MarshallerRegistryImpl.class).in(Singleton.class);
        bind(FeatureProviderRegistry.class).to(FeatureProviderRegistryImpl.class).in(Singleton.class);

        bind(EntityParser.class).to(EntityParserImpl.class);
        bind(PropertyParser.class).to(PropertyParserImpl.class);
        bind(MigrationParser.class).to(MigrationParserImpl.class);

        bind(EntityVerifier.class).to(EntityVerifierImpl.class);
        bind(PropertyVerifier.class).to(PropertyVerifierImpl.class);

        bind(EntityContext.class).to(EntityContextImpl.class).in(Singleton.class);

        bind(EntityGenerator.class).to(EntityGeneratorImpl.class);
        bind(EntityDescriptionGenerator.class).to(EntityDescriptionGeneratorImpl.class);
    }

}