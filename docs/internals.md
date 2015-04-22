# Torch internals

## TorchService

* user facing class that handles singleton access to Torch functionality

## Actors
NOTE: Would adding `ActorFactory` help anything?

Actors are many interfaces designed to help and guide the 

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