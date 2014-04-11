package org.brightify.torch.util;

import org.brightify.torch.filter.ColumnInfo;
import org.brightify.torch.marshall.CursorMarshallerInfo;
import org.brightify.torch.marshall.Marshaller2;
import org.brightify.torch.marshall.StreamMarshallerInfo;
import org.brightify.torch.parse.Property;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public interface TypeHelper {
    ProcessingEnvironment getProcessingEnvironment();

    TypeMirror getWrappedType(Property property);

    TypeMirror getWrappedType(TypeMirror propertyType);

    CursorMarshallerInfo getCursorMarshallerInfo(Property property);

    StreamMarshallerInfo getStreamMarshallerInfo(TypeMirror typeMirror);

    ColumnInfo getColumnInfo(Property property);

    TypeElement elementOf(Class cls);

    TypeMirror typeOf(Class cls);

    Class<?> classOf(TypeMirror mirror);

    String packageOf(Element element);

}
