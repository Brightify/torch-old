package org.brightify.torch.test.user;

import org.brightify.torch.Ref;
import org.brightify.torch.RefCollection;
import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;
import org.brightify.torch.annotation.Load;

import java.util.List;

@Entity
public class User {

    @Id
    Long id;

    String name;

    Ref<UserDetails> details;

    @Load
    RefCollection<Email> emails;

}

