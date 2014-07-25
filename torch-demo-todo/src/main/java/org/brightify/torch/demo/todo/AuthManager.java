package org.brightify.torch.demo.todo;

import org.brightify.torch.TorchService;
import org.brightify.torch.EntityDescription;
import org.brightify.torch.Key;
import org.brightify.torch.demo.todo.model.User;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.api.Scope;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
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
        EntityDescription<User> metadata = TorchService.factory().getEntities().getDescription(User.class);
        loggedUser = metadata.createKey(user);
    }
}
