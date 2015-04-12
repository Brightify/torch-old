package org.brightify.torch.model;

import org.brightify.torch.Ref;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;
import org.brightify.torch.annotation.Index;
import org.brightify.torch.annotation.Load;
import org.brightify.torch.annotation.Migration;
import org.brightify.torch.util.MigrationAssistant;
import org.brightify.torch.util.functional.EditFunction;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
@Entity(revision = 2)
public class Table {

    private Long id;
    private String tableName;
    private Long revision;
    private Ref<TableDetails> details;

    protected String version;

    // FIXME if no generic type argument is set, handle error through messager
    // public Key<TableDetails> details;
    // public Key uglyKey;
    // public List<Key<TableDetails>> detailList;

    @Id
    @Index
    @Load(when = TableDetails.class)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getRevision() {
        return revision;
    }

    public void setRevision(Long revision) {
        this.revision = revision;
    }
/*
    @Ignore
    public TableDetails getDetails() {
        return details.get();
    }

    public void setDetails(TableDetails details) {
        this.details.set(details);
    }
*/
    Ref<TableDetails> getDetailsRef() {
        return details;
    }

    void setDetailsRef(Ref<TableDetails> detailsRef) {
        this.details = detailsRef;
    }

    @Migration(source = 1, target = 2)
    static void migrate_addTableDetailsAndRevision(MigrationAssistant<Table> assistant) {
        assistant.addProperty(Table$.detailsRef);
        assistant.addProperty(Table$.revision);

        assistant.torch().load().type(Table.class).process().each(new EditFunction<Table>() {
            @Override
            public boolean apply(Table table) {
                table.setRevision(Entity.DEFAULT_REVISION);
                return true;
            }
        });

        assistant.removeProperty(Table$.version.getSafeName());
    }

}
