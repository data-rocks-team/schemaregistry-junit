package com.datarocks.schemaregistry.test.junit4;

import com.datarocks.schemaregistry.test.Myrecord;
import com.datarocks.schemaregistry.test.TestScenario;
import com.datarocks.schemaregistry.test.TestScenarioStringAvro;
import com.datarocks.schemaregistry.test.TestScenarioStringJson;
import com.datarocks.schemaregistry.test.TestScenarioStringProtobuf;
import com.salesforce.kafka.test.junit4.SharedKafkaTestResource;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;

public class SharedSchemaRegistryTestResourceTest {

    @ClassRule
    public static final SharedKafkaTestResource kafka = new SharedKafkaTestResource()
            .withBrokers(1);

    private static final SharedSchemaRegistryTestResource schemaRegistry = new SharedSchemaRegistryTestResource()
            .withBootstrapServers(kafka::getKafkaConnectString);

    // SharedSchemaRegistryTestResource depends on SharedKafkaTestResource. JUnit 4 does not offer a way to order Class
    // Rule initialisation. The only way to make sure Kafka Class Rule will be created before SchemaRegistry one is
    // exposing SchemaRegistry via a method. Quoting Junit4 documentation "If there are multiple annotated ClassRules
    // on a class, they will be applied in an order that depends on your JVM's implementation of the reflection API,
    // which is undefined, in general. However, Rules defined by fields will always be applied before Rules defined by
    // methods." (reference https://junit.org/junit4/javadoc/4.12/org/junit/ClassRule.html)
    @ClassRule
    public static TestRule schemaRegistry() {
        return schemaRegistry;
    }

    private final TestScenario<String, Object> avroScenario = new TestScenarioStringAvro(
            kafka.getKafkaConnectString(),
            schemaRegistry.schemaRegistryUrl(),
            schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient());

    private final TestScenario<String, TestScenarioStringJson.User> jsonScenario = new TestScenarioStringJson(
            kafka.getKafkaConnectString(),
            schemaRegistry.schemaRegistryUrl(),
            schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient());

    private final TestScenario<String, Myrecord.MyRecord> protobufScenario = new TestScenarioStringProtobuf(
            kafka.getKafkaConnectString(),
            schemaRegistry.schemaRegistryUrl(),
            schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient());

    @Test
    public void avroCase() {
        avroScenario.runTest();
    }

    @Test
    public void jsonCase() {
        jsonScenario.runTest();
    }

    @Test
    public void protobufCase() {
        protobufScenario.runTest();
    }

}
