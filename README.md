# schemaregistry-junit

![GitHub](https://img.shields.io/github/license/data-rocks-team/schemaregistry-junit)
[![Actions Status](https://github.com/data-rocks-team/schemaregistry-junit/workflows/build/badge.svg)](https://github.com/data-rocks-team/schemaregistry-junit/actions)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fdata-rocks-team%2Fschemaregistry-junit.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fdata-rocks-team%2Fschemaregistry-junit?ref=badge_shield)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=data-rocks-team_schemaregistry-junit&metric=alert_status)](https://sonarcloud.io/dashboard?id=data-rocks-team_schemaregistry-junit)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=data-rocks-team_schemaregistry-junit&metric=coverage)](https://sonarcloud.io/dashboard?id=data-rocks-team_schemaregistry-junit)

| Library | Version | Documentation | Vulnerability |
| ----------- | ----------- | ----------- | ----------- |
| [schemaregistry-junit5](https://mvnrepository.com/artifact/io.github.data-rocks-team/schemaregistry-junit5) | [![Maven Central](https://img.shields.io/maven-central/v/io.github.data-rocks-team/schemaregistry-junit5.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3Aio.github.data-rocks-team%20a%3Aschemaregistry-junit5) | [![Javadocs](http://www.javadoc.io/badge/io.github.data-rocks-team/schemaregistry-junit5.svg)](http://www.javadoc.io/doc/io.github.data-rocks-team/schemaregistry-junit5) | [![Known Vulnerabilities](https://snyk.io/test/github/data-rocks-team/schemaregistry-junit/badge.svg?targetFile=schemaregistry-junit5/build.gradle)](https://snyk.io/test/github/data-rocks-team/schemaregistry-junit?targetFile=schemaregistry-junit5/build.gradle)
| [schemaregistry-junit4](https://mvnrepository.com/artifact/io.github.data-rocks-team/schemaregistry-junit4) | [![Maven Central](https://img.shields.io/maven-central/v/io.github.data-rocks-team/schemaregistry-junit4.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3Aio.github.data-rocks-team%20a%3Aschemaregistry-junit4) | [![Javadocs](http://www.javadoc.io/badge/io.github.data-rocks-team/schemaregistry-junit4.svg)](http://www.javadoc.io/doc/io.github.data-rocks-team/schemaregistry-junit4) | [![Known Vulnerabilities](https://snyk.io/test/github/data-rocks-team/schemaregistry-junit/badge.svg?targetFile=schemaregistry-junit4/build.gradle)](https://snyk.io/test/github/data-rocks-team/schemaregistry-junit?targetFile=schemaregistry-junit4/build.gradle)
| [schemaregistry-junit-core](https://mvnrepository.com/artifact/io.github.data-rocks-team/schemaregistry-junit-core) | [![Maven Central](https://img.shields.io/maven-central/v/io.github.data-rocks-team/schemaregistry-junit-core.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3Aio.github.data-rocks-team%20a%3Aschemaregistry-junit-core) | [![Javadocs](http://www.javadoc.io/badge/io.github.data-rocks-team/schemaregistry-junit-core.svg)](http://www.javadoc.io/doc/io.github.data-rocks-team/schemaregistry-junit-core) | [![Known Vulnerabilities](https://snyk.io/test/github/data-rocks-team/schemaregistry-junit/badge.svg?targetFile=schemaregistry-junit-core/build.gradle)](https://snyk.io/test/github/data-rocks-team/schemaregistry-junit?targetFile=schemaregistry-junit-core/build.gradle) |

Are you tired of waiting for [Docker](https://www.docker.com/) containers to fire up while testing 
your Kafka solution? Or are you tired of waiting for an ephemeral environment to start? Are you 
looking for a solution to reduce the time you are loosing waiting for 
[Apache Kafka Cluster](https://kafka.apache.org/) and
[Confluent Schema Registry](https://docs.confluent.io/current/schema-registry/) to start while 
testing?
*schemaregistry-junit* is the answer! By pairing *schemaregistry-junit* together with 
[kafka-junit](https://github.com/salesforce/kafka-junit), you can speed up your tests and shorten 
the feedback loop by reducing the time wasted waiting for Docker containers or ephemeral 
environments to start.
*schemaregistry-junit* lets you run a fully working 
[Confluent Schema Registry](https://docs.confluent.io/current/schema-registry/) in-memory. The 
server lifecycle is fully automated via [JUnit](https://junit.org/junit5/) extensions.
A fluent DSL is provided to configure the Confluent Schema Registry.

This project was inspired by [kafka-junit](https://github.com/salesforce/kafka-junit).

## Features
- Support for JUnit 4 and JUnit 5
- Support all [Confluent SchemaRegistry](https://mvnrepository.com/artifact/io.confluent/kafka-schema-registry?repo=confluent-packages) 
version from 4.0.0 to the latest

## Installation
The easiest way to include `schemaregistry-junit` in your project(s) is via Maven dependency. Binary, Sources 
and Javadocs are all available in 
[Maven Central](https://search.maven.org/search?q=g:io.github.data-rocks-team).

For JUnit 5
<details>
  <summary>Maven</summary>
  
  #### Example POM
```xml
<!-- Declare schemaregistry-junit5 dependency -->
<dependency>
  <groupId>io.github.data-rocks-team</groupId>
  <artifactId>schemaregistry-junit5</artifactId>
  <version>0.1.1</version>
</dependency>

<!-- Include Confluent Schema-Registry -->
<dependency>
  <groupId>io.confluent</groupId>
  <artifactId>kafka-schema-registry</artifactId>
  <version>X.X.X</version>
</dependency>
```
</details>

<details>
  <summary>Gradle</summary>
  
  #### Example build.gradle
```groovy
testImplementation 'io.github.data-rocks-team:schemaregistry-junit5:0.1.1'
testImplementation 'io.confluent:kafka-schema-registry:X.X.X'
```
</details>

For JUnit 4
<details>
  <summary>Maven</summary>
  
  #### Example POM
```xml
<!-- Declare schemaregistry-junit5 dependency -->
<dependency>
  <groupId>io.github.data-rocks-team</groupId>
  <artifactId>schemaregistry-junit4</artifactId>
  <version>0.1.1</version>
</dependency>

<!-- Include Confluent Schema-Registry -->
<dependency>
  <groupId>io.confluent</groupId>
  <artifactId>kafka-schema-registry</artifactId>
  <version>X.X.X</version>
</dependency>
```
</details>

<details>
  <summary>Gradle</summary>
  
  #### Example build.gradle
```groovy
testImplementation 'io.github.data-rocks-team:schemaregistry-junit4:0.1.1'
testImplementation 'io.confluent:kafka-schema-registry:X.X.X'
```
</details>

## How it works
Given a class using `SharedSchemaRegistryTestResource`, before all tests start, 
[JUnit](https://junit.org/junit5/) calls the start function. This function replicates the behaviour 
of [SchemaRegistryMain.java](https://github.com/confluentinc/schema-registry/blob/master/core/src/main/java/io/confluent/kafka/schemaregistry/rest/SchemaRegistryMain.java), 
which first validates the settings and then starts the server. When all tests have been executed, 
independently of the test results, JUnit calls the shutdown function, which will shut the server 
down.
Logs generated by the server are redirected to the terminal.

## Regression testing
Every build is automatically tested against the latest patch version of every minor version since 
4.0.0. For more details, check 
[schemaregistry-junit-regression-test](/schemaregistry-junit-test/schemaregistry-junit-regression-test).

## Examples
- [JUnit 5 example](/examples/junit-5)
- [JUnit 4 example](/examples/junit-4)

Before compiling any example, run `./gradlew setDependenciesForExample` in the main project. This 
gradle task will ensure that the example project will use the same dependency versions used in the
main project. To ensure that `setDependenciesForExample` works as expected, dependencies 
version should be defined in the `ext {}` section in the [main build.gradle](build.gradle) and the
example project should define versions as `nameOfTheDependencyVersion = PLACEHOLDER` (an example 
can be found [here](examples/junit-5/build.gradle)). 

## Contributing
Found a bug? Think you've got an awesome feature you want to add? We welcome contributions!

## Feedback
Any questions or suggestions? Get in touch!

[![Tweet](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/FraNobilia)

## License
MIT [view license](/LICENSE)

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fdata-rocks-team%2Fschemaregistry-junit.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Fdata-rocks-team%2Fschemaregistry-junit?ref=badge_large)
