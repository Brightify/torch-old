package org.brightify.torch;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import java.util.Arrays;

import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static org.truth0.Truth.ASSERT;

public class ListReferenceTest {

    @Test
    public void testEntityWithListOfOtherEntities() {
        ASSERT.about(javaSources())
              .that(Arrays.asList(
                      JavaFileObjects.forResource("references/list/ParentEntity.java"),
                      JavaFileObjects.forResource("references/list/ChildEntity.java")))
              .processedWith(new TorchCompilerEntrypoint())
              .compilesWithoutError();
    }

}
