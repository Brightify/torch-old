IMPORTANT: This project is currently under heavy development and the API is not stable and might change
with upcoming versions. Once `beta` is released, the API is considered stable and shouldn't change change
(the only thing that would make the API change is some kind of critical bug).

# Brightify

NOTE: This README is currently a draft, some features might not be implemented yet, and some will likely change.

## Introduction

## Usage

### Configuration

It's recommended to set your own database name. That way you can be sure nothing will intervene with your data.
Simply define the following metadata in `<application />` tag in `AndroidManifest.xml` like this
```xml
<meta-data android:name="@string/brightify__DATABASE_NAME" android:value="sample_database" />
```
or you can use: (not recommended)
```xml
<meta-data android:name="com.brightgestures.brightify.DATABASE_NAME" android:value="sample_database" />
```

By default, `Brightify` doesn't create the database (or open if it's already created) and all its structure
when first loaded, but when first used to load or store entities. You can override this behavior by setting
following flag to true. Using this flag is not recommended, use `BrightifyService#async` instead.

NOTE: When `IMMEDIATE_DATABASE_CREATION` is enabled, calls to `BrightifyService#with` and `EntityRegistrar#register`
will be blocking.

```xml
<meta-data android:name="@string/brightify_IMMEDIATE_DATABASE_CREATION" android:value="true" />
```

For debugging purposes you might want to get each SQL query logged into logcat. To enable this behavior, just add
following line to the `<application />` tag inside of `AndroidManifest.xml`

```xml
<meta-data android:name="@string/brightify__ENABLE_QUERY_LOGGING" android:value="true" />
```


### Defining entities

### Loading entities

### Storing entities

## License

This work is licensed under the Creative Commons Attribution-NonCommercial 3.0 Unported License. To view a copy of this
license, visit http://creativecommons.org/licenses/by-nc/3.0/deed.en_US.