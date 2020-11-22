package com.datarocks.schemaregistry.test.example;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Library {

    private static final String FIELD = "f1";
    public static final String SCHEMA = "{\"type\":\"record\"," + "\"name\":\"myrecord\","
        + "\"fields\":[{\"name\":\"" + FIELD + "\",\"type\":\"string\"}]}";

    private final String topic;

    private final Producer<String, Object> producer;

    public Library(String topic, String kafkaBootstrapServer, String schemaRegistryUrl) {
        this.topic = topic;

        // Producer property
        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer);
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        producerProperties.put("schema.registry.url", schemaRegistryUrl);
        producer = new KafkaProducer<>(producerProperties);
    }

    public void someLibraryMethod() throws ExecutionException, InterruptedException {
        // Produce one record
        producer.send(randomRecord()).get();
    }

    private ProducerRecord<String, Object> randomRecord() {
        return new ProducerRecord<>(topic, 0, randomKey(), randomAvroRecord());
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
}
