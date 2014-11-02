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
        with(databaseEngine).register(new User_1_0_0_Description());

        User_1_0_0 entity1 = new User_1_0_0();
        entity1.username = "john1";
        entity1.name = "John Doe";
        entity1.email = "john@doe.com";

        torch().save().entity(entity1);

        assertThat(entity1.id, is(not(nullValue())));

        forceUnload();

        with(databaseEngine).register(new User_1_1_0_Description());

        User_1_1_0 entity2 = torch().load().type(User_1_1_0.class).single();

        assertThat(entity2.id, is(entity1.id));
        assertThat(entity2.username, is(entity1.username));
        assertThat(entity2.fullName, is(entity1.name));
        assertThat(entity2.email, is(entity1.email));
    }

    @Test
    public void addPropertyAndMigrateData() {
        with(databaseEngine).register(new User_1_1_0_Description());

        User_1_1_0 entity1 = new User_1_1_0();
        entity1.username = "john1";
        entity1.fullName = "John Doe";
        entity1.email = "john@doe.com";

        torch().save().entity(entity1);

        assertThat(entity1.id, is(not(nullValue())));

        forceUnload();

        with(databaseEngine).register(new User_1_2_0_Description());

        User_1_2_0 entity2 = torch().load().type(User_1_2_0.class).single();

        assertThat(entity2.id, is(entity1.id));
        assertThat(entity2.username, is(entity1.username));
        assertThat(entity2.firstName + " " + entity2.lastName, is(entity1.fullName));
        assertThat(entity2.email, is(entity1.email));
    }

    @Test
    public void removeProperty() {
        with(databaseEngine).register(new User_1_2_0_Description());

        User_1_2_0 entity1 = new User_1_2_0();
        entity1.username = "john1";
        entity1.firstName = "John";
        entity1.lastName = "Doe";
        entity1.email = "john@doe.com";

        torch().save().entity(entity1);

        assertThat(entity1.id, is(not(nullValue())));

        forceUnload();

        with(databaseEngine).register(new User_1_3_0_Description());

        User_1_3_0 entity2 = torch().load().type(User_1_3_0.class).single();

        assertThat(entity2.id, is(entity1.id));
        assertThat(entity2.username, is(entity1.username));
        assertThat(entity2.firstName, is(entity1.firstName));
        assertThat(entity2.lastName, is(entity1.lastName));
    }

    public ENGINE getDatabaseEngine() {
        return databaseEngine;
    }

    protected abstract ENGINE prepareDatabaseEngine();

    private static class User_1_0_0 {

        public Long id;

        public String username;

        public String name;

        public String email;
    }

    private static class User_1_0_0_Description implements EntityDescription<User_1_0_0> {

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
        public void setFromRawEntity(TorchFactory torchFactory, ReadableRawEntity rawEntity, User_1_0_0 entity,
                                              Set<Class<?>> loadGroups) throws Exception {
            entity.id = rawEntity.getLong(idProperty.getSafeName());
            entity.username = rawEntity.getString(usernameProperty.getSafeName());
            entity.name = rawEntity.getString(nameProperty.getSafeName());
            entity.email = rawEntity.getString(emailProperty.getSafeName());
        }

        @Override
        public User_1_0_0 createEmpty() {
            return new User_1_0_0();
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
        public String getVersion() {
            return "1.0.0";
        }

        @Override
        public Entity.MigrationType getMigrationType() {
            return Entity.MigrationType.MIGRATE;
        }

        @Override
        public Long getEntityId(User_1_0_0 entity) {
            return entity.id;
        }

        @Override
        public void setEntityId(User_1_0_0 entity, Long id) {
            entity.id = id;
        }

        @Override
        public Class<User_1_0_0> getEntityClass() {
            return User_1_0_0.class;
        }


        @Override
        public void toRawEntity(TorchFactory torchFactory, User_1_0_0 entity,
                                WritableRawEntity rawEntity) throws Exception {
            rawEntity.put(idProperty.getSafeName(), entity.id);
            rawEntity.put(usernameProperty.getSafeName(), entity.username);
            rawEntity.put(nameProperty.getSafeName(), entity.name);
            rawEntity.put(emailProperty.getSafeName(), entity.email);
        }

        @Override
        public void migrate(MigrationAssistant<User_1_0_0> assistant, String sourceVersion,
                            String targetVersion) throws Exception {
            throw new MigrationException(
                    (((("Unable to migrate entity! Could not find migration path from '" + sourceVersion) + "' to '") +
                      targetVersion) + "'!"));
        }
    }

    private static class User_1_1_0 {

        public Long id;

        public String username;

        public String fullName;

        public String email;

        public static void migrate_100_110(MigrationAssistant<User_1_1_0> assistant) {
            assistant.renameProperty(User_1_0_0_Description.nameProperty.getSafeName(),
                                     User_1_1_0_Description.fullNameProperty.getSafeName());
        }
    }

    private static class User_1_1_0_Description implements EntityDescription<User_1_1_0> {
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
        public void setFromRawEntity(TorchFactory torchFactory, ReadableRawEntity rawEntity, User_1_1_0 entity,
                                              Set<Class<?>> loadGroups) throws Exception {
            entity.id = rawEntity.getLong(idProperty.getSafeName());
            entity.username = rawEntity.getString(usernameProperty.getSafeName());
            entity.fullName = rawEntity.getString(fullNameProperty.getSafeName());
            entity.email = rawEntity.getString(emailProperty.getSafeName());
        }

        @Override
        public User_1_1_0 createEmpty() {
            return new User_1_1_0();
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
        public String getVersion() {
            return "1.1.0";
        }

        @Override
        public Entity.MigrationType getMigrationType() {
            return Entity.MigrationType.MIGRATE;
        }

        @Override
        public Long getEntityId(User_1_1_0 entity) {
            return entity.id;
        }

        @Override
        public void setEntityId(User_1_1_0 entity, Long id) {
            entity.id = id;
        }

        @Override
        public Class<User_1_1_0> getEntityClass() {
            return User_1_1_0.class;
        }


        @Override
        public void toRawEntity(TorchFactory torchFactory, User_1_1_0 entity,
                                WritableRawEntity rawEntity) throws Exception {
            rawEntity.put(idProperty.getSafeName(), entity.id);
            rawEntity.put(usernameProperty.getSafeName(), entity.username);
            rawEntity.put(fullNameProperty.getSafeName(), entity.fullName);
            rawEntity.put(emailProperty.getSafeName(), entity.email);
        }

        @Override
        public void migrate(MigrationAssistant<User_1_1_0> assistant, String sourceVersion,
                            String targetVersion) throws Exception {
            String migration = ((sourceVersion + "->") + targetVersion);
            if (migration.equals("1.0.0->1.1.0")) {
                assistant.renameProperty(User_1_0_0_Description.nameProperty.getSafeName(),
                                         User_1_1_0_Description.fullNameProperty.getSafeName());
            } else {
                throw new MigrationException(
                        (((("Unable to migrate entity! Could not find migration path from '" + sourceVersion) +
                           "' to '") + targetVersion) + "'!"));
            }
        }
    }

    private static class User_1_2_0 {

        public Long id;

        public String username;

        public String firstName;

        public String lastName;

        public String email;


        public static void migrate_100_110(MigrationAssistant<User_1_2_0> assistant) {
            assistant.renameProperty(User_1_0_0_Description.nameProperty.getSafeName(),
                                     User_1_1_0_Description.fullNameProperty.getSafeName());
        }

        public static void migrate_110_120(MigrationAssistant<User_1_2_0> assistant) {
            assistant.renameProperty(User_1_1_0_Description.fullNameProperty.getSafeName(),
                                     User_1_2_0_Description.firstNameProperty.getSafeName());

            assistant.addProperty(User_1_2_0_Description.lastNameProperty);

            assistant.torch().load().type(User_1_2_0.class).process().each(new EditFunction<User_1_2_0>() {
                @Override
                public boolean apply(User_1_2_0 entity) {
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

    private static class User_1_2_0_Description implements EntityDescription<User_1_2_0> {
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
        public String getVersion() {
            return "1.2.0";
        }

        @Override
        public Entity.MigrationType getMigrationType() {
            return Entity.MigrationType.MIGRATE;
        }

        @Override
        public Long getEntityId(User_1_2_0 entity) {
            return entity.id;
        }

        @Override
        public void setEntityId(User_1_2_0 entity, Long id) {
            entity.id = id;
        }

        @Override
        public Class<User_1_2_0> getEntityClass() {
            return User_1_2_0.class;
        }

        @Override
        public void setFromRawEntity(TorchFactory torchFactory, ReadableRawEntity rawEntity, User_1_2_0 entity,
                                           Set<Class<?>> loadGroups) throws Exception {
            entity.id = rawEntity.getLong(idProperty.getSafeName());
            entity.username = rawEntity.getString(usernameProperty.getSafeName());
            entity.firstName = rawEntity.getString(firstNameProperty.getSafeName());
            entity.lastName = rawEntity.getString(lastNameProperty.getSafeName());
            entity.email = rawEntity.getString(emailProperty.getSafeName());
        }

        @Override
        public User_1_2_0 createEmpty() {
            return new User_1_2_0();
        }

        @Override
        public void toRawEntity(TorchFactory torchFactory, User_1_2_0 entity,
                                WritableRawEntity rawEntity) throws Exception {
            rawEntity.put(idProperty.getSafeName(), entity.id);
            rawEntity.put(usernameProperty.getSafeName(), entity.username);
            rawEntity.put(firstNameProperty.getSafeName(), entity.firstName);
            rawEntity.put(lastNameProperty.getSafeName(), entity.lastName);
            rawEntity.put(emailProperty.getSafeName(), entity.email);
        }

        @Override
        public void migrate(MigrationAssistant<User_1_2_0> assistant, String sourceVersion,
                            String targetVersion) throws Exception {
            String migration = ((sourceVersion + "->") + targetVersion);
            if (migration.equals("1.0.0->1.1.0")) {
                User_1_2_0.migrate_100_110(assistant);
            } else if (migration.equals("1.0.0->1.2.0")) {
                User_1_2_0.migrate_100_110(assistant);
                User_1_2_0.migrate_110_120(assistant);
            } else if (migration.equals("1.1.0->1.2.0")) {
                User_1_2_0.migrate_110_120(assistant);
            } else {
                throw new MigrationException(
                        (((("Unable to migrate entity! Could not find migration path from '" + sourceVersion) +
                           "' to '") + targetVersion) + "'!"));
            }
        }
    }

    private static class User_1_3_0 {

        public Long id;

        public String username;

        public String firstName;

        public String lastName;


        public static void migrate_100_110(MigrationAssistant<User_1_3_0> assistant) {
            assistant.renameProperty(User_1_0_0_Description.nameProperty.getSafeName(),
                                     User_1_1_0_Description.fullNameProperty.getSafeName());
        }

        public static void migrate_110_120(MigrationAssistant<User_1_3_0> assistant) {
            assistant.renameProperty(User_1_1_0_Description.fullNameProperty.getSafeName(),
                                     User_1_2_0_Description.firstNameProperty.getSafeName());

            assistant.addProperty(User_1_2_0_Description.lastNameProperty);

            assistant.torch().load().type(User_1_3_0.class).process().each(new EditFunction<User_1_3_0>() {
                @Override
                public boolean apply(User_1_3_0 entity) {
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

        public static void migrate_120_130(MigrationAssistant<User_1_3_0> assistant) {
            assistant.removeProperty("email");
        }
    }

    private static class User_1_3_0_Description implements EntityDescription<User_1_3_0> {
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
        public String getVersion() {
            return "1.2.0";
        }

        @Override
        public Entity.MigrationType getMigrationType() {
            return Entity.MigrationType.MIGRATE;
        }

        @Override
        public Long getEntityId(User_1_3_0 entity) {
            return entity.id;
        }

        @Override
        public void setEntityId(User_1_3_0 entity, Long id) {
            entity.id = id;
        }

        @Override
        public Class<User_1_3_0> getEntityClass() {
            return User_1_3_0.class;
        }

        @Override
        public void setFromRawEntity(TorchFactory torchFactory, ReadableRawEntity rawEntity, User_1_3_0 entity,
                                     Set<Class<?>> loadGroups) throws Exception {
            entity.id = rawEntity.getLong(idProperty.getSafeName());
            entity.username = rawEntity.getString(usernameProperty.getSafeName());
            entity.firstName = rawEntity.getString(firstNameProperty.getSafeName());
            entity.lastName = rawEntity.getString(lastNameProperty.getSafeName());
        }

        @Override
        public User_1_3_0 createEmpty() {
            return new User_1_3_0();
        }

        @Override
        public void toRawEntity(TorchFactory torchFactory, User_1_3_0 entity,
                                WritableRawEntity rawEntity) throws Exception {
            rawEntity.put(idProperty.getSafeName(), entity.id);
            rawEntity.put(usernameProperty.getSafeName(), entity.username);
            rawEntity.put(firstNameProperty.getSafeName(), entity.firstName);
            rawEntity.put(lastNameProperty.getSafeName(), entity.lastName);
        }

        @Override
        public void migrate(MigrationAssistant<User_1_3_0> assistant, String sourceVersion,
                            String targetVersion) throws Exception {
            String migration = ((sourceVersion + "->") + targetVersion);
            if (migration.equals("1.0.0->1.1.0")) {
                User_1_3_0.migrate_100_110(assistant);
            } else if (migration.equals("1.0.0->1.2.0")) {
                User_1_3_0.migrate_100_110(assistant);
                User_1_3_0.migrate_110_120(assistant);
            } else if (migration.equals("1.1.0->1.2.0")) {
                User_1_3_0.migrate_110_120(assistant);
            } else if (migration.equals("1.0.0->1.3.0")) {
                User_1_3_0.migrate_100_110(assistant);
                User_1_3_0.migrate_110_120(assistant);
                User_1_3_0.migrate_120_130(assistant);
            } else if (migration.equals("1.1.0->1.3.0")) {
                User_1_3_0.migrate_110_120(assistant);
                User_1_3_0.migrate_120_130(assistant);
            } else if (migration.equals("1.2.0->1.3.0")) {
                User_1_3_0.migrate_120_130(assistant);
            } else {
                throw new MigrationException(
                        (((("Unable to migrate entity! Could not find migration path from '" + sourceVersion) +
                           "' to '") + targetVersion) + "'!"));
            }
        }
    }
}
