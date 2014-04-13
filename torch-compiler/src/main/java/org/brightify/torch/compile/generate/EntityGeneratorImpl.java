package org.brightify.torch.compile.generate;

import com.google.inject.Inject;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.compile.EntityInfo;
import org.brightify.torch.compile.Property;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.util.TypeHelper;

import javax.lang.model.type.TypeMirror;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class EntityGeneratorImpl implements EntityGenerator {

    @Inject
    private TypeHelper typeHelper;

    @Override
    public JCodeModel generate(EntityInfo entityInfo) throws Exception {
        JCodeModel codeModel = CodeModelTypes.getCodeModel();

        JDefinedClass definedClass = codeModel._class(entityInfo.getFullName());
        JAnnotationUse annotationUse = definedClass.annotate(Entity.class);

        for (Property property : entityInfo.getProperties()) {
            Class<?> type = typeHelper.classOf(property.getType());

            JFieldVar field =  definedClass.field(JMod.PRIVATE, type, property.getName());

            JMethod getterMethod = definedClass.method(JMod.PUBLIC, type, "get" + uppercaseFirstLetter(property.getName()));
            getterMethod.body()._return(field);

            JMethod setterMethod = definedClass.method(JMod.PUBLIC, Void.TYPE, "set" + uppercaseFirstLetter(property.getName()));
            JVar setterParameter = setterMethod.param(type, property.getName());
            setterMethod.body().assign(JExpr._this().ref(field), setterParameter);

            // FIXME add some more dynamic way to set annotations
            if(property.getId() != null) {
                getterMethod.annotate(property.getId().annotationType());
            }
            if(property.getIndex() != null) {
                getterMethod.annotate(property.getIndex().annotationType());
            }
            if(property.getNotNull() != null) {
                getterMethod.annotate(property.getNotNull().annotationType());
            }
            if(property.getUnique() != null) {
                getterMethod.annotate(property.getUnique().annotationType());
            }
        }

        return codeModel;
    }

    private String uppercaseFirstLetter(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
