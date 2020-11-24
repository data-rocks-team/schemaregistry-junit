# schemaregistry-junit

![GitHub](https://img.shields.io/github/license/data-rocks-team/schemaregistry-junit)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.data-rocks-team/schemaregistry-junit5.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3Aio.github.data-rocks-team%20a%3Aschemaregistry-junit5)
[![Actions Status](https://github.com/data-rocks-team/schemaregistry-junit/workflows/build/badge.svg)](https://github.com/data-rocks-team/schemaregistry-junit/actions)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fdata-rocks-team%2Fschemaregistry-junit.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fdata-rocks-team%2Fschemaregistry-junit?ref=badge_shield)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=data-rocks-team_schemaregistry-junit&metric=alert_status)](https://sonarcloud.io/dashboard?id=data-rocks-team_schemaregistry-junit)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=data-rocks-team_schemaregistry-junit&metric=coverage)](https://sonarcloud.io/dashboard?id=data-rocks-team_schemaregistry-junit)
[![Known Vulnerabilities](https://snyk.io/test/github/data-rocks-team/schemaregistry-junit/badge.svg?targetFile=schemaregistry-junit-core/build.gradle)](https://snyk.io/test/github/data-rocks-team/schemaregistry-junit?targetFile=schemaregistry-junit-core/build.gradle)
[![Known Vulnerabilities](https://snyk.io/test/github/data-rocks-team/schemaregistry-junit/badge.svg?targetFile=schemaregistry-junit4/build.gradle)](https://snyk.io/test/github/data-rocks-team/schemaregistry-junit?targetFile=schemaregistry-junit4/build.gradle)	
[![Known Vulnerabilities](https://snyk.io/test/github/data-rocks-team/schemaregistry-junit/badge.svg?targetFile=schemaregistry-junit5/build.gradle)](https://snyk.io/test/github/data-rocks-team/schemaregistry-junit?targetFile=schemaregistry-junit5/build.gradle)

Wrapping [Confluent Schema Registry](https://docs.confluent.io/current/schema-registry/), 
**schemaregistry-junit** allows developers to create and run integration tests using 
[JUnit](https://junit.org/) against a "real" 
[SchemaRegistry](https://github.com/confluentinc/schema-registry) service running within the test 
context. No need to start up an external deployment! Not even a Docker container! Everything runs 
in memory, making tests faster and shorten the feedback loop.

This project was inspired by [kafka-junit](https://github.com/salesforce/kafka-junit).

## Features
- Support for JUnit 4 and JUnit 5
- Support all SchemaRegistry version from 4.0.0 to the latest

## Installation
The easiest way is to include it in your project(s) by ways of a Maven dependency. Binary, Sources 
and Javadocs are all available from 
[Maven Central](https://search.maven.org/search?q=g:io.github.data-rocks-team).

For JUnit 5
```
<dependency>
  <groupId>io.github.data-rocks-team</groupId>
  <artifactId>schemaregistry-junit5</artifactId>
  <version>0.1.0</version>
</dependency>
```
For JUnit 4
```
<dependency>
  <groupId>io.github.data-rocks-team</groupId>
  <artifactId>schemaregistry-junit4</artifactId>
  <version>0.1.0</version>
</dependency>
```

## Regression testing
Every build is automatically tested against the latest patch version of every minor version since 4.0.0. For more 
details, check [schemaregistry-junit-regression-test](/schemaregistry-junit-test/schemaregistry-junit-regression-test).

## Examples
- [Junit 5 example](/examples/junit-5)

## Contributing
Found a bug? Think you've got an awesome feature you want to add? We welcome contributions!

## Feedback
Any questions or suggestions? Get in touch!

[![Tweet](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/FraNobilia)

## License
MIT [view license](/LICENSE)




[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fdata-rocks-team%2Fschemaregistry-junit.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Fdata-rocks-team%2Fschemaregistry-junit?ref=badge_large)