package com.brightgestures.brightify.marshall;

import com.brightgestures.brightify.parse.Property;
import com.brightgestures.brightify.util.TypeHelper;

import javax.lang.model.type.TypeMirror;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public interface StreamMarshallerProvider {

    boolean isSupported(TypeHelper typeHelper, TypeMirror typeMirror);

    StreamMarshallerInfo getMarshallerInfo(TypeHelper typeHelper, TypeMirror typeMirror);

}
