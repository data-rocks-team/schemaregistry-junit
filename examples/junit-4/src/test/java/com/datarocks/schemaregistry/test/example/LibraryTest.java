package com.datarocks.schemaregistry.test.example;

import com.datarocks.schemaregistry.test.junit4.SharedSchemaRegistryTestResource;
import com.salesforce.kafka.test.junit4.SharedKafkaTestResource;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class LibraryTest {

  private static final String TOPIC = "library-test";

  @ClassRule
  public static final SharedKafkaTestResource kafka = new SharedKafkaTestResource()
      .withBrokers(1);

  private static final SharedSchemaRegistryTestResource schemaRegistry =
      new SharedSchemaRegistryTestResource()
          .withBootstrapServers(kafka::getKafkaConnectString);

  // SharedSchemaRegistryTestResource depends on SharedKafkaTestResource. JUnit 4 does not offer a
  // way to order ClassRule initialisation. The only way to make sure Kafka Class Rule will be
  // created before SchemaRegistry one is exposing SchemaRegistry via a method. Quoting Junit4
  // documentation "If there are multiple annotated ClassRules on a class, they will be applied
  // in an order that depends on your JVM's implementation of the reflection API, which is
  // undefined, in general. However, Rules defined by fields will always be applied before
  // Rules defined by methods." (reference
  // https://junit.org/junit4/javadoc/4.12/org/junit/ClassRule.html)
  @ClassRule
  public static TestRule schemaRegistry() {
    return schemaRegistry;
  }

  @Test
  public void testSomeLibraryMethod() throws Exception {
    Library classUnderTest = new Library(
        TOPIC,
        kafka.getKafkaConnectString(),
        schemaRegistry.schemaRegistryUrl());

    assertThatCode(classUnderTest::someLibraryMethod)
        .doesNotThrowAnyException();

    List<ConsumerRecord<byte[], byte[]>> records = kafka.getKafkaTestUtils()
        .consumeAllRecordsFromTopic(TOPIC);

    // Check that the record is there
    assertThat(records).hasSize(1);

    // Check that the schema is there
    assertThat(schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient().getAllSubjects())
        .contains(TOPIC + "-value");
  }
}
