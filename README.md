# Nuxeo Utils: Log4j

[![Build Status](https://qa.nuxeo.org/jenkins/buildStatus/icon?job=Sandbox/sandbox_nuxeo-log4j-utils-master)](https://qa.nuxeo.org/jenkins/view/Sandbox/job/Sandbox/job/sandbox_nuxeo-log4j-utils-master/)

Operation to change log level of specified categories at runtime.  By default, changes the CONSOLE appender for logging.

## Dependencies

The [java-libpst](https://github.com/rjohnsondev/java-libpst/) library is used to read and extract the PST content.  We are currently using the 0.9.4 release of this package.  You only have to build the dependency once for use with the `nuxeo-pst` plugin.

### Build Dependency

Initialize the submodule (points to 0.9.4 release):

```
git submodule init
```

Use `maven` to install the jar:

```
mvn -f java-libpst/pom.xml -Dmaven.javadoc.skip=true -Dgpg.skip=true clean install
```

## Build and Install

Build with maven (at least 3.3)

```
mvn clean install
```
> Package built here: `nuxeo-log4j-util-package/target`

> Install with `nuxeoctl mp-install <package>`

## Operation

Name: `Services.LogLevel`

Parameters:
- categories: comma-separated list of log categories to manipulate
- debug: (true) or false, enable or disable debug logging
- children: true or (false), change children of parent categories

> Changes CONSOLE appender

## Support

**These features are sand-boxed and not yet part of the Nuxeo Production platform.**

These solutions are provided for inspiration and we encourage customers to use them as code samples and learning resources.

This is a moving project (no API maintenance, no deprecation process, etc.) If any of these solutions are found to be useful for the Nuxeo Platform in general, they will be integrated directly into platform, not maintained here.

## Licensing

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

## About Nuxeo

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris.

More information is available at [www.nuxeo.com](http://www.nuxeo.com).

