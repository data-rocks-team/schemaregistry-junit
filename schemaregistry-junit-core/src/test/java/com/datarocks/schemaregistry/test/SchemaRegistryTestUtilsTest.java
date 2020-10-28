package com.datarocks.schemaregistry.test;

import io.confluent.kafka.schemaregistry.rest.SchemaRegistryConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThatCode;

@Testcontainers
public class SchemaRegistryTestUtilsTest {

    @Container
    public static final GenericContainer<?> env = new GenericContainer<>(DockerImageName.parse("landoop/fast-data-dev:2.3.1"))
            .withExposedPorts(8081);

    private SchemaRegistryTestUtils schemaRegistryTestUtils;

    @BeforeEach
    void init() {
        Properties properties = new Properties();
        properties.put(SchemaRegistryConfig.LISTENERS_CONFIG,
                SchemaRegistryProperties.singleListenerString(env.getMappedPort(8081)));

        schemaRegistryTestUtils = new SchemaRegistryTestUtils(new SchemaRegistryProperties(properties));
    }

    @Test
    @SneakyThrows
    void shouldReturnWorkingClient() {
        assertThatCode(() -> schemaRegistryTestUtils.schemaRegistryClient().getMode())
                .doesNotThrowAnyException();
    }

    @Test
    @SneakyThrows
    void shouldReturnWorkingClientWithSizedCache() {
        assertThatCode(() -> schemaRegistryTestUtils.schemaRegistryClient(1).getMode())
                .doesNotThrowAnyException();
    }
}
