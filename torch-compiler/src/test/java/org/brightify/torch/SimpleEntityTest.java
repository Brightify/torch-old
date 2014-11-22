package org.brightify.torch;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

public class SimpleEntityTest {

    @Test
    public void testSimpleEntity() {
        ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("HelloEntity.java"))
                .processedWith(new TorchCompilerEntrypoint())
                .compilesWithoutError();
    }

    @Test
    public void testEntityWithoutPackage() {
        ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("EntityWithoutPackage.java"))
                .processedWith(new TorchCompilerEntrypoint())
                .compilesWithoutError();
    }

    @Test
    public void testEntityWithoutId() {
        ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("negative/EntityWithoutId.java"))
                .processedWith(new TorchCompilerEntrypoint())
                .failsToCompile()
                .withErrorContaining("There has to be exactly one @Id property in each entity!")
                .in(JavaFileObjects.forResource("negative/EntityWithoutId.java")).onLine(6);
    }

    @Test
    public void testEntityWithPrimitiveLongId() {
        ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("negative/EntityWithPrimitiveLongId.java"))
                .processedWith(new TorchCompilerEntrypoint())
                .failsToCompile()
                .withErrorContaining("@Id property has to be type Long.")
                .in(JavaFileObjects.forResource("negative/EntityWithPrimitiveLongId.java")).onLine(10);
    }

    @Test
    public void testEntityWithStringId() {
        ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("negative/EntityWithStringId.java"))
                .processedWith(new TorchCompilerEntrypoint())
                .failsToCompile()
                .withErrorContaining("@Id property has to be type Long.")
                .in(JavaFileObjects.forResource("negative/EntityWithStringId.java")).onLine(10);
    }

    @Test
    public void testEntityWithTwoIdProperties() {
        ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("negative/EntityWithTwoIdProperties.java"))
                .processedWith(new TorchCompilerEntrypoint())
                .failsToCompile()
                .withErrorContaining("There has to be exactly one @Id property in each entity!")
                .in(JavaFileObjects.forResource("negative/EntityWithTwoIdProperties.java")).onLine(10).and()
                .withErrorContaining("There has to be exactly one @Id property in each entity!")
                .in(JavaFileObjects.forResource("negative/EntityWithTwoIdProperties.java")).onLine(13);
    }

}
