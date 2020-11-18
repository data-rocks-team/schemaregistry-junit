package com.datarocks.schemaregistry.test;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.entities.Schema;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class TestScenarioStringAvro extends AbstractTestScenarioStringObject<Object> {

  private static final String TOPIC = "test-scenario-string-avro";

  private static final String FIELD = "f1";
  private static final String SCHEMA = "{\"type\":\"record\"," + "\"name\":\"myrecord\","
      + "\"fields\":[{\"name\":\"" + FIELD + "\",\"type\":\"string\"}]}";

  /**
   * Define {@link TestScenario} for records with String key and Avro value.
   *
   * @param kafkaBootstrapServer {@link String} defining where to find Kafka brokers
   * @param schemaRegistryUrl {@link String} defining where to find SchemaRegistry
   * @param schemaRegistryClient {@link SchemaRegistryClient} providing access to the
   *                                                         SchemaRegistry instance available at
   *                                                         {@code schemaRegistryUrl}
   */
  public TestScenarioStringAvro(String kafkaBootstrapServer,
                                String schemaRegistryUrl,
                                SchemaRegistryClient schemaRegistryClient) {
    super(TOPIC, KafkaAvroSerializer.class, KafkaAvroDeserializer.class, kafkaBootstrapServer,
        schemaRegistryUrl, schemaRegistryClient);
  }

  @Override
  public Optional<Schema> valueSchema() {
    return Optional.of(new Schema(topic() + "-value", 1, -1, "AVRO",
        Collections.emptyList(), SCHEMA));
  }

  @Override
  public Serializer<Object> valueSerializer() {
    return new KafkaAvroSerializer(schemaRegistryClient());
  }

  @Override
  public Deserializer<Object> valueDeserializer() {
    return new KafkaAvroDeserializer(schemaRegistryClient());
  }

  @Override
  protected Object randomValue() {
    org.apache.avro.Schema.Parser parser = new org.apache.avro.Schema.Parser();
    org.apache.avro.Schema schema = parser.parse(SCHEMA);

    GenericRecord avroRecord = new GenericData.Record(schema);
    avroRecord.put(FIELD, UUID.randomUUID().toString());

    return avroRecord;
  }

}
