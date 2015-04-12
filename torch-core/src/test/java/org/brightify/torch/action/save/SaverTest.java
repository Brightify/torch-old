package org.brightify.torch.action.save;

import org.brightify.torch.TorchConfiguration;
import org.brightify.torch.TorchFactory;
import org.brightify.torch.TorchFactoryImpl;
import org.brightify.torch.test.MockDatabaseEngine;
import org.brightify.torch.test.TestObject;
import org.junit.Test;

import static org.brightify.torch.test.TestUtils.createTestObject;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class SaverTest {

    private MockDatabaseEngine engine;

    @Test
    public void testUTF8() {
        TorchConfiguration<?> configuration = new TorchFactoryImpl.BasicConfiguration(new MockDatabaseEngine());
        configuration.register(TestObject.class);
        TorchFactory torchFactory = configuration.initializeFactory();

        TestObject object = createTestObject();
        object.stringField = "AwesomeAppěščřžýáíéňľ";

        Long id = torchFactory.begin().save().entity(object);

        assertThat(id, is(not(nullValue())));

        TestObject loadObject = torchFactory.begin().load().type(TestObject.class).id(id);

        assertThat(loadObject, is(object));

        object.stringField = "AwesomeAppவான்வழிe";

        Long newId = torchFactory.begin().save().entity(object);

        assertThat(newId, is(id));

        loadObject = torchFactory.begin().load().type(TestObject.class).id(newId);

        assertThat(loadObject, is(object));

        torchFactory.unload();

    }

}