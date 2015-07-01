package org.brightify.torch.compile.feature;

import com.google.inject.Inject;
import com.sun.codemodel.*;
import org.brightify.torch.annotation.Delete;
import org.brightify.torch.annotation.Id;
import org.brightify.torch.annotation.Load;
import org.brightify.torch.annotation.Save;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.compile.util.TypeHelper;
import org.brightify.torch.filter.Property;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class BundledFeatureProvider implements FeatureProvider {

    @Inject
    private TypeHelper typeHelper;

    @Override
    public void constructFeatures(PropertyMirror propertyMirror, List<JExpression> featureConstructions) {
        JCodeModel codeModel = CodeModelTypes.CODE_MODEL;

        Id id = typeHelper.getAnnotation(propertyMirror.getAnnotations(), Id.class);
        if(id != null) {
            JInvocation idExpresion = JExpr._new(codeModel.ref(Id.IdFeature.class)).arg(JExpr.lit(id.autoIncrement()));

            featureConstructions.add(idExpresion);
        }
        Load load = typeHelper.getAnnotation(propertyMirror.getAnnotations(), Load.class);
        if(load != null) {
            JArray whenValues = classArrayToArrayExpression(load.when());
            JArray unlessValues = classArrayToArrayExpression(load.unless());

            JInvocation expression = JExpr._new(codeModel.ref(Load.LoadFeature.class))
                    .arg(whenValues).arg(unlessValues);
            featureConstructions.add(expression);
        }
        Save save = typeHelper.getAnnotation(propertyMirror.getAnnotations(), Save.class);
        if(save != null) {
            JArray whenValues = classArrayToArrayExpression(save.when());
            JArray unlessValues = classArrayToArrayExpression(save.unless());

            JInvocation expression = JExpr._new(codeModel.ref(Save.SaveFeature.class))
                    .arg(whenValues).arg(unlessValues);
            featureConstructions.add(expression);
        }
        Delete delete = typeHelper.getAnnotation(propertyMirror.getAnnotations(), Delete.class);
        if(delete != null) {
            JArray whenValues = classArrayToArrayExpression(delete.when());
            JArray unlessValues = classArrayToArrayExpression(delete.unless());

            JInvocation expression = JExpr._new(codeModel.ref(Delete.DeleteFeature.class))
                    .arg(whenValues).arg(unlessValues);
            featureConstructions.add(expression);
        }


    }

    private JArray classArrayToArrayExpression(Class<?>... values) {
        JArray output = JExpr.newArray(CodeModelTypes.CLASS.narrow(CodeModelTypes.WILDCARD));

        for (Class<?> value : values) {
            output.add(CodeModelTypes.CODE_MODEL.ref(value).dotclass());
        }

        return output;
    }
}
