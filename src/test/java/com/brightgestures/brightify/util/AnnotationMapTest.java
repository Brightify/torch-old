package com.brightgestures.brightify.util;

import com.brightgestures.brightify.TestObject;
import com.brightgestures.brightify.annotation.Entity;
import com.brightgestures.brightify.annotation.Index;
import org.junit.Test;

import java.lang.annotation.Annotation;

import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class AnnotationMapTest {

    @Test
    public void testAnnotationMap() throws Exception{
        Annotation[] annotations = TestObject.class.getAnnotations();
        AnnotationMap map = new AnnotationMap();

        for(Annotation annotation : annotations) {
            map.putAnnotation(annotation);
        }

        Entity entityAnnotation = map.getAnnotation(Entity.class);
        assertNotNull(entityAnnotation);

        Index indexAnnotation = map.getAnnotation(Index.class);
        assertNotNull(indexAnnotation);
    }

}
