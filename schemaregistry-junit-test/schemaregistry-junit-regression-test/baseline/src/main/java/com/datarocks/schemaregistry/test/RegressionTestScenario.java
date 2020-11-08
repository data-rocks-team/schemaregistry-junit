package com.datarocks.schemaregistry.test;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.SneakyThrows;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class RegressionTestScenario {

    private static final String FIELD = "f1";
    private static final String SCHEMA = "{\"type\":\"record\"," + "\"name\":\"myrecord\"," +
            "\"fields\":[{\"name\":\"" + FIELD + "\",\"type\":\"string\"}]}";

    private final String topic;
    private final SchemaRegistryClient schemaRegistryClient;

    private final Producer<String, Object> producer;
    private final Consumer<String, Object> consumer;

    public RegressionTestScenario(String topic,
                                  String kafkaBootstrapServer,
                                  String schemaRegistryUrl,
                                  SchemaRegistryClient schemaRegistryClient) {
        this.topic = topic;
        this.schemaRegistryClient = schemaRegistryClient;

        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer);
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        producerProperties.put("schema.registry.url", schemaRegistryUrl);
        producer = new KafkaProducer<>(producerProperties);

        Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, topic);
        consumerProperties.put("schema.registry.url", schemaRegistryUrl);
        consumer = new KafkaConsumer<>(consumerProperties);
    }

    @SneakyThrows
    public void runTest() {
        ProducerRecord<String, Object> record = randomRecord();
        Set<TopicPartition> topicPartition = Collections.singleton(new TopicPartition(record.topic(), record.partition()));

        // Verify produce can produce using schema-registry
        producer.send(record).get();

        // Verify consume can consume using schema-registry
        consumer.assign(topicPartition);
        consumer.seekToBeginning(topicPartition);

        List<ConsumerRecord<String, Object>> records = new LinkedList<>();

        do {
            // consumer.poll(Duration) is only present from 5.x.x
            consumer.poll(TimeUnit.SECONDS.toMillis(2L))
                    .forEach(records::add);
        } while (records.isEmpty());

        assertThat(records).hasSize(1);
        assertThat(records.get(0).key()).isEqualTo(record.key());
        assertThat(records.get(0).value()).isEqualTo(record.value());

        assertThat(schemaRegistryClient.getAllSubjects()).contains(topic + "-value");
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
