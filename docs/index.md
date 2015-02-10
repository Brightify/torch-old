![Torch logo][logo]

---

# Introduction

## Goals

Data persistence on Android can be tricky. In our applications, we have always used handwritten SQL commands to work with the database directly since any kind of ORM for Android seemed too difficult to setup or was too slow because of reflection. So that's why we have decided to build **Torch**. It's easy to setup and easy to use and also really fast as it doesn't use reflection, but annotation processing in the compile-time.

# Features

* **From 0 to 60 in seconds**: Adding **Torch** to your project is a piece of cake and there is not much you need to learn before you start using it.
* **User-friendly API**: We have spent many sleepless nights drafting the API to match our own high requirements to deliver an easy-to-use interface.
* **Superior performance**: Thanks to annotation processing which happens right when you compile your code, you won't waste your processing time on slow Java reflection. Nothing is dynamic, everything is precompiled.
* **Compile time safety**: Many ORM libraries let you work with fields of your [[entities|Basic entity creation]] using their names as Java strings. We believe that this is a disadvantage because you can't let the compiler help you check if you didn't refactor the field away by changing its name or removing it. With **Torch** you will reference the fields in compile-time safe manner.
* **Completely open source**: We believe that being open benefits everyone and results in having less bugs and more satisfied users.
* **Lightweight**: Torch was originally designed for Android and on phones size does matter. That's why we made extra effort to keep its size as small as possible.

# What to do next

* If you like the idea of an easy data persistence for your Android application, **[jump right in!](get_started.md)**
* **Found a bug?** You can either report it to the [issue tracker](https://github.com/brightify/torch/issues) or if you know how to fix it, send us a pull request with the fix! We will appreciate any feedback and contributions to the project!

[logo]: https://raw.githubusercontent.com/brightify/torch/develop/git_logo.png