package com.datarocks.schemaregistry.test.example;

import com.datarocks.schemaregistry.test.junit5.SharedSchemaRegistryTestResource;
import com.salesforce.kafka.test.junit5.SharedKafkaTestResource;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class LibraryTest {

  @RegisterExtension
  @Order(1)
  public static final SharedKafkaTestResource kafka = new SharedKafkaTestResource()
      .withBrokers(1);
  @RegisterExtension
  @Order(2)
  static final SharedSchemaRegistryTestResource schemaRegistry =
      new SharedSchemaRegistryTestResource()
          .withBootstrapServers(kafka::getKafkaConnectString);
  private static final String TOPIC = "library-test";

  @Test
  void testSomeLibraryMethod() throws Exception {
    Library classUnderTest = new Library(
        TOPIC,
        kafka.getKafkaConnectString(),
        schemaRegistry.schemaRegistryUrl());

    assertThatCode(classUnderTest::someLibraryMethod)
        .doesNotThrowAnyException();

    List<ConsumerRecord<byte[], byte[]>> records = kafka.getKafkaTestUtils()
        .consumeAllRecordsFromTopic(TOPIC);

    assertThat(records).hasSize(1);

    assertThat(schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient().getAllSubjects())
        .contains(TOPIC + "-value");
  }
}
