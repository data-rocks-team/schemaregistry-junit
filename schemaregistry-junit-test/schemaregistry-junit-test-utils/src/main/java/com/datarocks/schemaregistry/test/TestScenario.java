package com.datarocks.schemaregistry.test;

import com.google.protobuf.DynamicMessage;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.entities.Schema;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public interface TestScenario<K, V> {

    String topic();

    Optional<Schema> keySchema();

    Optional<Schema> valueSchema();

    Properties producerProperties();

    Properties consumerProperties();

    Serializer<K> keySerializer();

    Serializer<V> valueSerializer();

    Deserializer<K> keyDeserializer();

    Deserializer<V> valueDeserializer();

    ProducerRecord<K, V> randomRecord();

    Producer<K, V> producer();

    Consumer<K, V> consumer();

    SchemaRegistryClient schemaRegistryClient();

    @SneakyThrows
    default void runTest() {
        ProducerRecord<K, V> record = randomRecord();
        Set<TopicPartition> topicPartition = Collections.singleton(new TopicPartition(record.topic(), record.partition()));

        // Verify produce can produce using schema-registry
        Producer<K, V> producer = producer();
        producer.send(record).get();

        // Verify consume can consume using schema-registry
        Consumer<K, V> consumer = consumer();

        consumer.assign(topicPartition);
        consumer.seekToBeginning(topicPartition);

        List<ConsumerRecord<K, V>> records = new LinkedList<>();

        do {
            consumer.poll(Duration.ofSeconds(2)).forEach(records::add);
        } while (records.isEmpty());

        assertThat(records).hasSize(1);
        assertThat(records.get(0).key()).isEqualTo(record.key());

        // For Protobuf the deserializer returns DynamicMessage and not the actual Message
        if (records.get(0).value() instanceof DynamicMessage) {
            assertThat(Myrecord.MyRecord.parseFrom(((DynamicMessage) records.get(0).value()).toByteArray()))
                    .isEqualTo(record.value());
        } else {
            assertThat(records.get(0).value()).isEqualTo(record.value());
        }

        // If present, verify key schema can be retrieved from schema-registry
        keySchema().ifPresent(keySchema -> {
            Schema schema = schemaRegistryClient()
                    .getByVersion(topic() + "-key", 1, false);

            assertThat(schema.getSchemaType()).isEqualTo(keySchema.getSchemaType());
            assertThat(schema.getSchema()).isEqualTo(keySchema.getSchema());
        });

        // If present, verify value schema can be retrieved from schema-registry
        valueSchema().ifPresent(valueSchema -> {
            Schema schema = schemaRegistryClient()
                    .getByVersion(topic() + "-value", 1, false);

            assertThat(schema.getSchemaType()).isEqualTo(valueSchema.getSchemaType());
            assertThat(schema.getSchema()).isEqualTo(valueSchema.getSchema());
        });
    }
}
