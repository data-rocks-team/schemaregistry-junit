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
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

public class TestScenarioStringJson implements TestScenario<String, TestScenarioStringJson.User> {

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

  private Properties producerProperties;
  private Properties consumerProperties;
  private SchemaRegistryClient schemaRegistryClient;

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
    this.schemaRegistryClient = schemaRegistryClient;

    producerProperties = new Properties();
    producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer);
    producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        KafkaJsonSchemaSerializer.class);
    producerProperties.put("schema.registry.url", schemaRegistryUrl);

    consumerProperties = new Properties();
    consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer);
    consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        KafkaJsonSchemaDeserializer.class);
    consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG,
        TestScenarioStringJson.class.getCanonicalName());
    consumerProperties.put("schema.registry.url", schemaRegistryUrl);
    consumerProperties.put(KafkaJsonDeserializerConfig.JSON_VALUE_TYPE, User.class.getName());
  }

  @Override
  public String topic() {
    return TOPIC;
  }

  @Override
  public Optional<Schema> keySchema() {
    return Optional.empty();
  }

  @Override
  public Optional<Schema> valueSchema() {
    return Optional.of(new Schema(topic() + "-value", 1, -1, "JSON",
        Collections.emptyList(), SCHEMA));
  }

  @Override
  public Properties producerProperties() {
    return producerProperties;
  }

  @Override
  public Properties consumerProperties() {
    return consumerProperties;
  }

  @Override
  public Serializer<String> keySerializer() {
    return new StringSerializer();
  }

  @Override
  public Serializer<User> valueSerializer() {
    return new KafkaJsonSchemaSerializer<>(schemaRegistryClient);
  }

  @Override
  public Deserializer<String> keyDeserializer() {
    return new StringDeserializer();
  }

  @Override
  public Deserializer<User> valueDeserializer() {
    return new KafkaJsonSchemaDeserializer<>(schemaRegistryClient);
  }

  @Override
  public ProducerRecord<String, User> randomRecord() {
    return new ProducerRecord<>(TOPIC, 0, randomKey(), randomUser());
  }

  private String randomKey() {
    return UUID.randomUUID().toString();
  }

  private User randomUser() {
    return new User(UUID.randomUUID().toString(), UUID.randomUUID().toString());
  }

  @Override
  public Producer<String, User> producer() {
    return new KafkaProducer<>(producerProperties);
  }

  @Override
  public Consumer<String, User> consumer() {
    return new KafkaConsumer<>(consumerProperties);
  }

  @Override
  public SchemaRegistryClient schemaRegistryClient() {
    return schemaRegistryClient;
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
