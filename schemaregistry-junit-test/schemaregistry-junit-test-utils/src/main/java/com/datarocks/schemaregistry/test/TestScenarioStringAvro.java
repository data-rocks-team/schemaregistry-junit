package com.datarocks.schemaregistry.test;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.entities.Schema;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
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

public class TestScenarioStringAvro implements TestScenario<String, Object> {

    private static final String TOPIC = "test-scenario-string-avro";

    private static final String FIELD = "f1";
    private static final String SCHEMA = "{\"type\":\"record\"," + "\"name\":\"myrecord\"," +
            "\"fields\":[{\"name\":\"" + FIELD + "\",\"type\":\"string\"}]}";

    private Properties producerProperties;
    private Properties consumerProperties;
    private SchemaRegistryClient schemaRegistryClient;

    public TestScenarioStringAvro(String kafkaBootstrapServer,
                                  String schemaRegistryUrl,
                                  SchemaRegistryClient schemaRegistryClient) {
        this.schemaRegistryClient = schemaRegistryClient;

        producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer);
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        producerProperties.put("schema.registry.url", schemaRegistryUrl);

        consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, TestScenarioStringAvro.class.getCanonicalName());
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
        return Optional.of(new Schema(topic() + "-value", 1, -1, "AVRO",
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
    public Serializer<Object> valueSerializer() {
        return new KafkaAvroSerializer(schemaRegistryClient);
    }

    @Override
    public Deserializer<String> keyDeserializer() {
        return new StringDeserializer();
    }

    @Override
    public Deserializer<Object> valueDeserializer() {
        return new KafkaAvroDeserializer(schemaRegistryClient);
    }

    @Override
    public ProducerRecord<String, Object> randomRecord() {
        return new ProducerRecord<>(TOPIC, 0, randomKey(), randomAvroRecord());
    }

    private String randomKey() {
        return UUID.randomUUID().toString();
    }

    private GenericRecord randomAvroRecord() {
        org.apache.avro.Schema.Parser parser = new org.apache.avro.Schema.Parser();
        org.apache.avro.Schema schema = parser.parse(SCHEMA);

        GenericRecord avroRecord = new GenericData.Record(schema);
        avroRecord.put(FIELD, UUID.randomUUID().toString());

        return avroRecord;
    }

    @Override
    public Producer<String, Object> producer() {
        return new KafkaProducer<>(producerProperties);
    }

    @Override
    public Consumer<String, Object> consumer() {
        return new KafkaConsumer<>(consumerProperties);
    }

    @Override
    public SchemaRegistryClient schemaRegistryClient() {
        return schemaRegistryClient;
    }

}
