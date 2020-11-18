package com.datarocks.schemaregistry.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.entities.Schema;
import io.confluent.kafka.serializers.KafkaJsonDeserializerConfig;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class TestScenarioStringJson
    extends AbstractTestScenarioStringObject<TestScenarioStringJson.User> {

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

  /**
   * Define {@link TestScenario} for records with String key and JSON value.
   *
   * @param kafkaBootstrapServer {@link String} defining where to find Kafka brokers
   * @param schemaRegistryUrl {@link String} defining where to find SchemaRegistry
   * @param schemaRegistryClient {@link SchemaRegistryClient} providing access to the
   *                                                         SchemaRegistry instance available at
   *                                                         {@code schemaRegistryUrl}
   */
  public TestScenarioStringJson(String kafkaBootstrapServer,
                                String schemaRegistryUrl,
                                SchemaRegistryClient schemaRegistryClient) {
    super(TOPIC, KafkaJsonSchemaSerializer.class, KafkaJsonSchemaDeserializer.class,
        kafkaBootstrapServer, schemaRegistryUrl, schemaRegistryClient,
        Collections.singletonMap(
            KafkaJsonDeserializerConfig.JSON_VALUE_TYPE, User.class.getName()));
  }

  @Override
  public Optional<Schema> valueSchema() {
    return Optional.of(new Schema(topic() + "-value", 1, -1, "JSON",
        Collections.emptyList(), SCHEMA));
  }

  @Override
  public Serializer<User> valueSerializer() {
    return new KafkaJsonSchemaSerializer<>(schemaRegistryClient());
  }

  @Override
  public Deserializer<User> valueDeserializer() {
    return new KafkaJsonSchemaDeserializer<>(schemaRegistryClient());
  }

  @Override
  protected User randomValue() {
    return new User(UUID.randomUUID().toString(), UUID.randomUUID().toString());
  }

  @EqualsAndHashCode
  @NoArgsConstructor
  @AllArgsConstructor
  public static class User {
    @JsonProperty
    public String firstName;

    @JsonProperty
    public String lastName;
  }

}
