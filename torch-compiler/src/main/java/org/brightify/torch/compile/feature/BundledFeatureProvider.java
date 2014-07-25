package org.brightify.torch.compile.feature;

import com.google.inject.Inject;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import org.brightify.torch.annotation.Id;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.compile.util.TypeHelper;

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
    }
}
