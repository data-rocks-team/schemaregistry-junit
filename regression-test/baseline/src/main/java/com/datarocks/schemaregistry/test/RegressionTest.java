package com.datarocks.schemaregistry.test;

import com.datarocks.schemaregistry.test.junit5.SharedSchemaRegistryTestResource;
import com.salesforce.kafka.test.junit5.SharedKafkaTestResource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public abstract class RegressionTest {

  @RegisterExtension
  @Order(1)
  public static final SharedKafkaTestResource kafka = new SharedKafkaTestResource()
      .withBrokers(1);

  @RegisterExtension
  @Order(2)
  static final SharedSchemaRegistryTestResource schemaRegistry =
      new SharedSchemaRegistryTestResource()
          .withBootstrapServers(kafka::getKafkaConnectString);

  @Test
  @SneakyThrows
  void shouldProduceConsumeAndFetchSchema() {
    regressionTestScenario().runTest();
  }

  private RegressionTestScenario regressionTestScenario() {
    return new RegressionTestScenario(
        topic(),
        kafka.getKafkaConnectString(),
        schemaRegistry.schemaRegistryUrl(),
        schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient());
  }

  public abstract String topic();

}
