# Retail Category Mapper

[![Build Status](https://travis-ci.org/group-papa/category-mapper.svg?branch=master)](https://travis-ci.org/group-papa/category-mapper)

This repository holds the codebase for the backend mapping engine, classifier
and API.

This software requires Redis to be installed.

## Development Environment
It is recommended to use IntelliJ IDEA to develop this project. IntelliJ has
Git, GitHub and Gradle plugins which are recommended.

You will also need Git installed on your machine. If you are not comfortable
with using Git on the command line, the GitHub app is recommended.

We are using Gradle to automate our building and testing. Gradle also handles
dependencies for us. You will want to use the following Gradle tasks:
 - `execute`: This compiles the project and then runs it.
 - `build`: Build the project (this also runs the tests).
 - `test`: Run all tests.
 - `clean`: This cleans your working directory by deleting the project build
directory.

## Configuration
`src/main/resources/config` contains example properties files for this
project. To change any of the configuration options, copy the relevant
example file and remove the `.example` from the name before making any changes.
