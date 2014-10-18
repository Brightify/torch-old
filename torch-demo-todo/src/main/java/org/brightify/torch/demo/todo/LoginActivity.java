package org.brightify.torch.demo.todo;

import android.app.Activity;
import android.widget.EditText;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import org.brightify.torch.demo.todo.model.User;
import org.brightify.torch.demo.todo.model.User$;

import static org.brightify.torch.TorchService.torch;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
@EActivity(R.layout.login_activity)
public class LoginActivity extends Activity {

    @ViewById(R.id.username)
    EditText username;

    @ViewById(R.id.password)
    EditText password;

    @Bean
    AuthManager authManager;

    @Click(R.id.login)
    void login() {
        username.setError(null);
        password.setError(null);

        User user = torch().load().type(User.class).filter(User$.username.equalTo(username.getText().toString())).single();
        if(user == null) {
            username.setError("Username invalid!");
            return;
        }
        // FIXME make use of SHA1 hash
        if(!user.getPasswordHash().equals(password.getText().toString())) {
            password.setError("Password invalid!");
            return;
        }

        authManager.setLoggedUser(user);

        startMainActivity();
    }

    @Click(R.id.register)
    void register() {
        username.setError(null);
        password.setError(null);

        User user = torch().load().type(User.class).filter(User$.username.equalTo(username.getText().toString())).single();
        if(user != null) {
            username.setError("Username exists!");
            return;
        }

        user = new User();
        user.setUsername(username.getText().toString());
        user.setPasswordHash(password.getText().toString());

        // TODO make async
        torch().save().entity(user);

        authManager.setLoggedUser(user);

        startMainActivity();
    }

    private void startMainActivity() {
        MainActivity_.intent(this).start();
    }

}
