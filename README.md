# schemaregistry-junit
Wrapping [Confluent Schema Registry](https://docs.confluent.io/current/schema-registry/), **schemaregistry-junit** 
allows developers to create and run integration tests using [JUnit](https://junit.org/) against a "real" 
[SchemaRegistry](https://github.com/confluentinc/schema-registry) service running within the test context. No need to 
start up an external deployment! Not even a Docker container! Everything runs in memory, making tests faster and shorten 
the feedback loop.

This project is highly inspired by [kafka-junit](https://github.com/salesforce/kafka-junit).

## Features
- Support for JUnit 4
- Support for JUnit 5

## Contributing
Found a bug? Think you've got an awesome feature you want to add? We welcome contributions!

## License
MIT [view license](/LICENSE)


