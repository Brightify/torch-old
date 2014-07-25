package org.brightify.torch.compile.parse;

import com.google.inject.Inject;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.compile.EntityMirror;
import org.brightify.torch.compile.EntityMirrorImpl;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.parse.EntityParseException;
import org.brightify.torch.util.Helper;
import org.brightify.torch.compile.util.TypeHelper;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.ArrayList;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class EntityParserImpl implements EntityParser {

    @Inject
    private Types types;

    @Inject
    private Messager messager;

    @Inject
    private TypeHelper typeHelper;

    @Inject
    private PropertyParser propertyParser;

    @Override
    public EntityMirror parseEntityElement(Element element) {
        Entity entity = element.getAnnotation(Entity.class);
        if (entity == null) {
            throw new EntityParseException(element, "Object %s was not annotated with @Entity!", element);
        }
        if (entity.ignore()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "Entity " + element.getSimpleName() + " ignored, " +
                                                        "because @Entity.ignore is true.", element);
            return null;
        }

        EntityMirrorImpl info = new EntityMirrorImpl();

        info.setName(element.getSimpleName().toString());
        info.setFullName(element.toString());
        info.setPackageName(typeHelper.packageOf(element));

        String tableName;
        if (!entity.name().equals("")) {
            tableName = entity.name();
        } else if (entity.useSimpleName()) {
            tableName = info.getSimpleName();
        } else {
            tableName = info.getFullName();
        }

        info.setTableName(Helper.safeNameFromClassName(tableName));
        info.setDelete(entity.delete());
        info.setVersion(entity.version());
        info.setMigrationType(entity.migration());

        info.setProperties(new ArrayList<PropertyMirror>(propertyParser.parseEntityElement(element).values()));
        for (PropertyMirror propertyMirror : info.getProperties()) {
            if (propertyMirror.getId() != null) {
                if (info.getIdPropertyMirror() != null) {
                    throw new EntityParseException(propertyMirror.getGetter().getElement(),
                                                   "There can be exactly one @Id property in each entity!");
                }

                info.setIdPropertyMirror(propertyMirror);
            }
        }
        // We need ID property to be the first
        info.getProperties().remove(info.getIdPropertyMirror());
        info.getProperties().add(0, info.getIdPropertyMirror());

        return info;
    }


}
