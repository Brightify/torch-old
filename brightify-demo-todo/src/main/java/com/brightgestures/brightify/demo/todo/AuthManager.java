package com.brightgestures.brightify.demo.todo;

import com.brightgestures.brightify.BrightifyService;
import com.brightgestures.brightify.EntityMetadata;
import com.brightgestures.brightify.Key;
import com.brightgestures.brightify.demo.todo.model.User;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.api.Scope;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
@EBean(scope = Scope.Singleton)
public class AuthManager {

    Key<User> loggedUser;

    public Key<User> getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(Key<User> user) {
        loggedUser = user;
    }

    public void setLoggedUser(User user) {
        EntityMetadata<User> metadata = BrightifyService.factory().getEntities().getMetadata(User.class);
        loggedUser = metadata.createKey(user);
    }
}
