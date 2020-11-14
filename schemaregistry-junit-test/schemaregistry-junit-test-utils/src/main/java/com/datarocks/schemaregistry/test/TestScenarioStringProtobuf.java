package com.datarocks.schemaregistry.test;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.entities.Schema;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer;
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

public class TestScenarioStringProtobuf implements TestScenario<String, Myrecord.MyRecord> {

  private static final String TOPIC = "test-scenario-string-protobuf";
  private static final String SCHEMA = "syntax = \"proto3\";\n"
      + "package com.datarocks.schemaregistry.test;\n"
      + "\n"
      + "message MyRecord {\n"
      + "  string f1 = 1;\n"
      + "}\n";

  private Properties producerProperties;
  private Properties consumerProperties;
  private SchemaRegistryClient schemaRegistryClient;

  /**
   * Define {@link TestScenario} for records with String key and Protobuf value.
   *
   * @param kafkaBootstrapServer {@link String} defining where to find Kafka brokers
   * @param schemaRegistryUrl {@link String} defining where to find SchemaRegistry
   * @param schemaRegistryClient {@link SchemaRegistryClient} providing access to the
   *                                                         SchemaRegistry instance available at
   *                                                         {@code schemaRegistryUrl}
   */
  public TestScenarioStringProtobuf(String kafkaBootstrapServer,
                                    String schemaRegistryUrl,
                                    SchemaRegistryClient schemaRegistryClient) {
    this.schemaRegistryClient = schemaRegistryClient;

    producerProperties = new Properties();
    producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer);
    producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        KafkaProtobufSerializer.class);
    producerProperties.put("schema.registry.url", schemaRegistryUrl);

    consumerProperties = new Properties();
    consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer);
    consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        KafkaProtobufDeserializer.class);
    consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG,
        TestScenarioStringProtobuf.class.getCanonicalName());
    consumerProperties.put("schema.registry.url", schemaRegistryUrl);
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
    return Optional.of(new Schema(topic() + "-value", 1, -1,
        "PROTOBUF", Collections.emptyList(), SCHEMA));
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
  public Serializer<Myrecord.MyRecord> valueSerializer() {
    return new KafkaProtobufSerializer<>(schemaRegistryClient);
  }

  @Override
  public Deserializer<String> keyDeserializer() {
    return new StringDeserializer();
  }

  @Override
  public Deserializer<Myrecord.MyRecord> valueDeserializer() {
    return new KafkaProtobufDeserializer<>(schemaRegistryClient);
  }

  @Override
  public ProducerRecord<String, Myrecord.MyRecord> randomRecord() {
    return new ProducerRecord<>(TOPIC, 0, randomKey(), randomProtoRecord());
  }

  private String randomKey() {
    return UUID.randomUUID().toString();
  }

  private Myrecord.MyRecord randomProtoRecord() {
    return Myrecord.MyRecord.newBuilder().setF1(UUID.randomUUID().toString()).build();
  }

  @Override
  public Producer<String, Myrecord.MyRecord> producer() {
    return new KafkaProducer<>(producerProperties);
  }

  @Override
  public Consumer<String, Myrecord.MyRecord> consumer() {
    return new KafkaConsumer<>(consumerProperties);
  }

  @Override
  public SchemaRegistryClient schemaRegistryClient() {
    return schemaRegistryClient;
  }
}
