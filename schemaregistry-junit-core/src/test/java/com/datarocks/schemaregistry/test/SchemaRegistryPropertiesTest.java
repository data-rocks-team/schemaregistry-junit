package com.datarocks.schemaregistry.test;

import io.confluent.kafka.schemaregistry.rest.SchemaRegistryConfig;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class SchemaRegistryPropertiesTest {

    @Test
    void shouldReturnSingleListenerString() {
        assertThat(SchemaRegistryProperties.singleListenerString(0))
                .isEqualTo("http://localhost:0");
    }

    @Test
    void shouldReturnUrlUsingDefault() {
        assertThat(new SchemaRegistryProperties(new Properties()).url())
                .isEqualTo("http://localhost:8081");
    }

    @Test
    void shouldReturnUrlUsingProvidedListener() {
        Properties properties = new Properties();
        properties.put(SchemaRegistryConfig.LISTENERS_CONFIG, "listener");

        assertThat(new SchemaRegistryProperties(properties).url())
                .isEqualTo("listener");
    }

    @Test
    void shouldReturnUrlUsingTheFirstProvidedListener() {
        Properties properties = new Properties();
        properties.put(SchemaRegistryConfig.LISTENERS_CONFIG, "one, two");

        assertThat(new SchemaRegistryProperties(properties).url())
                .isEqualTo("one");
    }

    @Test
    void shouldSetBootstrapServerViaSupplier() {
        SchemaRegistryProperties srProperties = new SchemaRegistryProperties(new Properties());
        srProperties.addBootstrapServersSupplier(() -> "value");

        assertThat(srProperties.schemaRegistryConfig().bootstrapBrokers())
                .isEqualTo("PLAINTEXT://value");
    }

    @Test
    void shouldSetBootstrapServerViaProperty() {
        SchemaRegistryProperties srPropAddProp = new SchemaRegistryProperties(new Properties());
        srPropAddProp.addProperty(SchemaRegistryConfig.KAFKASTORE_BOOTSTRAP_SERVERS_CONFIG, "value");

        assertThat(srPropAddProp.schemaRegistryConfig().bootstrapBrokers())
                .isEqualTo("PLAINTEXT://value");

        Properties prop = new Properties();
        prop.put(SchemaRegistryConfig.KAFKASTORE_BOOTSTRAP_SERVERS_CONFIG, "value");
        SchemaRegistryProperties srProp = new SchemaRegistryProperties(prop);

        assertThat(srProp.schemaRegistryConfig().bootstrapBrokers())
                .isEqualTo("PLAINTEXT://value");
    }

    @Test
    void shouldUseDefaultValues() {
        SchemaRegistryProperties properties = new SchemaRegistryProperties(new Properties());
        properties.addProperty(SchemaRegistryConfig.KAFKASTORE_BOOTSTRAP_SERVERS_CONFIG, "value");

        assertThat(properties.schemaRegistryConfig().originalProperties())
                .contains(
                        new AbstractMap.SimpleEntry<>(SchemaRegistryConfig.LISTENERS_CONFIG, "http://localhost:8081"),
                        new AbstractMap.SimpleEntry<>(SchemaRegistryConfig.HOST_NAME_CONFIG, "localhost"),
                        new AbstractMap.SimpleEntry<>(SchemaRegistryConfig.DEBUG_CONFIG, true));
    }

    @Test
    void shouldUseOverrideDefaultValues() {
        SchemaRegistryProperties propAddProp = new SchemaRegistryProperties(new Properties());
        propAddProp.addProperty(SchemaRegistryConfig.KAFKASTORE_BOOTSTRAP_SERVERS_CONFIG, "value");
        propAddProp.addProperty(SchemaRegistryConfig.DEBUG_CONFIG, false);

        assertThat(propAddProp.schemaRegistryConfig().originalProperties())
                .contains(
                        new AbstractMap.SimpleEntry<>(SchemaRegistryConfig.LISTENERS_CONFIG, "http://localhost:8081"),
                        new AbstractMap.SimpleEntry<>(SchemaRegistryConfig.HOST_NAME_CONFIG, "localhost"),
                        new AbstractMap.SimpleEntry<>(SchemaRegistryConfig.DEBUG_CONFIG, false));

        Properties prop = new Properties();
        prop.put(SchemaRegistryConfig.DEBUG_CONFIG, false);
        SchemaRegistryProperties propViaConstructor = new SchemaRegistryProperties(prop);
        propViaConstructor.addProperty(SchemaRegistryConfig.KAFKASTORE_BOOTSTRAP_SERVERS_CONFIG, "value");

        assertThat(propViaConstructor.schemaRegistryConfig().originalProperties())
                .contains(
                        new AbstractMap.SimpleEntry<>(SchemaRegistryConfig.LISTENERS_CONFIG, "http://localhost:8081"),
                        new AbstractMap.SimpleEntry<>(SchemaRegistryConfig.HOST_NAME_CONFIG, "localhost"),
                        new AbstractMap.SimpleEntry<>(SchemaRegistryConfig.DEBUG_CONFIG, false));
    }
}
