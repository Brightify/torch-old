# Save planning

Torch should not save the changes in entities immediately.

```
User user = new User();
user.username = "John";
torch().save().entity(user);

// < Saved one entity with ID 1
assert user.getId == 1

user.username = "Doe";
torch().save().entity(user);

// < In the engine, user with ID 1 still has "John", not "Doe"

... some code ...

// < The previous save was added to a background queue where an executor goes through all the queued writes and execute them one by one.


```

