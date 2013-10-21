package com.brightgestures.brightify.util;

import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.Ref;
import com.brightgestures.brightify.sql.affinity.IntegerAffinity;
import com.brightgestures.brightify.sql.affinity.NoneAffinity;
import com.brightgestures.brightify.sql.affinity.RealAffinity;
import com.brightgestures.brightify.sql.affinity.TextAffinity;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class TypeHelper {

    private static final Map<TypeMirror, Class> TYPE_TO_AFFINITY = new HashMap<TypeMirror, Class>();
    private static ProcessingEnvironment sEnvironment;

    public static void prepare(ProcessingEnvironment environment) {
        sEnvironment = environment;


        TYPE_TO_AFFINITY.put(typeOf(Byte.class), IntegerAffinity.class);
        TYPE_TO_AFFINITY.put(typeOf(Short.class), IntegerAffinity.class);
        TYPE_TO_AFFINITY.put(typeOf(Integer.class), IntegerAffinity.class);
        TYPE_TO_AFFINITY.put(typeOf(Long.class), IntegerAffinity.class);
        TYPE_TO_AFFINITY.put(typeOf(Boolean.class), IntegerAffinity.class);

        TYPE_TO_AFFINITY.put(typeOf(CharSequence.class), TextAffinity.class);
        TYPE_TO_AFFINITY.put(typeOf(String.class), TextAffinity.class);

        TYPE_TO_AFFINITY.put(typeOf(Key.class), NoneAffinity.class);
        TYPE_TO_AFFINITY.put(typeOf(Ref.class), NoneAffinity.class);
        TYPE_TO_AFFINITY.put(typeOf(Serializable.class), NoneAffinity.class);

        TYPE_TO_AFFINITY.put(typeOf(Float.class), RealAffinity.class);
        TYPE_TO_AFFINITY.put(typeOf(Double.class), RealAffinity.class);
    }

    public static Class affinityClassFromTypeMirror(TypeMirror mirror) {
        if(mirror.getKind().isPrimitive()) {
            mirror = sEnvironment.getTypeUtils().boxedClass((PrimitiveType) mirror).asType();
        }
        if(TYPE_TO_AFFINITY.containsKey(mirror)) {
            return TYPE_TO_AFFINITY.get(mirror);
        } else if(sEnvironment.getTypeUtils().isAssignable(mirror, typeOf(Serializable.class))) {
            return TYPE_TO_AFFINITY.get(typeOf(Serializable.class));
        } else {
            throw new IllegalArgumentException("Type \"" + mirror + "\" isn't supported! " +
                    "You can store custom object only if they implements Serializable interface." +
                    "Although it's recommended that you can use Keys/Refs");
        }
    }

    public static String affinityFromTypeMirror(TypeMirror mirror) {
        return "new " + affinityClassFromTypeMirror(mirror).getSimpleName() + "()";
    }

    public static TypeMirror typeOf(Class cls) {
        Element element = sEnvironment.getElementUtils().getTypeElement(cls.getName());
        return element.asType();
    }

}
