package com.datarocks.schemaregistry.test.junit5;

import com.datarocks.schemaregistry.test.TestScenario;
import com.datarocks.schemaregistry.test.TestScenarioStringAvro;
import com.datarocks.schemaregistry.test.TestScenarioStringJson;
import com.datarocks.schemaregistry.test.TestScenarioStringProtobuf;
import com.salesforce.kafka.test.junit5.SharedKafkaTestResource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class SharedSchemaRegistryTestResourceTest {

    @RegisterExtension
    @Order(1)
    public static final SharedKafkaTestResource kafka = new SharedKafkaTestResource()
            .withBrokers(1);

    @RegisterExtension
    @Order(2)
    // SharedSchemaRegistryTestResource depends on SharedKafkaTestResource, therefore SharedKafkaTestResource should be
    // instantiated before SharedSchemaRegistryTestResource can be created.
    static final SharedSchemaRegistryTestResource schemaRegistry = new SharedSchemaRegistryTestResource()
            .withBootstrapServers(kafka::getKafkaConnectString);

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("testScenarios")
    <K, V> void shouldProduceConsumeAndFetchSchema(TestScenario<K, V> scenario) {
        scenario.runTest();
    }

    private static Stream<Arguments> testScenarios() {
        return Stream.of(
                Arguments.of(new TestScenarioStringAvro(
                        kafka.getKafkaConnectString(),
                        schemaRegistry.schemaRegistryUrl(),
                        schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient())),
                Arguments.of(new TestScenarioStringJson(
                        kafka.getKafkaConnectString(),
                        schemaRegistry.schemaRegistryUrl(),
                        schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient())),
                Arguments.of(new TestScenarioStringProtobuf(
                        kafka.getKafkaConnectString(),
                        schemaRegistry.schemaRegistryUrl(),
                        schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient()))
        );
    }

}
