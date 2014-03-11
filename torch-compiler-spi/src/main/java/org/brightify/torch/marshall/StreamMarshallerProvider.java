package org.brightify.torch.marshall;

import org.brightify.torch.util.TypeHelper;

import javax.lang.model.type.TypeMirror;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface StreamMarshallerProvider {

    boolean isSupported(TypeHelper typeHelper, TypeMirror typeMirror);

    StreamMarshallerInfo getMarshallerInfo(TypeHelper typeHelper, TypeMirror typeMirror);

}
