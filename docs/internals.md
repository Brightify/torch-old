# Torch internals

Torch is built around Android's SQLite, but it does not require it and can be used with other databases and platforms as well.

## TorchService

User facing class that handles singleton access to Torch functionality.

## Actor DSL
NOTE: Would adding `ActorFactory` help anything?
WARNING: There might be problem with the loader API, because each database has different capabilities. We should think about this properly before the release of first Beta.

Actors are many interfaces designed to help and guide the user through **load**, **save** and **delete** actions. The most advanced is the `Loader`.

[ ] - rename package `action` to `dsl`
[ ] - rename `Loader` to `LoadDsl` (or just `Load`?)
[ ] - rename `Saver` to `SaveDsl` (or just `Save`?)
[ ] - rename `Deleter` to `DeleteDsl` (or just `Delete`?)

### Loader

### Saver

### Deleter


## EntityManager

## DatabaseEngine
NOTE: Might be renamed to `DatabaseDriver` for better clarity.

This part provides the connection between `EntityManager` and the target database. It should have little to no logic as there will be many engines and they should not just duplicate the code.

Subparts

### Migration assistant

Implementation of the `MigrationAssistant` interface that handles the necessary migration calls from the user. This is database-dependent because each database has different possibilities.



## Database

This should be always outside Torch and should have no knowledge about Torch. The only exception to that is MockDatabase which is only used in unit tests.