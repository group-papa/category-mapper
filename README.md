# Category Mapper

[![Build Status](https://travis-ci.org/group-papa/category-mapper.svg)](https://travis-ci.org/group-papa/category-mapper)

This repository holds the codebase for the backend Java mapping engine and API.

## Development Environment
It is recommended to use IntelliJ IDEA to develop this project. IntelliJ has
Git, GitHub and Gradle plugins which are recommended.

You will also need Git installed on your machine. If you are not comfortable
with using Git on the command line, the GitHub app is recommended.

We are using Gradle to automate our building and testing. Gradle also handles
dependencies for us. You will want to use the following Gradle tasks:
 - `execute`: This builds the project (which also involves running tests) and
then runs it.
 - `test`: Run all tests.
 - `clean`: This cleans your working directory by deleting the project build
directory.
