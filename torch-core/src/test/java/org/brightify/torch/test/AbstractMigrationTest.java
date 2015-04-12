package org.brightify.torch.test;

import org.brightify.torch.DatabaseEngine;
import org.brightify.torch.EntityDescription;
import org.brightify.torch.TorchConfiguration;
import org.brightify.torch.TorchFactoryImpl;
import org.brightify.torch.TorchService;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;
import org.brightify.torch.filter.NumberProperty;
import org.brightify.torch.filter.Property;
import org.brightify.torch.filter.ReferenceProperty;
import org.brightify.torch.filter.StringProperty;
import org.brightify.torch.filter.ValueProperty;
import org.brightify.torch.impl.filter.LongPropertyImpl;
import org.brightify.torch.impl.filter.StringPropertyImpl;
import org.brightify.torch.util.ArrayListBuilder;
import org.brightify.torch.util.Helper;
import org.brightify.torch.util.MigrationAssistant;
import org.brightify.torch.util.MigrationException;
import org.brightify.torch.util.functional.EditFunction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.brightify.torch.TorchService.torch;
import static org.brightify.torch.TorchService.unload;
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
        unload();
    }

    @Test
    public void renameProperty() {
        initWith(new User_r1_Description());

        User_r1 entity1 = new User_r1();
        entity1.username = "john1";
        entity1.name = "John Doe";
        entity1.email = "john@doe.com";

        torch().save().entity(entity1);

        assertThat(entity1.id, is(not(nullValue())));

        unload();

        initWith(new User_r2_Description());

        User_r2 entity2 = torch().load().type(User_r2.class).single();

        assertThat(entity2.id, is(entity1.id));
        assertThat(entity2.username, is(entity1.username));
        assertThat(entity2.fullName, is(entity1.name));
        assertThat(entity2.email, is(entity1.email));
    }

    @Test
    public void addPropertyAndMigrateData() {
        initWith(new User_r2_Description());

        User_r2 entity1 = new User_r2();
        entity1.username = "john1";
        entity1.fullName = "John Doe";
        entity1.email = "john@doe.com";

        torch().save().entity(entity1);

        assertThat(entity1.id, is(not(nullValue())));

        unload();

        initWith(new User_r3_Description());

        User_r3 entity2 = torch().load().type(User_r3.class).single();

        assertThat(entity2.id, is(entity1.id));
        assertThat(entity2.username, is(entity1.username));
        assertThat(entity2.firstName + " " + entity2.lastName, is(entity1.fullName));
        assertThat(entity2.email, is(entity1.email));
    }

    @Test
    public void removeProperty() {
        initWith(new User_r3_Description());

        User_r3 entity1 = new User_r3();
        entity1.username = "john1";
        entity1.firstName = "John";
        entity1.lastName = "Doe";
        entity1.email = "john@doe.com";

        torch().save().entity(entity1);

        assertThat(entity1.id, is(not(nullValue())));

        unload();

        initWith(new User_r4_Description());

        User_r4 entity2 = torch().load().type(User_r4.class).single();

        assertThat(entity2.id, is(entity1.id));
        assertThat(entity2.username, is(entity1.username));
        assertThat(entity2.firstName, is(entity1.firstName));
        assertThat(entity2.lastName, is(entity1.lastName));
    }

    private void initWith(EntityDescription<?>... descriptions) {
        TorchConfiguration<?> configuration = new TorchFactoryImpl.BasicConfiguration(databaseEngine);
        configuration.register(descriptions);
        TorchService.initialize(configuration);
    }

    protected abstract ENGINE prepareDatabaseEngine();

    private static class User_r1 {

        public Long id;

        public String username;

        public String name;

        public String email;
    }

    private static class User_r1_Description implements EntityDescription<User_r1> {

        public static NumberProperty<User_r1, Long> idProperty =
                new LongPropertyImpl<User_r1>(User_r1.class, "id", "torch_id") {
                    @Override
                    public Long get(User_r1 entity) {
                        return entity.id;
                    }

                    @Override
                    public void set(User_r1 entity, Long value) {
                        entity.id = value;
                    }
                }.feature(new Id.IdFeature(true));
        public static StringProperty<User_r1> usernameProperty =
                new StringPropertyImpl<User_r1>(User_r1.class, "username", "torch_username") {
                    @Override
                    public String get(User_r1 entity) {
                        return entity.username;
                    }

                    @Override
                    public void set(User_r1 entity, String value) {
                        entity.username = value;
                    }
                };
        public static StringProperty<User_r1> nameProperty =
                new StringPropertyImpl<User_r1>(User_r1.class, "name", "torch_name") {
                    @Override
                    public String get(User_r1 entity) {
                        return entity.name;
                    }

                    @Override
                    public void set(User_r1 entity, String value) {
                        entity.name = value;
                    }
                };
        public static StringProperty<User_r1> emailProperty =
                new StringPropertyImpl<User_r1>(User_r1.class, "email", "torch_email") {
                    @Override
                    public String get(User_r1 entity) {
                        return entity.email;
                    }

                    @Override
                    public void set(User_r1 entity, String value) {
                        entity.email = value;
                    }
                };

        public static final List<? extends Property<User_r1, ?>> properties =
                ArrayListBuilder.<Property<User_r1, ?>>begin()
                                .add(idProperty)
                                .add(usernameProperty)
                                .add(nameProperty)
                                .add(emailProperty)
                                .list();

        public static final List<? extends ValueProperty<User_r1, ?>> valueProperties =
                ArrayListBuilder.<ValueProperty<User_r1, ?>>begin()
                                .add(idProperty)
                                .add(usernameProperty)
                                .add(nameProperty)
                                .add(emailProperty)
                                .list();

        public static final List<? extends ReferenceProperty<User_r1, ?>> referenceProperties =
                ArrayListBuilder.<ReferenceProperty<User_r1, ?>>begin().list();

        @Override
        public NumberProperty<User_r1, Long> getIdProperty() {
            return idProperty;
        }

        @Override
        public User_r1 createEmpty() {
            return new User_r1();
        }

        @Override
        public List<? extends Property<User_r1, ?>> getProperties() {
            return properties;
        }

        @Override
        public List<? extends ValueProperty<User_r1, ?>> getValueProperties() {
            return valueProperties;
        }

        @Override
        public List<? extends ReferenceProperty<User_r1, ?>> getReferenceProperties() {
            return referenceProperties;
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
        public Class<User_r1> getEntityClass() {
            return User_r1.class;
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
        public static NumberProperty<User_r2, Long> idProperty =
                new LongPropertyImpl<User_r2>(User_r2.class, "id", "torch_id") {
                    @Override
                    public Long get(User_r2 entity) {
                        return entity.id;
                    }

                    @Override
                    public void set(User_r2 entity, Long value) {
                        entity.id = value;
                    }
                }.feature(new Id.IdFeature(true));
        public static StringProperty<User_r2> usernameProperty =
                new StringPropertyImpl<User_r2>(User_r2.class, "username", "torch_username") {
                    @Override
                    public String get(User_r2 entity) {
                        return entity.username;
                    }

                    @Override
                    public void set(User_r2 entity, String value) {
                        entity.username = value;
                    }
                };
        public static StringProperty<User_r2> fullNameProperty =
                new StringPropertyImpl<User_r2>(User_r2.class, "fullName", "torch_fullName") {
                    @Override
                    public String get(User_r2 entity) {
                        return entity.fullName;
                    }

                    @Override
                    public void set(User_r2 entity, String value) {
                        entity.fullName = value;
                    }
                };
        public static StringProperty<User_r2> emailProperty =
                new StringPropertyImpl<User_r2>(User_r2.class, "email", "torch_email") {
                    @Override
                    public String get(User_r2 entity) {
                        return entity.email;
                    }

                    @Override
                    public void set(User_r2 entity, String value) {
                        entity.email = value;
                    }
                };

        public static final List<? extends Property<User_r2, ?>> properties =
                ArrayListBuilder.<Property<User_r2, ?>>begin()
                                .add(idProperty)
                                .add(usernameProperty)
                                .add(fullNameProperty)
                                .add(emailProperty)
                                .list();

        public static final List<? extends ValueProperty<User_r2, ?>> valueProperties =
                ArrayListBuilder.<ValueProperty<User_r2, ?>>begin()
                                .add(idProperty)
                                .add(usernameProperty)
                                .add(fullNameProperty)
                                .add(emailProperty)
                                .list();

        public static final List<? extends ReferenceProperty<User_r2, ?>> referenceProperties =
                ArrayListBuilder.<ReferenceProperty<User_r2, ?>>begin().list();

        @Override
        public User_r2 createEmpty() {
            return new User_r2();
        }

        @Override
        public NumberProperty<User_r2, Long> getIdProperty() {
            return idProperty;
        }

        @Override
        public List<? extends Property<User_r2, ?>> getProperties() {
            return properties;
        }

        @Override
        public List<? extends ValueProperty<User_r2, ?>> getValueProperties() {
            return valueProperties;
        }

        @Override
        public List<? extends ReferenceProperty<User_r2, ?>> getReferenceProperties() {
            return referenceProperties;
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
        public Class<User_r2> getEntityClass() {
            return User_r2.class;
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

                    if (splitName.length > 1) {
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
        public static NumberProperty<User_r3, Long> idProperty =
                new LongPropertyImpl<User_r3>(User_r3.class, "id", "torch_id") {
                    @Override
                    public Long get(User_r3 entity) {
                        return entity.id;
                    }

                    @Override
                    public void set(User_r3 entity, Long value) {
                        entity.id = value;
                    }
                }.feature(new Id.IdFeature(true));
        public static StringProperty<User_r3> usernameProperty =
                new StringPropertyImpl<User_r3>(User_r3.class, "username", "torch_username") {
                    @Override
                    public String get(User_r3 entity) {
                        return entity.username;
                    }

                    @Override
                    public void set(User_r3 entity, String value) {
                        entity.username = value;
                    }
                };
        public static StringProperty<User_r3> firstNameProperty =
                new StringPropertyImpl<User_r3>(User_r3.class, "firstName", "torch_firstName") {
                    @Override
                    public String get(User_r3 entity) {
                        return entity.firstName;
                    }

                    @Override
                    public void set(User_r3 entity, String value) {
                        entity.firstName = value;
                    }
                };
        public static StringProperty<User_r3> lastNameProperty =
                new StringPropertyImpl<User_r3>(User_r3.class, "lastName", "torch_lastName") {
                    @Override
                    public String get(User_r3 entity) {
                        return entity.lastName;
                    }

                    @Override
                    public void set(User_r3 entity, String value) {
                        entity.lastName = value;
                    }
                };
        public static StringProperty<User_r3> emailProperty =
                new StringPropertyImpl<User_r3>(User_r3.class, "email", "torch_email") {
                    @Override
                    public String get(User_r3 entity) {
                        return entity.email;
                    }

                    @Override
                    public void set(User_r3 entity, String value) {
                        entity.email = value;
                    }
                };

        public static final List<? extends Property<User_r3, ?>> properties =
                ArrayListBuilder.<Property<User_r3, ?>>begin()
                                .add(idProperty)
                                .add(usernameProperty)
                                .add(firstNameProperty)
                                .add(lastNameProperty)
                                .add(emailProperty)
                                .list();

        public static final List<? extends ValueProperty<User_r3, ?>> valueProperties =
                ArrayListBuilder.<ValueProperty<User_r3, ?>>begin()
                                .add(idProperty)
                                .add(usernameProperty)
                                .add(firstNameProperty)
                                .add(lastNameProperty)
                                .add(emailProperty)
                                .list();

        public static final List<? extends ReferenceProperty<User_r3, ?>> referenceProperties =
                ArrayListBuilder.<ReferenceProperty<User_r3, ?>>begin().list();

        @Override
        public NumberProperty<User_r3, Long> getIdProperty() {
            return idProperty;
        }

        @Override
        public List<? extends Property<User_r3, ?>> getProperties() {
            return properties;
        }

        @Override
        public List<? extends ValueProperty<User_r3, ?>> getValueProperties() {
            return valueProperties;
        }

        @Override
        public List<? extends ReferenceProperty<User_r3, ?>> getReferenceProperties() {
            return referenceProperties;
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
        public Class<User_r3> getEntityClass() {
            return User_r3.class;
        }

        @Override
        public User_r3 createEmpty() {
            return new User_r3();
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

            assistant.addProperty(User_r4_Description.lastNameProperty);

            assistant.torch().load().type(User_r4.class).process().each(new EditFunction<User_r4>() {
                @Override
                public boolean apply(User_r4 entity) {
                    String[] splitName = entity.firstName.split(" ");

                    if (splitName.length > 1) {
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
        public static NumberProperty<User_r4, Long> idProperty =
                new LongPropertyImpl<User_r4>(User_r4.class, "id", "torch_id") {
                    @Override
                    public Long get(User_r4 entity) {
                        return entity.id;
                    }

                    @Override
                    public void set(User_r4 entity, Long value) {
                        entity.id = value;
                    }
                }.feature(new Id.IdFeature(true));
        public static StringProperty<User_r4> usernameProperty =
                new StringPropertyImpl<User_r4>(User_r4.class, "username", "torch_username") {
                    @Override
                    public String get(User_r4 entity) {
                        return entity.username;
                    }

                    @Override
                    public void set(User_r4 entity, String value) {
                        entity.username = value;
                    }
                };
        public static StringProperty<User_r4> firstNameProperty =
                new StringPropertyImpl<User_r4>(User_r4.class, "firstName", "torch_firstName") {
                    @Override
                    public String get(User_r4 entity) {
                        return entity.firstName;
                    }

                    @Override
                    public void set(User_r4 entity, String value) {
                        entity.firstName = value;
                    }
                };
        public static StringProperty<User_r4> lastNameProperty =
                new StringPropertyImpl<User_r4>(User_r4.class, "lastName", "torch_lastName") {
                    @Override
                    public String get(User_r4 entity) {
                        return entity.lastName;
                    }

                    @Override
                    public void set(User_r4 entity, String value) {
                        entity.lastName = value;
                    }
                };

        public static final List<? extends Property<User_r4, ?>> properties =
                ArrayListBuilder.<Property<User_r4, ?>>begin()
                                .add(idProperty)
                                .add(usernameProperty)
                                .add(firstNameProperty)
                                .add(lastNameProperty)
                                .list();

        public static final List<? extends ValueProperty<User_r4, ?>> valueProperties =
                ArrayListBuilder.<ValueProperty<User_r4, ?>>begin()
                                .add(idProperty)
                                .add(usernameProperty)
                                .add(firstNameProperty)
                                .add(lastNameProperty)
                                .list();

        public static final List<? extends ReferenceProperty<User_r4, ?>> referenceProperties =
                ArrayListBuilder.<ReferenceProperty<User_r4, ?>>begin().list();

        @Override
        public NumberProperty<User_r4, Long> getIdProperty() {
            return idProperty;
        }

        @Override
        public List<? extends ValueProperty<User_r4, ?>> getValueProperties() {
            return valueProperties;
        }

        @Override
        public List<? extends ReferenceProperty<User_r4, ?>> getReferenceProperties() {
            return referenceProperties;
        }

        @Override
        public List<? extends Property<User_r4, ?>> getProperties() {
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
        public Class<User_r4> getEntityClass() {
            return User_r4.class;
        }

        @Override
        public User_r4 createEmpty() {
            return new User_r4();
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
