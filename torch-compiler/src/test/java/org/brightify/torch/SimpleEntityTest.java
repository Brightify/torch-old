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
    
    @Test
    public void testEntityWithIntId(){
        ASSERT.about(javaSource()).that(JavaFileObjects.forResource("negative/EntityWithIntId.java")).
                processedWith(new TorchCompilerEntrypoint()).failsToCompile().
                withErrorContaining("@Id has to be type Long.").
                in(JavaFileObjects.forResource("negative/EntityWithIntId.java")).onLine(9);
    }
    
    @Test
    public void testEntityWithPrimitiveIntId(){
        ASSERT.about(javaSource()).that(JavaFileObjects.forResource("negative/EntityWithPrimitiveIntId.java")).
                processedWith(new TorchCompilerEntrypoint()).failsToCompile().
                withErrorContaining("@ID has to be type Long.").
                in(JavaFileObjects.forResource("negative/EntityWithPrimitiveIntId.java")).onLine(9);
    }

    @Test
    public void testEntityWithLongFieldsWithoutIdAnnotation(){
        ASSERT.about(javaSource()).that(JavaFileObjects.forResource("negative/EntityWithLongFieldsWithoutIdAnnotation.java")).
                processedWith(new TorchCompilerEntrypoint()).failsToCompile().
                withErrorContaining("You has to mark exactly one of Long fields with @Id annotation").
                in(JavaFileObjects.forResource("negative/EntityWithLongFieldsWithoutIdAnnotation.java")).onLine(7);
        
    }

    @Test
    public void testEntityWithoutEntityAnnotation(){
        ASSERT.about(javaSource()).that(JavaFileObjects.forResource("negative/EntityWithoutEntityAnnotation.java")).
                processedWith(new TorchCompilerEntrypoint()).failsToCompile().
                withErrorContaining("You has to mark class representing an entity with @Entity annotation.").
                in(JavaFileObjects.forResource("negative/EntityWithoutEntityAnnotation.java")).onLine(5);

    }
    
    @Test
    public void testEntityWithNoAnnotations(){
        ASSERT.about(javaSource()).that(JavaFileObjects.forResource("negative/EntityWithNoAnnotations.java")).
                processedWith(new TorchCompilerEntrypoint()).failsToCompile().
                withErrorContaining("You have to annotate your class with an @Entity annotation.").
                in(JavaFileObjects.forResource("negative/EntityWithNoAnnotations.java")).onLine(3).and().
                withErrorContaining("You have to annotate exactly one Long field with @Id annotation.").
                in(JavaFileObjects.forResource("negative/EntityWithNoAnnotations.java")).onLine(4);
        
    }
}
