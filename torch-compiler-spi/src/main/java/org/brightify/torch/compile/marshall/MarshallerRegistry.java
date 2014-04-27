package org.brightify.torch.compile.marshall;

import org.brightify.torch.compile.PropertyMirror;

import javax.lang.model.type.TypeMirror;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface MarshallerRegistry {

    Marshaller getMarshaller(PropertyMirror propertyMirror);

    Marshaller getMarshallerOrThrow(PropertyMirror propertyMirror);

    Marshaller getMarshallerOrThrow(TypeMirror type);

    Marshaller getMarshaller(TypeMirror type);
}
