package org.brightify.torch;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Ignore;
import org.junit.Test;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

@Ignore
public class ListEntityTest {

    @Test
    public void testStringListEntity() {
        ASSERT.about(javaSource())
                .that(JavaFileObjects.forResource("StringListEntity.java"))
                .processedWith(new TorchCompilerEntrypoint())
                .compilesWithoutError();
    }

}
