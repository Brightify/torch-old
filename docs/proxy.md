# Proxy

```
interface EntityProxy {

}

@Entity
class User {
   
    private Long id;
    private String username;

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

}

class User$$$Proxy extends User {
    
    private boolean idLoaded;
    private boolean usernameLoaded;
    
    @Override
    public Long getId() {
        if(!idLoaded) {
            load(User$.id);
            idLoaded = true;
        }
    
        return id;
    }
    
    @Override
    public void setId(Long id) {
        this.id = id;
        save(User$.id);
    }
    
    @Override
    public String getUsername() {
        if(!usernameLoaded) {
            load(User$.username);
            usernameLoaded = true;
        }
        return username;
    }
    
    @Override
    public void setUsername(String username) {
        this.username = username;
        save(User$.username);
    }
}

```