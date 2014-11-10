package org.brightify.torch.test;

import org.brightify.torch.DatabaseEngine;
import org.brightify.torch.EntityDescription;
import org.brightify.torch.ReadableRawEntity;
import org.brightify.torch.TorchFactory;
import org.brightify.torch.WritableRawEntity;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;
import org.brightify.torch.filter.NumberProperty;
import org.brightify.torch.filter.Property;
import org.brightify.torch.filter.StringProperty;
import org.brightify.torch.impl.filter.NumberPropertyImpl;
import org.brightify.torch.impl.filter.StringPropertyImpl;
import org.brightify.torch.util.Helper;
import org.brightify.torch.util.MigrationAssistant;
import org.brightify.torch.util.MigrationException;
import org.brightify.torch.util.functional.EditFunction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.brightify.torch.TorchService.forceUnload;
import static org.brightify.torch.TorchService.torch;
import static org.brightify.torch.TorchService.with;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public abstract class AbstractMigrationTest<ENGINE extends DatabaseEngine> {

    private ENGINE databaseEngine;

    @Before
    public void setUp() throws Exception {
        databaseEngine = prepareDatabaseEngine();
    }

    @After
    public void tearDown() throws Exception {
        databaseEngine.wipe();
        databaseEngine = null;

        forceUnload();
    }

    @Test
    public void renameProperty() {
        with(databaseEngine).register(new User_r1_Description());

        User_r1 entity1 = new User_r1();
        entity1.username = "john1";
        entity1.name = "John Doe";
        entity1.email = "john@doe.com";

        torch().save().entity(entity1);

        assertThat(entity1.id, is(not(nullValue())));

        forceUnload();

        with(databaseEngine).register(new User_r2_Description());

        User_r2 entity2 = torch().load().type(User_r2.class).single();

        assertThat(entity2.id, is(entity1.id));
        assertThat(entity2.username, is(entity1.username));
        assertThat(entity2.fullName, is(entity1.name));
        assertThat(entity2.email, is(entity1.email));
    }

    @Test
    public void addPropertyAndMigrateData() {
        with(databaseEngine).register(new User_r2_Description());

        User_r2 entity1 = new User_r2();
        entity1.username = "john1";
        entity1.fullName = "John Doe";
        entity1.email = "john@doe.com";

        torch().save().entity(entity1);

        assertThat(entity1.id, is(not(nullValue())));

        forceUnload();

        with(databaseEngine).register(new User_r3_Description());

        User_r3 entity2 = torch().load().type(User_r3.class).single();

        assertThat(entity2.id, is(entity1.id));
        assertThat(entity2.username, is(entity1.username));
        assertThat(entity2.firstName + " " + entity2.lastName, is(entity1.fullName));
        assertThat(entity2.email, is(entity1.email));
    }

    @Test
    public void removeProperty() {
        with(databaseEngine).register(new User_r3_Description());

        User_r3 entity1 = new User_r3();
        entity1.username = "john1";
        entity1.firstName = "John";
        entity1.lastName = "Doe";
        entity1.email = "john@doe.com";

        torch().save().entity(entity1);

        assertThat(entity1.id, is(not(nullValue())));

        forceUnload();

        with(databaseEngine).register(new User_r4_Description());

        User_r4 entity2 = torch().load().type(User_r4.class).single();

        assertThat(entity2.id, is(entity1.id));
        assertThat(entity2.username, is(entity1.username));
        assertThat(entity2.firstName, is(entity1.firstName));
        assertThat(entity2.lastName, is(entity1.lastName));
    }

    public ENGINE getDatabaseEngine() {
        return databaseEngine;
    }

    protected abstract ENGINE prepareDatabaseEngine();

    private static class User_r1 {

        public Long id;

        public String username;

        public String name;

        public String email;
    }

    private static class User_r1_Description implements EntityDescription<User_r1> {

        public static NumberProperty<Long> idProperty =
                new NumberPropertyImpl<Long>("id", "torch_id", Long.class, new Id.IdFeature(true));
        public static StringProperty usernameProperty = new StringPropertyImpl("username", "torch_username");
        public static StringProperty nameProperty = new StringPropertyImpl("name", "torch_name");
        public static StringProperty emailProperty = new StringPropertyImpl("email", "torch_email");

        public static Property<?>[] properties = {
                idProperty,
                usernameProperty,
                nameProperty,
                emailProperty
        };

        @Override
        public NumberProperty<Long> getIdProperty() {
            return idProperty;
        }

        @Override
        public void setFromRawEntity(TorchFactory torchFactory, ReadableRawEntity rawEntity, User_r1 entity,
                                              Set<Class<?>> loadGroups) throws Exception {
            entity.id = rawEntity.getLong(idProperty.getSafeName());
            entity.username = rawEntity.getString(usernameProperty.getSafeName());
            entity.name = rawEntity.getString(nameProperty.getSafeName());
            entity.email = rawEntity.getString(emailProperty.getSafeName());
        }

        @Override
        public User_r1 createEmpty() {
            return new User_r1();
        }

        @Override
        public Property<?>[] getProperties() {
            return properties;
        }

        @Override
        public String getSafeName() {
            return "org_brightify_torch_test_AbstractMigrationTest_User";
        }

        @Override
        public long getRevision() {
            return 1;
        }

        @Override
        public Entity.MigrationType getMigrationType() {
            return Entity.MigrationType.MIGRATE;
        }

        @Override
        public Long getEntityId(User_r1 entity) {
            return entity.id;
        }

        @Override
        public void setEntityId(User_r1 entity, Long id) {
            entity.id = id;
        }

        @Override
        public Class<User_r1> getEntityClass() {
            return User_r1.class;
        }


        @Override
        public void toRawEntity(TorchFactory torchFactory, User_r1 entity,
                                WritableRawEntity rawEntity) throws Exception {
            rawEntity.put(idProperty.getSafeName(), entity.id);
            rawEntity.put(usernameProperty.getSafeName(), entity.username);
            rawEntity.put(nameProperty.getSafeName(), entity.name);
            rawEntity.put(emailProperty.getSafeName(), entity.email);
        }

        @Override
        public void migrate(MigrationAssistant<User_r1> assistant, long sourceRevision,
                            long targetRevision) throws Exception {
            throw new MigrationException(
                    (((("Unable to migrate entity! Could not find migration path from '" + sourceRevision) + "' to '") +
                            targetRevision) + "'!"));
        }
    }

    private static class User_r2 {

        public Long id;

        public String username;

        public String fullName;

        public String email;

        public static void migrate_r1_r2(MigrationAssistant<User_r2> assistant) {
            assistant.renameProperty(User_r1_Description.nameProperty.getSafeName(),
                    User_r2_Description.fullNameProperty.getSafeName());
        }
    }

    private static class User_r2_Description implements EntityDescription<User_r2> {
        public static NumberProperty<Long> idProperty =
                new NumberPropertyImpl<Long>("id", "torch_id", Long.class, new Id.IdFeature(true));
        public static StringProperty usernameProperty = new StringPropertyImpl("username", "torch_username");
        public static StringProperty fullNameProperty = new StringPropertyImpl("fullName", "torch_fullName");
        public static StringProperty emailProperty = new StringPropertyImpl("email", "torch_email");

        public static Property<?>[] properties = {
                idProperty,
                usernameProperty,
                fullNameProperty,
                emailProperty
        };

        @Override
        public void setFromRawEntity(TorchFactory torchFactory, ReadableRawEntity rawEntity, User_r2 entity,
                                              Set<Class<?>> loadGroups) throws Exception {
            entity.id = rawEntity.getLong(idProperty.getSafeName());
            entity.username = rawEntity.getString(usernameProperty.getSafeName());
            entity.fullName = rawEntity.getString(fullNameProperty.getSafeName());
            entity.email = rawEntity.getString(emailProperty.getSafeName());
        }

        @Override
        public User_r2 createEmpty() {
            return new User_r2();
        }

        @Override
        public NumberProperty<Long> getIdProperty() {
            return idProperty;
        }

        @Override
        public Property<?>[] getProperties() {
            return properties;
        }

        @Override
        public String getSafeName() {
            return "org_brightify_torch_test_AbstractMigrationTest_User";
        }

        @Override
        public long getRevision() {
            return 2;
        }

        @Override
        public Entity.MigrationType getMigrationType() {
            return Entity.MigrationType.MIGRATE;
        }

        @Override
        public Long getEntityId(User_r2 entity) {
            return entity.id;
        }

        @Override
        public void setEntityId(User_r2 entity, Long id) {
            entity.id = id;
        }

        @Override
        public Class<User_r2> getEntityClass() {
            return User_r2.class;
        }


        @Override
        public void toRawEntity(TorchFactory torchFactory, User_r2 entity,
                                WritableRawEntity rawEntity) throws Exception {
            rawEntity.put(idProperty.getSafeName(), entity.id);
            rawEntity.put(usernameProperty.getSafeName(), entity.username);
            rawEntity.put(fullNameProperty.getSafeName(), entity.fullName);
            rawEntity.put(emailProperty.getSafeName(), entity.email);
        }

        @Override
        public void migrate(MigrationAssistant<User_r2> assistant, long sourceRevision,
                            long targetRevision) throws Exception {
            String migration = ((sourceRevision + "->") + targetRevision);
            if (migration.equals("1->2")) {
                User_r2.migrate_r1_r2(assistant);
            } else {
                throw new MigrationException(
                        (((("Unable to migrate entity! Could not find migration path from '" + sourceRevision) +
                           "' to '") + targetRevision) + "'!"));
            }
        }
    }

    private static class User_r3 {

        public Long id;

        public String username;

        public String firstName;

        public String lastName;

        public String email;


        public static void migrate_r1_r2(MigrationAssistant<User_r3> assistant) {
            assistant.renameProperty(User_r1_Description.nameProperty.getSafeName(),
                    User_r2_Description.fullNameProperty.getSafeName());
        }

        public static void migrate_r2_r3(MigrationAssistant<User_r3> assistant) {
            assistant.renameProperty(User_r2_Description.fullNameProperty.getSafeName(),
                    User_r3_Description.firstNameProperty.getSafeName());

            assistant.addProperty(User_r3_Description.lastNameProperty);

            assistant.torch().load().type(User_r3.class).process().each(new EditFunction<User_r3>() {
                @Override
                public boolean apply(User_r3 entity) {
                    String[] splitName = entity.firstName.split(" ");

                    if(splitName.length > 1) {
                        entity.firstName = splitName[0];
                        entity.lastName = Helper.stringsToString(splitName, 1, " ");
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
    }

    private static class User_r3_Description implements EntityDescription<User_r3> {
        public static NumberProperty<Long> idProperty =
                new NumberPropertyImpl<Long>("id", "torch_id", Long.class, new Id.IdFeature(true));
        public static StringProperty usernameProperty = new StringPropertyImpl("username", "torch_username");
        public static StringProperty firstNameProperty = new StringPropertyImpl("firstName", "torch_firstName");
        public static StringProperty lastNameProperty = new StringPropertyImpl("lastName", "torch_lastName");
        public static StringProperty emailProperty = new StringPropertyImpl("email", "torch_email");

        public static Property<?>[] properties = {
                idProperty,
                usernameProperty,
                firstNameProperty,
                lastNameProperty,
                emailProperty
        };

        @Override
        public NumberProperty<Long> getIdProperty() {
            return idProperty;
        }

        @Override
        public Property<?>[] getProperties() {
            return properties;
        }

        @Override
        public String getSafeName() {
            return "org_brightify_torch_test_AbstractMigrationTest_User";
        }

        @Override
        public long getRevision() {
            return 3;
        }

        @Override
        public Entity.MigrationType getMigrationType() {
            return Entity.MigrationType.MIGRATE;
        }

        @Override
        public Long getEntityId(User_r3 entity) {
            return entity.id;
        }

        @Override
        public void setEntityId(User_r3 entity, Long id) {
            entity.id = id;
        }

        @Override
        public Class<User_r3> getEntityClass() {
            return User_r3.class;
        }

        @Override
        public void setFromRawEntity(TorchFactory torchFactory, ReadableRawEntity rawEntity, User_r3 entity,
                                           Set<Class<?>> loadGroups) throws Exception {
            entity.id = rawEntity.getLong(idProperty.getSafeName());
            entity.username = rawEntity.getString(usernameProperty.getSafeName());
            entity.firstName = rawEntity.getString(firstNameProperty.getSafeName());
            entity.lastName = rawEntity.getString(lastNameProperty.getSafeName());
            entity.email = rawEntity.getString(emailProperty.getSafeName());
        }

        @Override
        public User_r3 createEmpty() {
            return new User_r3();
        }

        @Override
        public void toRawEntity(TorchFactory torchFactory, User_r3 entity,
                                WritableRawEntity rawEntity) throws Exception {
            rawEntity.put(idProperty.getSafeName(), entity.id);
            rawEntity.put(usernameProperty.getSafeName(), entity.username);
            rawEntity.put(firstNameProperty.getSafeName(), entity.firstName);
            rawEntity.put(lastNameProperty.getSafeName(), entity.lastName);
            rawEntity.put(emailProperty.getSafeName(), entity.email);
        }

        @Override
        public void migrate(MigrationAssistant<User_r3> assistant, long sourceRevision,
                            long targetRevision) throws Exception {
            String migration = ((sourceRevision + "->") + targetRevision);
            if (migration.equals("1->2")) {
                User_r3.migrate_r1_r2(assistant);
            } else if (migration.equals("1->3")) {
                User_r3.migrate_r1_r2(assistant);
                User_r3.migrate_r2_r3(assistant);
            } else if (migration.equals("2->3")) {
                User_r3.migrate_r2_r3(assistant);
            } else {
                throw new MigrationException(
                        (((("Unable to migrate entity! Could not find migration path from '" + sourceRevision) +
                           "' to '") + targetRevision) + "'!"));
            }
        }
    }

    private static class User_r4 {

        public Long id;

        public String username;

        public String firstName;

        public String lastName;


        public static void migrate_r1_r2(MigrationAssistant<User_r4> assistant) {
            assistant.renameProperty(User_r1_Description.nameProperty.getSafeName(),
                    User_r2_Description.fullNameProperty.getSafeName());
        }

        public static void migrate_r2_r3(MigrationAssistant<User_r4> assistant) {
            assistant.renameProperty(User_r2_Description.fullNameProperty.getSafeName(),
                    User_r3_Description.firstNameProperty.getSafeName());

            assistant.addProperty(User_r3_Description.lastNameProperty);

            assistant.torch().load().type(User_r4.class).process().each(new EditFunction<User_r4>() {
                @Override
                public boolean apply(User_r4 entity) {
                    String[] splitName = entity.firstName.split(" ");

                    if(splitName.length > 1) {
                        entity.firstName = splitName[0];
                        entity.lastName = Helper.stringsToString(splitName, 1, " ");
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }

        public static void migrate_r3_r4(MigrationAssistant<User_r4> assistant) {
            assistant.removeProperty("email");
        }
    }

    private static class User_r4_Description implements EntityDescription<User_r4> {
        public static NumberProperty<Long> idProperty =
                new NumberPropertyImpl<Long>("id", "torch_id", Long.class, new Id.IdFeature(true));
        public static StringProperty usernameProperty = new StringPropertyImpl("username", "torch_username");
        public static StringProperty firstNameProperty = new StringPropertyImpl("firstName", "torch_firstName");
        public static StringProperty lastNameProperty = new StringPropertyImpl("lastName", "torch_lastName");
        public static StringProperty emailProperty = new StringPropertyImpl("email", "torch_email");

        public static Property<?>[] properties = {
                idProperty,
                usernameProperty,
                firstNameProperty,
                lastNameProperty,
                emailProperty
        };

        @Override
        public NumberProperty<Long> getIdProperty() {
            return idProperty;
        }

        @Override
        public Property<?>[] getProperties() {
            return properties;
        }

        @Override
        public String getSafeName() {
            return "org_brightify_torch_test_AbstractMigrationTest_User";
        }

        @Override
        public long getRevision() {
            return 4;
        }

        @Override
        public Entity.MigrationType getMigrationType() {
            return Entity.MigrationType.MIGRATE;
        }

        @Override
        public Long getEntityId(User_r4 entity) {
            return entity.id;
        }

        @Override
        public void setEntityId(User_r4 entity, Long id) {
            entity.id = id;
        }

        @Override
        public Class<User_r4> getEntityClass() {
            return User_r4.class;
        }

        @Override
        public void setFromRawEntity(TorchFactory torchFactory, ReadableRawEntity rawEntity, User_r4 entity,
                                     Set<Class<?>> loadGroups) throws Exception {
            entity.id = rawEntity.getLong(idProperty.getSafeName());
            entity.username = rawEntity.getString(usernameProperty.getSafeName());
            entity.firstName = rawEntity.getString(firstNameProperty.getSafeName());
            entity.lastName = rawEntity.getString(lastNameProperty.getSafeName());
        }

        @Override
        public User_r4 createEmpty() {
            return new User_r4();
        }

        @Override
        public void toRawEntity(TorchFactory torchFactory, User_r4 entity,
                                WritableRawEntity rawEntity) throws Exception {
            rawEntity.put(idProperty.getSafeName(), entity.id);
            rawEntity.put(usernameProperty.getSafeName(), entity.username);
            rawEntity.put(firstNameProperty.getSafeName(), entity.firstName);
            rawEntity.put(lastNameProperty.getSafeName(), entity.lastName);
        }

        @Override
        public void migrate(MigrationAssistant<User_r4> assistant, long sourceRevision,
                            long targetRevision) throws Exception {
            String migration = ((sourceRevision + "->") + targetRevision);
            if (migration.equals("1->2")) {
                User_r4.migrate_r1_r2(assistant);
            } else if (migration.equals("1->3")) {
                User_r4.migrate_r1_r2(assistant);
                User_r4.migrate_r2_r3(assistant);
            } else if (migration.equals("2->3")) {
                User_r4.migrate_r2_r3(assistant);
            } else if (migration.equals("1->4")) {
                User_r4.migrate_r1_r2(assistant);
                User_r4.migrate_r2_r3(assistant);
                User_r4.migrate_r3_r4(assistant);
            } else if (migration.equals("2->4")) {
                User_r4.migrate_r2_r3(assistant);
                User_r4.migrate_r3_r4(assistant);
            } else if (migration.equals("3->4")) {
                User_r4.migrate_r3_r4(assistant);
            } else {
                throw new MigrationException(
                        (((("Unable to migrate entity! Could not find migration path from '" + sourceRevision) +
                           "' to '") + targetRevision) + "'!"));
            }
        }
    }
}
