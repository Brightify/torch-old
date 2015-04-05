package org.brightify.torch.test.user;

import org.brightify.torch.Ref;
import org.brightify.torch.RefCollection;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;
import org.brightify.torch.annotation.Load;

import java.util.List;

@Entity
public class UserDetails {

    @Id
    Long customIdName;

    String something;

    Ref<User> mother;

    @Load
    Ref<User> father;

    RefCollection<User> friends;

    List<String> nicknames;
}
