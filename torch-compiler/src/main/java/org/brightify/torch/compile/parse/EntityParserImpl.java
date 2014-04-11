package org.brightify.torch.compile.parse;

import com.google.inject.Inject;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.compile.EntityInfo;
import org.brightify.torch.compile.EntityInfoImpl;
import org.brightify.torch.compile.parse.EntityParser;
import org.brightify.torch.parse.EntityParseException;
import org.brightify.torch.util.Helper;
import org.brightify.torch.util.TypeHelper;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;

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

    public EntityInfo parseEntityElement(Element element) {
        Entity entity = element.getAnnotation(Entity.class);
        if (entity == null) {
            throw new EntityParseException(element, "Object %s was not annotated with @Entity!", element);
        }
        if(entity.ignore()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "Entity " + element.getSimpleName() + " ignored, " +
                                                        "because @Entity.ignore is true.", element);
            return null;
        }

        EntityInfoImpl info = new EntityInfoImpl();

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

        info.setTableName(Helper.tableNameFromClassName(tableName));
        info.setDelete(entity.delete());
        info.setVersion(entity.version());
        info.setMigrationType(entity.migration());

        return info;
    }


}
