# schemaregistry-junit

![GitHub](https://img.shields.io/github/license/data-rocks-team/schemaregistry-junit)
[![Actions Status](https://github.com/data-rocks-team/schemaregistry-junit/workflows/build/badge.svg)](https://github.com/data-rocks-team/schemaregistry-junit/actions)

Wrapping [Confluent Schema Registry](https://docs.confluent.io/current/schema-registry/), **schemaregistry-junit** 
allows developers to create and run integration tests using [JUnit](https://junit.org/) against a "real" 
[SchemaRegistry](https://github.com/confluentinc/schema-registry) service running within the test context. No need to 
start up an external deployment! Not even a Docker container! Everything runs in memory, making tests faster and shorten 
the feedback loop.

This project is highly inspired by [kafka-junit](https://github.com/salesforce/kafka-junit).

## Features
- Support for JUnit 4 and JUnit 5
- Support all SchemaRegistry version from 4.0.0 to the latest

## Regression testing
Every build is automatically tested against the latest patch version of every minor version since 4.0.0. For more 
details, check [schemaregistry-junit-regression-test](/schemaregistry-junit-test/schemaregistry-junit-regression-test).

## Contributing
Found a bug? Think you've got an awesome feature you want to add? We welcome contributions!

## Feedback
Any questions or suggestions? Get in touch!

[![Tweet](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/FraNobilia)

## License
MIT [view license](/LICENSE)


