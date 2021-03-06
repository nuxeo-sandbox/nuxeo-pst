# Nuxeo Utils: PST Import

[![Build Status](https://qa.nuxeo.org/jenkins/buildStatus/icon?job=Sandbox/sandbox_nuxeo-pst-master)](https://qa.nuxeo.org/jenkins/view/Sandbox/job/Sandbox/job/sandbox_nuxeo-pst-master/)

Import PST-compatible mailboxes into Nuxeo.

## Dependencies

The [java-libpst](https://github.com/rjohnsondev/java-libpst/) library is used to read and extract the PST content.  We are currently using the 0.9.4 release of this package.  You only have to build the dependency once for use with the `nuxeo-pst` plugin.

### Build Dependency

Initialize the submodule (points to 0.9.4 release):

```
git submodule init
git submodule update
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
> Package built here: `nuxeo-pst-importer-package/target`

> Install with `nuxeoctl mp-install <package>`

## Operation

### `Document.ImportPST`

Accepts: Documents, Document, Blobs, Blob

|Parameter|Description|Default Value|
|---------|-----------|-------------|
|xpath|Document blob XPath|file:content|
|destination|Destination import path|(null)|
|import.emptyFolders|Import empty folders|true|
|import.attachments|Import attachments|true|
|import.messages|Import messages|true|
|import.activity|Import activity|false|
|import.appointment|Import appointment|false|
|import.contact|Import contact|false|
|import.distributionList|Import Distribution List|false|
|import.rss|Import RSS feed|false|
|import.task|Import Task|false|

## Support

**These features are sand-boxed and not yet part of the Nuxeo Production platform.**

These solutions are provided for inspiration and we encourage customers to use them as code samples and learning resources.

This is a moving project (no API maintenance, no deprecation process, etc.) If any of these solutions are found to be useful for the Nuxeo Platform in general, they will be integrated directly into platform, not maintained here.

## Licensing

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

## About Nuxeo

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris.

More information is available at [www.nuxeo.com](http://www.nuxeo.com).

