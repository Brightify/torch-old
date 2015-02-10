There are, for now, two already tested approaches to add **Torch** into your project. The first one using [maven](get_started.md#maven) and the second one using [gradle](get_started.md#gradle).

## Installation

### Maven

Setup is very easy in maven. You just need to add two dependencies into your pom.xml file.

```xml
...
<dependencies>
    <dependency>
        <groupId>org.brightify.torch</groupId>
        <artifactId>torch-compiler</artifactId>
        <version>1.0.-alpha.3</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.brightify.torch</groupId>
        <artifactId>torch-android</artifactId>
        <version>1.0.0-alpha.3</version>
    </dependency>
</dependencies>
...
```

That's it, it's just that simple. Now you can go on and [create your first entity](get_started.md#basic-entity-creation).

### Gradle

To setup `torch-compiler` you will need to put `android-apt` plugin into the classpath, then enable the plugin via `apply plugin: 'android-apt'` and include `apt "org.brightify.torch:torch-compiler:${torchVersion}"` in your dependencies. For **Torch** to work, you also have to add `org.brightify.torch:torch-android` into the dependencies list, this time with `compile` scope.

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        // change this to the latest version of the Android plugin
        classpath 'com.android.tools.build:gradle:0.7.+'
        // change this to the latest version of android-apt plugin
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.2'
    }
}

apply plugin: 'android'
apply plugin: 'android-apt'

def torchVersion = '1.0.0-alpha.3'

dependencies {
    apt "org.brightify.torch:torch-compiler:${torchVersion}"
    compile "org.brightify.torch:torch-android:${torchVersion}"
}

android {
    compileSdkVersion 21
    buildToolsVersion "21.1.1"

    defaultConfig {
        minSdkVersion 14
        targetSdkVersion 21
        versionCode 1
        versionName "1.0"
    }
}

```

Now you can go on and [create your first entity](get_started.md#basic-entity-creation).

## Basic entity creation

**Torch** works with so called entities. These are basically just POJOs with @Entity annotation and at least one **Long** field with @Id annotation. Let's create our own example entity which will hold information about our users (username, name, email and password hash).

```java
@Entity
public class User {
    @Id
    Long id;
    String username;
    String name;
    String email;
    String passwordHash;
}
```

It's that easy. Nothing more is required. If you feel like you need more options, head to [advanced entity creation](Advanced entity creation).

IMPORTANT: All fields has to be `public`, `protected` or default (no accessibility keyword). Otherwise you have to use [accessors](Property accessors).

## Entity registration

NOTE: This section is work in progress.

Each entity has to be registered before **Torch** will be able to work with it. What you need to do is to register so called `EntityDescription` for the specified entity. There are currently two ways to register them.

### Precompiled description
If you use **Torch compiler**, it will create descriptions for all your entities at compile time. The name of the generated class is the same as the entity's, but with a dollar sign `$` at the end. You just need to register them like this:

```java
Context context = <android Application or Activity>;

TorchAndroid.with(context).register(User$.create());
```

Or if you want this to happen in background thread, you can use:

IMPORTANT: This is currently not the recommended approach, because you might end up by running any **Torch** action before the factory is loaded. It is on our list to be fixed 

```java
Callback<Void> callback = <create own implementation of callback which will be invoked after init>;
DatabaseEngine databaseEngine = <create an AndroidSQLiteEngine>;
TorchService.asyncInit(callback).with(databaseEngine).register(User$.create()).submit();
```

Pass in your own instance of the callback to get notified about the result.

NOTE: You can chain more `register` calls before you call submit. This will automatically register all the entities and then calls the `Callback#onSuccess` method.

### Hand-written description
Of course you can write your own description (for example if you want to store a class you can't or don't want to change into an entity). Simply implement `EntityDescription<ENTITY>` interface, where `ENTITY` is the entity you're writing the description for. Then register an instance as any other metadata:

```java
Context context = <android Application or Activity>;
EntityDescription<User> description = ...;
TorchService.with(context).register(metadata);
```

Example of a hand-written entity description is [SQLiteMaster$](https://github.com/brightify/torch/blob/develop/torch-android/src/main/java/org/brightify/torch/android/internal/SQLiteMaster$.java) which is a description for SQLite's internal entity [SQLiteMaster](https://github.com/brightify/torch/blob/develop/torch-android/src/main/java/org/brightify/torch/android/internal/SQLiteMaster.java).

### Runtime description
NOTE: Not yet implemented.

This method is recommended for rapid prototype development as it doesn't require any specific setup, but it is not recommended for production environment, because it relies on reflection which is very expensive. Basically you just register `TorchReflector` like this:

```java
Context context = <android Application or Activity>;
TorchService.with(context).register(TorchReflector.forEntity(User.class));
```
