package com.datarocks.schemaregistry.test;

import com.salesforce.kafka.test.junit5.SharedKafkaTestResource;
import io.confluent.kafka.schemaregistry.json.JsonSchema;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.Duration;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.awaitility.Awaitility.await;

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
        .schemaRegistryClient().register(TOPIC + "-value", new JsonSchema(SCHEMA)))
        .doesNotThrowAnyException();

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
        .schemaRegistryClient().register(TOPIC + "-value", new JsonSchema(SCHEMA)))
        .doesNotThrowAnyException();

    assertThat(schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient().getAllSubjects())
        .hasSize(1);

    assertThatCode(schemaRegistry::shutdown)
        .doesNotThrowAnyException();

    assertThatCode(() -> kafka.getKafkaTestUtils()
        .getAdminClient()
        .deleteTopics(Collections.singletonList("_schemas")).all().get())
        .doesNotThrowAnyException();

    waitForKafkaToPropagateTopicDeletion();

    assertThatCode(schemaRegistry::start)
        .doesNotThrowAnyException();

    assertThat(schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient().getAllSubjects())
        .isEmpty();

    assertThatCode(schemaRegistry::shutdown)
        .doesNotThrowAnyException();
  }

  private void waitForKafkaToPropagateTopicDeletion() {
    // Kafka is not designed to delete a topic and recreate it a few milliseconds after.
    // Such behaviour causes inconsistency: AdminClient method to list all topics does not
    // return the just deleted topic, while the method to create the topic reports to be already
    // present.
    // A sleep is required to between deleting the __schema topic and restarting the SchemaRegistry
    // to prevent the above described inconsistency.
    // The same behaviour could be achieved with a Thread.sleep(2000) but SonarCloud correctly
    // reports it as a code smell.
    long start = System.currentTimeMillis();

    await()
        .atLeast(Duration.ofSeconds(2))
        .until(() -> System.currentTimeMillis() - start >= 2000);
  }
}
