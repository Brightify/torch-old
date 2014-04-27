package org.brightify.torch.compile.marshall;

import com.google.inject.Inject;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JVar;
import org.brightify.torch.compile.EntityContext;
import org.brightify.torch.compile.EntityMirror;
import org.brightify.torch.compile.EntityMirrorImpl;
import org.brightify.torch.compile.PropertyMirror;
import org.brightify.torch.compile.PropertyMirrorImpl;
import org.brightify.torch.compile.generate.EntityGenerator;
import org.brightify.torch.compile.generate.EntityMetadataGenerator;
import org.brightify.torch.compile.generate.EntityMetadataGeneratorImpl;
import org.brightify.torch.compile.util.CodeModelTypes;
import org.brightify.torch.compile.util.TypeHelper;
import org.brightify.torch.sql.ColumnDef;
import org.brightify.torch.sql.affinity.IntegerAffinity;
import org.brightify.torch.sql.statement.CreateTable;
import org.brightify.torch.util.Helper;
import org.brightify.torch.util.LazyArrayList;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.List;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class ListMarshaller implements Marshaller {

    @Inject
    private Types types;

    @Inject
    private TypeHelper typeHelper;

    @Inject
    private MarshallerRegistry marshallerRegistry;

    @Inject
    private EntityContext entityContext;

    @Inject
    private EntityGenerator entityGenerator;

    @Inject
    private EntityMetadataGenerator metadataGenerator;

    @Override
    public boolean accepts(TypeMirror type) {
        DeclaredType lazyList =
                types.getDeclaredType(typeHelper.elementOf(LazyArrayList.class), types.getWildcardType(null, null));

        if(!types.isAssignable(lazyList, typeHelper.getWrappedType(type))) {
            return false;
        }

        TypeMirror listType = typeHelper.singleGenericParameter(type);

        return marshallerRegistry.getMarshaller(listType) != null && !entityContext.containsEntity(listType.toString());
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public JStatement marshall(EntityMetadataGenerator.ToContentValuesHolder holder, PropertyMirror propertyMirror) {
        JClass listType = CodeModelTypes.ref(typeHelper.singleGenericParameter(propertyMirror.getType()).toString());

        JBlock block = new JBlock();

        JVar items = block.decl(CodeModelTypes.LIST.narrow(listType), "items",
                                JExpr._new(CodeModelTypes.ARRAY_LIST.narrow(listType)));
        JExpression propertyItems = propertyMirror.getGetter().getValue(holder.entity);

        JConditional isLazyList = block._if(propertyItems._instanceof(CodeModelTypes.LAZY_ARRAY_LIST.narrow(listType)));

        isLazyList._then().assign(items, JExpr.cast(CodeModelTypes.LAZY_ARRAY_LIST.narrow(listType), propertyItems));
        JForEach forEachItem = isLazyList._else().forEach(listType, "item", propertyItems);
        forEachItem.body().add(items.invoke("add").arg(forEachItem.var()));

        block.add(holder.torch.invoke("save").invoke("entities").arg(items));

        return block;
    }

    @Override
    public JStatement unmarshall(EntityMetadataGenerator.CreateFromCursorHolder holder, PropertyMirror propertyMirror) {
        JExpression relation = holder.torch
                .invoke("relation")
                .invoke("with").arg(CodeModelTypes.ref(holder.classHolder.entityMirror.getFullName()).dotclass())
                .invoke("onProperty").arg(holder.classHolder.definedClass.staticRef(propertyMirror.getName()))
                .invoke("parentId").arg(holder.classHolder.entityMirror.getIdPropertyMirror().getGetter().getValue(
                        holder.entity));

        return propertyMirror.getSetter().setValue(holder.entity, relation);
    }

    @Override
    public ColumnDef createColumn(EntityMetadataGenerator.ClassHolder holder, List<CreateTable> tablesToCreate, PropertyMirror propertyMirror) {
        CreateTable table = new CreateTable();
        table.setTableName(Helper.bindingTableName(holder.entityMirror.getFullName(), propertyMirror.getName()));

        ColumnDef idColumn = new ColumnDef("torch_id");
        idColumn.setTypeAffinity(IntegerAffinity.getInstance());
        table.addColumnDef(idColumn);

        ColumnDef parentIdColumn = new ColumnDef("torch_parent");
        parentIdColumn.setTypeAffinity(IntegerAffinity.getInstance());
        table.addColumnDef(parentIdColumn);

        ColumnDef indexColumn = new ColumnDef("torch_index");
        indexColumn.setTypeAffinity(IntegerAffinity.getInstance());
        table.addColumnDef(indexColumn);

        ColumnDef valueColumn = new ColumnDef("torch_value");
        valueColumn.setTypeAffinity(IntegerAffinity.getInstance());
        table.addColumnDef(valueColumn);

        tablesToCreate.add(table);

        EntityMirror listEntity = createEntityMirror(holder, propertyMirror);

        try {
            entityGenerator.generate(listEntity, holder.definedClass._class(listEntity.getSimpleName()), false);

            metadataGenerator.generate(listEntity, holder.definedClass._class(listEntity.getSimpleName() + EntityMetadataGeneratorImpl.METADATA_POSTFIX));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public JFieldVar createColumnField(EntityMetadataGenerator.ClassHolder holder, PropertyMirror propertyMirror) {
        JClass listType = CodeModelTypes.ref(typeHelper.singleGenericParameter(propertyMirror.getType()).toString());
        JClass entityType = CodeModelTypes.ref(holder.entityMirror.getFullName());

        JClass columnClass = CodeModelTypes.LIST_PROPERTY
                .narrow(entityType)
                .narrow(listType);


        JClass columnClassImpl = CodeModelTypes.LIST_PROPERTY_IMPL
                .narrow(entityType)
                .narrow(listType);

        return holder.definedClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, columnClass, propertyMirror.getName(),
                                         JExpr._new(columnClassImpl).arg(propertyMirror.getColumnName()));
    }
}
