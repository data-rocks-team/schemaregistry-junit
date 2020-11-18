package com.datarocks.schemaregistry.test;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.entities.Schema;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class TestScenarioStringProtobuf
    extends AbstractTestScenarioStringObject<Myrecord.MyRecord> {

  private static final String TOPIC = "test-scenario-string-protobuf";
  private static final String SCHEMA = "syntax = \"proto3\";\n"
      + "package com.datarocks.schemaregistry.test;\n"
      + "\n"
      + "message MyRecord {\n"
      + "  string f1 = 1;\n"
      + "}\n";

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
    super(TOPIC, KafkaProtobufSerializer.class, KafkaProtobufDeserializer.class,
        kafkaBootstrapServer, schemaRegistryUrl, schemaRegistryClient);
  }

  @Override
  public Optional<Schema> valueSchema() {
    return Optional.of(new Schema(topic() + "-value", 1, -1,
        "PROTOBUF", Collections.emptyList(), SCHEMA));
  }

  @Override
  public Serializer<Myrecord.MyRecord> valueSerializer() {
    return new KafkaProtobufSerializer<>(schemaRegistryClient());
  }

  @Override
  public Deserializer<Myrecord.MyRecord> valueDeserializer() {
    return new KafkaProtobufDeserializer<>(schemaRegistryClient());
  }

  @Override
  protected Myrecord.MyRecord randomValue() {
    return Myrecord.MyRecord.newBuilder().setF1(UUID.randomUUID().toString()).build();
  }
}
