package org.brightify.torch.compile.marshall;

import org.brightify.torch.compile.Property;

import javax.lang.model.type.TypeMirror;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface MarshallerRegistry {

    Marshaller getMarshaller(Property property);

    Marshaller getMarshallerOrThrow(Property property);

    Marshaller getMarshallerOrThrow(TypeMirror type);

    Marshaller getMarshaller(TypeMirror type);
}
