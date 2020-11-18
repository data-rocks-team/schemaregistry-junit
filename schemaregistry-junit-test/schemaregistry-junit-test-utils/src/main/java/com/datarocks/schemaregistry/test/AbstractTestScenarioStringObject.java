package com.datarocks.schemaregistry.test;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.entities.Schema;
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
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

public abstract class AbstractTestScenarioStringObject<V> implements TestScenario<String, V> {

  private final String topic;
  private final Properties producerProperties;
  private final Properties consumerProperties;
  private final SchemaRegistryClient schemaRegistryClient;

  /**
   * Define {@link TestScenario} for records with String key and Avro value.
   *
   * @param topic {@link String} defining the topic to use for this {@link TestScenario}
   * @param valueSerializer {@link Serializer} used to serialize kafka value
   * @param valueDeserializer {@link Deserializer} used to deserialize kafka value
   * @param kafkaBootstrapServer {@link String} defining where to find Kafka brokers
   * @param schemaRegistryUrl {@link String} defining where to find SchemaRegistry
   * @param schemaRegistryClient {@link SchemaRegistryClient} providing access to the
   *                                                         SchemaRegistry instance available at
   *                                                         {@code schemaRegistryUrl}
   */
  protected AbstractTestScenarioStringObject(String topic,
                                          Class<? extends Serializer> valueSerializer,
                                          Class<? extends Deserializer> valueDeserializer,
                                          String kafkaBootstrapServer,
                                          String schemaRegistryUrl,
                                          SchemaRegistryClient schemaRegistryClient) {
    this(topic, valueSerializer, valueDeserializer, kafkaBootstrapServer, schemaRegistryUrl,
        schemaRegistryClient, Collections.emptyMap());
  }

  /**
   * Define {@link TestScenario} for records with String key and Avro value.
   *
   * @param topic {@link String} defining the topic to use for this {@link TestScenario}
   * @param valueSerializer {@link Serializer} used to serialize kafka value
   * @param valueDeserializer {@link Deserializer} used to deserialize kafka value
   * @param kafkaBootstrapServer {@link String} defining where to find Kafka brokers
   * @param schemaRegistryUrl {@link String} defining where to find SchemaRegistry
   * @param schemaRegistryClient {@link SchemaRegistryClient} providing access to the
   *                                                         SchemaRegistry instance available at
   *                                                         {@code schemaRegistryUrl}
   * @param consumerPropertyOverride {@link Map} can be used to extend or override consumer
   *                                            properties
   */
  protected AbstractTestScenarioStringObject(String topic,
                                          Class<? extends Serializer> valueSerializer,
                                          Class<? extends Deserializer> valueDeserializer,
                                          String kafkaBootstrapServer,
                                          String schemaRegistryUrl,
                                          SchemaRegistryClient schemaRegistryClient,
                                          Map<String, Object> consumerPropertyOverride) {
    this.topic = topic;

    this.schemaRegistryClient = schemaRegistryClient;

    producerProperties = new Properties();
    producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer);
    producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
    producerProperties.put("schema.registry.url", schemaRegistryUrl);

    consumerProperties = new Properties();
    consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer);
    consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
    consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, topic);
    consumerProperties.put("schema.registry.url", schemaRegistryUrl);

    consumerPropertyOverride.forEach(consumerProperties::put);
  }

  @Override
  public String topic() {
    return topic;
  }

  @Override
  public Optional<Schema> keySchema() {
    return Optional.empty();
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
  public Deserializer<String> keyDeserializer() {
    return new StringDeserializer();
  }

  @Override
  public ProducerRecord<String, V> randomRecord() {
    return new ProducerRecord<>(topic, 0, randomKey(), randomValue());
  }

  private String randomKey() {
    return UUID.randomUUID().toString();
  }

  protected abstract V randomValue();

  @Override
  public Producer<String, V> producer() {
    return new KafkaProducer<>(producerProperties);
  }

  @Override
  public Consumer<String, V> consumer() {
    return new KafkaConsumer<>(consumerProperties);
  }

  @Override
  public SchemaRegistryClient schemaRegistryClient() {
    return schemaRegistryClient;
  }

}
