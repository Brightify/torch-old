package com.brightgestures.brightify.demo.todo.model;

import com.brightgestures.brightify.annotation.Entity;
import com.brightgestures.brightify.annotation.Id;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
@Entity
public class User {

    private Long id;
    private String username;
    private String passwordHash;

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
