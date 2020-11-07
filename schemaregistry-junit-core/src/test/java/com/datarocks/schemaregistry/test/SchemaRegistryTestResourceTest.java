package com.datarocks.schemaregistry.test;

import com.salesforce.kafka.test.junit5.SharedKafkaTestResource;
import io.confluent.kafka.schemaregistry.rest.SchemaRegistryConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

public class SchemaRegistryTestResourceTest {

    @RegisterExtension
    public static final SharedKafkaTestResource kafka = new SharedKafkaTestResource()
            .withBrokers(1);

    @Test
    @SneakyThrows
    void shouldSetBootstrapServers() {
        SchemaRegistryTestResource<?> schemaRegistry = new SchemaRegistryTestResource<>()
                .withBootstrapServers(kafka.getKafkaConnectString());

        assertThatCode(schemaRegistry::start)
                .doesNotThrowAnyException();

        assertThatCode(() -> schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient().getMode())
                .doesNotThrowAnyException();

        assertThatCode(schemaRegistry::shutdown)
                .doesNotThrowAnyException();
    }

    @Test
    void shouldSetBootstrapServersViaSupplier() {
        SchemaRegistryTestResource<?> schemaRegistry = new SchemaRegistryTestResource<>()
                .withBootstrapServers(kafka::getKafkaConnectString);

        assertThatCode(schemaRegistry::start)
                .doesNotThrowAnyException();

        assertThatCode(() -> schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient().getMode())
                .doesNotThrowAnyException();

        assertThatCode(schemaRegistry::shutdown)
                .doesNotThrowAnyException();
    }

    @Test
    void shouldSetPort() {
        Integer port = 8888;
        SchemaRegistryTestResource<?> schemaRegistry = new SchemaRegistryTestResource<>()
                .withBootstrapServers(kafka.getKafkaConnectString())
                .withPort(port);

        assertThatCode(schemaRegistry::start)
                .doesNotThrowAnyException();

        assertThat(schemaRegistry.schemaRegistryUrl()).contains(port.toString());

        assertThatCode(() -> schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient().getMode())
                .doesNotThrowAnyException();

        assertThatCode(schemaRegistry::shutdown)
                .doesNotThrowAnyException();
    }

    @Test
    void shouldUseRandomPort() {
        SchemaRegistryTestResource<?> schemaRegistry = new SchemaRegistryTestResource<>()
                .withBootstrapServers(kafka.getKafkaConnectString())
                .withRandomPort();

        assertThatCode(schemaRegistry::start)
                .doesNotThrowAnyException();

        assertThat(schemaRegistry.schemaRegistryUrl()).doesNotContain("8081");

        assertThatCode(() -> schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient().getMode())
                .doesNotThrowAnyException();

        assertThatCode(schemaRegistry::shutdown)
                .doesNotThrowAnyException();
    }

    @Test
    void shouldSetProperty() {
        SchemaRegistryTestResource<?> schemaRegistry = new SchemaRegistryTestResource<>()
                .withProperty(SchemaRegistryConfig.KAFKASTORE_BOOTSTRAP_SERVERS_CONFIG, kafka.getKafkaConnectString());

        assertThatCode(schemaRegistry::start)
                .doesNotThrowAnyException();

        assertThatCode(() -> schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient().getMode())
                .doesNotThrowAnyException();

        assertThatCode(schemaRegistry::shutdown)
                .doesNotThrowAnyException();
    }

    @Test
    void shouldUsePropertyProvidedViaConstructor() {
        Properties properties = new Properties();
        properties.put(SchemaRegistryConfig.KAFKASTORE_BOOTSTRAP_SERVERS_CONFIG, kafka.getKafkaConnectString());
        SchemaRegistryTestResource<?> schemaRegistry = new SchemaRegistryTestResource<>(properties);

        assertThatCode(schemaRegistry::start)
                .doesNotThrowAnyException();

        assertThatCode(() -> schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient().getMode())
                .doesNotThrowAnyException();

        assertThatCode(schemaRegistry::shutdown)
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowIfSettingPropertyAfterCallingStart() {
        SchemaRegistryTestResource<?> schemaRegistry = new SchemaRegistryTestResource<>()
                .withProperty(SchemaRegistryConfig.KAFKASTORE_BOOTSTRAP_SERVERS_CONFIG, kafka.getKafkaConnectString());

        assertThatCode(schemaRegistry::start)
                .doesNotThrowAnyException();

        assertThatIllegalStateException()
                .isThrownBy(() -> schemaRegistry.withProperty("test", "test"))
                .withMessage("Cannot add properties after service has started.");

        assertThatCode(schemaRegistry::shutdown)
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowIfSettingBootstrapServerAfterCallingStart() {
        SchemaRegistryTestResource<?> schemaRegistry = new SchemaRegistryTestResource<>()
                .withProperty(SchemaRegistryConfig.KAFKASTORE_BOOTSTRAP_SERVERS_CONFIG, kafka.getKafkaConnectString());

        assertThatCode(schemaRegistry::start)
                .doesNotThrowAnyException();

        assertThatIllegalStateException()
                .isThrownBy(() -> schemaRegistry.withBootstrapServers(kafka.getKafkaConnectString()))
                .withMessage("Cannot add properties after service has started.");

        assertThatIllegalStateException()
                .isThrownBy(() -> schemaRegistry.withBootstrapServers(kafka::getKafkaConnectString))
                .withMessage("Cannot add bootstrap servers after service has started.");

        assertThatCode(schemaRegistry::shutdown)
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowIfSettingPortAfterCallingStart() {
        SchemaRegistryTestResource<?> schemaRegistry = new SchemaRegistryTestResource<>()
                .withProperty(SchemaRegistryConfig.KAFKASTORE_BOOTSTRAP_SERVERS_CONFIG, kafka.getKafkaConnectString());

        assertThatCode(schemaRegistry::start)
                .doesNotThrowAnyException();

        assertThatIllegalStateException()
                .isThrownBy(() -> schemaRegistry.withPort(0))
                .withMessage("Cannot add properties after service has started.");

        assertThatCode(schemaRegistry::shutdown)
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowIfSettingRandomPortAfterCallingStart() {
        SchemaRegistryTestResource<?> schemaRegistry = new SchemaRegistryTestResource<>()
                .withProperty(SchemaRegistryConfig.KAFKASTORE_BOOTSTRAP_SERVERS_CONFIG, kafka.getKafkaConnectString());

        assertThatCode(schemaRegistry::start)
                .doesNotThrowAnyException();

        assertThatIllegalStateException()
                .isThrownBy(schemaRegistry::withRandomPort)
                .withMessage("Cannot add properties after service has started.");

        assertThatCode(schemaRegistry::shutdown)
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowIfCallingStartMultipleTimes() {
        SchemaRegistryTestResource<?> schemaRegistry = new SchemaRegistryTestResource<>()
                .withProperty(SchemaRegistryConfig.KAFKASTORE_BOOTSTRAP_SERVERS_CONFIG, kafka.getKafkaConnectString());

        assertThatCode(schemaRegistry::start)
                .doesNotThrowAnyException();

        assertThatIllegalStateException()
                .isThrownBy(schemaRegistry::start)
                .withMessage("Schema-registry test server already exists!");

        assertThatCode(schemaRegistry::shutdown)
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowIfCallingShutdownBeforeStart() {
        SchemaRegistryTestResource<?> schemaRegistry = new SchemaRegistryTestResource<>()
                .withProperty(SchemaRegistryConfig.KAFKASTORE_BOOTSTRAP_SERVERS_CONFIG, kafka.getKafkaConnectString());

        assertThatIllegalStateException()
                .isThrownBy(schemaRegistry::shutdown)
                .withMessage("Schema-registry test server does not exist yet!");
    }
}
