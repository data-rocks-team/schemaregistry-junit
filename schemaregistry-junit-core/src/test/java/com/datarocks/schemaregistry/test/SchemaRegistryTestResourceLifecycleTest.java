package com.datarocks.schemaregistry.test;

import com.salesforce.kafka.test.junit5.SharedKafkaTestResource;
import io.confluent.kafka.schemaregistry.json.JsonSchema;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class SchemaRegistryTestResourceLifecycleTest {

    @RegisterExtension
    public static final SharedKafkaTestResource kafka = new SharedKafkaTestResource()
            .withBrokers(1);

    private static final String TOPIC = "test-scenario-string-json";

    private static final String SCHEMA = "{"
            + "\"$schema\":\"http://json-schema.org/draft-07/schema#\","
            + "\"title\":\"User\","
            + "\"type\":\"object\","
            + "\"additionalProperties\":false,"
            + "\"properties\":{"
            + "\"firstName\":{\"oneOf\":[{\"type\":\"null\",\"title\":\"Not included\"},"
            + "{\"type\":\"string\"}]},"
            + "\"lastName\":{\"oneOf\":[{\"type\":\"null\",\"title\":\"Not included\"},"
            + "{\"type\":\"string\"}]}}}";

    @Test
    @SneakyThrows
    void shouldBeAbleToAccessSchemasAfterSchemaRegistryTestResourceRestart() {
        SchemaRegistryTestResource<?> schemaRegistry = new SchemaRegistryTestResource<>()
                .withBootstrapServers(kafka.getKafkaConnectString())
                .withRandomPort();

        assertThatCode(schemaRegistry::start)
                .doesNotThrowAnyException();

        assertThatCode(() -> schemaRegistry
                .schemaRegistryTestUtils()
                .schemaRegistryClient().register(TOPIC + "-value", new JsonSchema(SCHEMA)));

        assertThat(schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient().getAllSubjects())
                .hasSize(1);

        assertThatCode(schemaRegistry::shutdown)
                .doesNotThrowAnyException();

        assertThatCode(schemaRegistry::start)
                .doesNotThrowAnyException();

        assertThat(schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient().getAllSubjects())
                .hasSize(1);

        assertThatCode(schemaRegistry::shutdown)
                .doesNotThrowAnyException();
    }

    @Test
    @SneakyThrows
    void shouldNotBeAbleToAccessSchemasOnEmptySchemasTopic() {
        SchemaRegistryTestResource<?> schemaRegistry = new SchemaRegistryTestResource<>()
                .withBootstrapServers(kafka.getKafkaConnectString())
                .withRandomPort();

        assertThatCode(schemaRegistry::start)
                .doesNotThrowAnyException();

        assertThatCode(() -> schemaRegistry
                .schemaRegistryTestUtils()
                .schemaRegistryClient().register(TOPIC + "-value", new JsonSchema(SCHEMA)));

        assertThat(schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient().getAllSubjects())
                .hasSize(1);

        assertThatCode(schemaRegistry::shutdown)
                .doesNotThrowAnyException();

        assertThatCode(() -> kafka.getKafkaTestUtils().getAdminClient().deleteTopics(List.of("_schemas")))
                .doesNotThrowAnyException();

        SchemaRegistryTestResource<?> schemaRegistry2 = new SchemaRegistryTestResource<>()
                .withBootstrapServers(kafka.getKafkaConnectString())
                .withRandomPort();

        assertThatCode(schemaRegistry::start)
                .doesNotThrowAnyException();

        assertThat(schemaRegistry2.schemaRegistryTestUtils().schemaRegistryClient().getAllSubjects())
                .hasSize(1);

        assertThatCode(schemaRegistry2::shutdown)
                .doesNotThrowAnyException();
    }

    @Test
    @SneakyThrows
    void shouldNotBeAbleToAccessSchemasOnEmptySchemasTopicWithSeperateKafkaInstances() {
        SharedKafkaTestResource kafka2 = new SharedKafkaTestResource()
                .withBrokers(1);
        kafka2.beforeAll(null); // Required as we need an ExecutionContext we don't have access to

        SchemaRegistryTestResource<?> schemaRegistry = new SchemaRegistryTestResource<>()
                .withBootstrapServers(kafka.getKafkaConnectString())
                .withRandomPort();

        assertThatCode(schemaRegistry::start)
                .doesNotThrowAnyException();

        assertThatCode(() -> schemaRegistry
                .schemaRegistryTestUtils()
                .schemaRegistryClient().register(TOPIC + "-value", new JsonSchema(SCHEMA)));

        assertThat(schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient().getAllSubjects())
                .hasSize(1);

        assertThatCode(schemaRegistry::shutdown)
                .doesNotThrowAnyException();

        SchemaRegistryTestResource<?> schemaRegistry2 = new SchemaRegistryTestResource<>()
                .withBootstrapServers(kafka2.getKafkaConnectString())
                .withRandomPort();

        assertThatCode(schemaRegistry2::start)
                .doesNotThrowAnyException();

        assertThat(schemaRegistry2.schemaRegistryTestUtils().schemaRegistryClient().getAllSubjects())
                .hasSize(0);

        assertThatCode(schemaRegistry2::shutdown)
                .doesNotThrowAnyException();
    }
}
