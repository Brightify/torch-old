package org.brightify.torch.test.user;

import org.brightify.torch.annotation.Entity;
import org.brightify.torch.annotation.Id;

@Entity
public class Email {

    @Id
    Long id;

    String email;
}
