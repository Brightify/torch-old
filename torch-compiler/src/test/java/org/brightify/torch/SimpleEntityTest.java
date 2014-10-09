package org.brightify.torch;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Ignore;
import org.junit.Test;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

public class SimpleEntityTest {

    // FIXME unignore when we have property implementations on classpath
    @Ignore
    @Test
    public void testSimpleEntity() {
        ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("HelloEntity.java"))
                .processedWith(new TorchCompilerEntrypoint())
                .compilesWithoutError();
    }

    @Test
    public void testEntityWithoutId() {
        ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("EntityWithoutId.java"))
                .processedWith(new TorchCompilerEntrypoint())
                .failsToCompile()
                .withErrorContaining("There has to be exactly one @Id property in each entity!")
                .in(JavaFileObjects.forResource("EntityWithoutId.java")).onLine(6);
    }

    @Test
    public void testEntityWithTwoIdProperties() {
        ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("EntityWithTwoIdProperties.java"))
                .processedWith(new TorchCompilerEntrypoint())
                .failsToCompile()
                .withErrorContaining("There has to be exactly one @Id property in each entity!")
                .in(JavaFileObjects.forResource("EntityWithTwoIdProperties.java")).onLine(8).and()
                .withErrorContaining("There has to be exactly one @Id property in each entity!")
                .in(JavaFileObjects.forResource("EntityWithTwoIdProperties.java")).onLine(11);
    }

}
