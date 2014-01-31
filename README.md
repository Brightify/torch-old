IMPORTANT: This project is currently under heavy development and the API is not stable and might change
with upcoming versions. Once `beta` is released, the API is considered stable and shouldn't change change unless some
critical bug is found, which fixing would mean changing the API.

# Brightify [![Build Status](https://travis-ci.org/brightify/torch.png?branch=develop)](https://travis-ci.org/brightify/torch)

NOTE: This README is currently a draft, some features might not be implemented yet, and some will likely change.

## Introduction

## Usage

### Configuration

It's recommended to set your own database name. That way you can be sure nothing will intervene with your data.
Simply define the following metadata in `<application />` tag in `AndroidManifest.xml` like this
```xml
<meta-data android:name="@string/brightify__DATABASE_NAME" android:value="sample_database" />
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

TBD

### Loading entities

TBD

### Storing entities

TBD