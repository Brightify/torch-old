```
interface RefCollection<T> extends Collection<T>;

interface RefList<T> extends RefCollection<T>, List<T>;

interface RefSet<T> extends RefCollection<T>, Set<T>;

interface Ref<T>;
```


```java
class ProxyList<T> extends List<T> {
    
    final Class<T> backingClass;
    Map<Long, T> cache = new HashMap<Long, T>();

    public ProxyList(Class<T> backingClass, List<Long> ids) {
        this.backingClass = backingClass;
        for(Long id : ids) {
            cache.put(id, null);
        }
    }

    public T get(int i) {
        Long id = cache.keySet().get(i);
        T cachedObject = cache.get(id);
        if(cachedObject == null) {
            cachedObject = torch().load().type(backingClass).id(id);
            cache.put(id, cachedObject);
        }
        return cachedObject;
    }
}
```

## Entity classes

```java
@Entity
class User {
    
    @Id
    Long id;

    String name;

    UserDetails details;

    @Load
    List<Email> emails;

}

@Entity
class UserDetails {
    @Id
    Long customIdName;

    String something;
    
    User mother;

    @Load    
    User father;
    
    List<User> friends;

    List<String> nicknames;
}

@Entity
class Email {
    @Id
    Long id;

    String email;
}

```

// When concrete implementation of List<?> is used, @Load has to be present, otherwise fail in compile time

## Table representations


### User

Column type | Column name
------------|-------------
Long        | id
String      | name
Long        | UserDetails_details_customIdName
  
  
### User__to__Email_BindTable

Column type | Column name
------------|-------------
Long        | id
Long        | User_id
Long        | Email_id
  
  
### UserDetails

Column type | Column name
------------|-------------
Long        | customIdName
String      | something
Long        | User_mother_id
Long        | User_father_id
  
  
### UserDetails__nicknames

Column type | Column name
------------|-------------
Long        | id
Long        | User_id
String      | value
  
  
### UserDetails__to__User

Column type | Column name
------------|-------------
Long        | id
Long        | UserDetails_customIdName
Long        | User_id
  
  
### Email

Column type | Column name
------------|-------------
Long        | id
String      | email
  
  
## Table creation SQL

```sql
CREATE TABLE User (
    id INTEGER,
    name TEXT,
    UserDetails_details_customIdName INTEGER
);

CREATE TABLE User__to__Email_BindTable (
    User_id INTEGER,
    Email_id INTEGER
);

CREATE TABLE UserDetails (
    customIdName INTEGER,
    something String,
);

CREATE TABLE User__test (
    id INTEGER,
    User_id INTEGER,
    test TEXT
);

```





```java
if(@Load is present) {
    SELECT User.*, Email.* FROM User WHERE
} else {
    
}
```