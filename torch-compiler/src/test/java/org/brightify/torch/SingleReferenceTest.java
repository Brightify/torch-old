package org.brightify.torch;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import java.util.Arrays;

import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static org.truth0.Truth.ASSERT;

public class SingleReferenceTest {

    @Test
    public void testEntityWithListOfOtherEntities() {
        ASSERT.about(javaSources())
                .that(Arrays.asList(
                        JavaFileObjects.forResource("references/single/ParentEntity.java"),
                        JavaFileObjects.forResource("references/single/ChildEntity.java")))
                .processedWith(new TorchCompilerEntrypoint())
                .compilesWithoutError();
    }

}
