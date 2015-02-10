The **Torch compiler** generates the entity metadata based on used annotations. Here is the list of annotations you can use and the description of each annotation's usage.

## Class

* [@Entity](annotations.md#entity)
* [@Index](annotations.md#index)

## Fields and methods - entity properties

If you use accessor methods (and you should), then be sure to apply the annotations to the *getter*. Any annotation (except the **@Accessor**) you put on the *setter* will be ignored and **Torch compiler** will warn you about it.

* [@Accessor](annotations.md#accessor)
* [@Id](annotations.md#id)
* [@Ignore](annotations.md#ignore)
* [@Index](annotations.md#index)
* [@Load](annotations.md#load)
* [@NotNull](annotations.md#notnull)
* [@Unindex](annotations.md#unindex)
* [@Unique](annotations.md#unique)

## Static methods

* [@Migration](annotations#migration_1)

# Annotation usage

### @Entity
The main annotation you need to annotate any class you want to use as [[Torch entity|Basic-entity-creation#what is entity]].

#### Parameters

##### name (not implemented yet)
###### default: Torch-generated table name
Override the default name of the backing table for this entity. We recommend you not to do this and let **Torch** handle the internal naming for you.

##### version
###### default: `1.0.0`
Each time you make changes to the entity, you should increase its version. That way you can use built-in [[table migration]] support, so that your users won't lose any data.

##### migration
###### default: `MigrationType.MIGRATE`
You can select the way how the migration will be done. Each migration type is explained in the JavaDoc.

##### delete (not implemented yet)
###### default: `false`
If you want to delete an entity from your users' database, you need to change this parameter to `true`. This way you can remove anything inside the entity class, but remember that **Torch** need this class reference so that it knows it should delete all the entity data.

##### useSimpleName (not tested yet)
###### default: `false`
In case you want **Torch** to use only the class simple name as a table name, change this parameter to `true`. We don't recommend it though as name clashes might occur.

##### ignore (not tested yet)
###### default: `false`
If set to `true`, **Torch compiler** will simply ignore this class as if it was not annotated with **@Entity** at all.

### @Index
**IMPORTANT**: Indexing is not settled yet and therefore SQLite probably only supports one index at a time (other indexes are ignored) in queries, so we are trying to figure out at the moment the best approach to indexing in **Torch**. This means that the behavior of **@Index** is likely to change.

There are two possible usages of this annotation. If you annotate an entity class with it, every property will be indexed, except those you mark with [[@Unindex|Annotations#@unindex] annotation.

The other usage is to put it in front of a field. This will tell **Torch** to index this property. Then you can use it for multiple fields and all you mark with the annotation will be indexed.

### @Accessor
**Torch** has [[rules for naming|Rules]] the get/set methods for properties. Any method beginning with `set` is considered *setters* and methods beginning with `is` or `get` are considered *getters*. The rest of the method's name is then used as a property name. For example if you have a pair of accessor methods `getUserName` and `setUserName`, the name of the property would then be `userName`. Remember that you need both getters and setters, otherwise **Torch** entity compilation will fail. 

#### Parameters
##### name
###### default: `""`
In case you want to override the name of the property, just set this parameter to anything you would like. Just be sure it's unique in the entity and that **Torch** can find exactly one *getter* and one *setter* for this property.

##### type
###### default: `Type.INFERRED`
You can force **Torch** to handle the method as either *getter* or *setter* by changing the type. This is useful when you don't want it to follow the `setX`/`getX` naming rule.

### @Id
Each entity must have at least one property, with type `Long` and annotated with the **@Id**, otherwise the **Torch** entity compilation will fail.

#### Parameters
##### autoIncrement
###### default: `true`
If you set this parameter to `false`, you will have to make sure you have set the ID yourself and if you'd like to add a new entity it will be unique.

### @Ignore
Use this annotation to tell **Torch** that the annotated field/method is not a property.

### @Load
**IMPORTANT**: This is not yet implemented! It will be a part of an upcoming release of the alpha 2.
**FIXME**: Incomplete documentation!

This annotation is meant to be used as a way to tell **Torch** when to load a reference to an entity, collection of entities or collection of basic types eagerly.

#### Parameters
##### value
###### default: an empty array
Define load group classes array. In case any of the load group is set during the load from **Torch**, the property's value will be loaded eagerly. Otherwise only a live reference will be present, waiting to be loaded later by your code.

##### unless
###### default: an empty array
When you use the *unless*, **Torch** will load the property's value eagerly if none of the load groups, set by this parameter, are set during the load from **Torch**.

### @NotNull
In case you want to be sure that you will never save an entity with a null value of this property into **Torch**, you can mark it with this annotation. This is however a run-time check and therefore will result in an exception thrown by the save action.

### @Unindex
**IMPORTANT**: Indexing is not settled yet and therefore SQLite probably only supports one index at a time (other indexes are ignored) in queries, so we are trying to figure out at the moment the best approach to indexing in **Torch**. This means that the behavior of **@Unindex** is likely to change, or it might even be removed.

Marking a property with this annotation will tell **Torch** to never index it.

### @Unique

Properties marked with this annotation will have to be unique among all the stored entities. If you try to violate this rule, an exception will be thrown by the save action.

### @Migration
