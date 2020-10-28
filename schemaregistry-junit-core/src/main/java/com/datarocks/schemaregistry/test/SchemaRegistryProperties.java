package com.datarocks.schemaregistry.test;

import io.confluent.kafka.schemaregistry.rest.SchemaRegistryConfig;
import lombok.SneakyThrows;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

class SchemaRegistryProperties {

    static final String DEFAULT_HOST = "localhost";
    static final int DEFAULT_PORT = 8081;

    private static final Properties DEFAULT_PROPERTIES;

    static {
        DEFAULT_PROPERTIES = new Properties();
        DEFAULT_PROPERTIES.put(SchemaRegistryConfig.LISTENERS_CONFIG, singleListenerString(DEFAULT_PORT));
        DEFAULT_PROPERTIES.put(SchemaRegistryConfig.HOST_NAME_CONFIG, DEFAULT_HOST);
        DEFAULT_PROPERTIES.put(SchemaRegistryConfig.DEBUG_CONFIG, true);
    }

    private final Properties properties;
    private Supplier<String> bootstrapServersSupplier;

    SchemaRegistryProperties(Properties properties) {
        this.properties = new Properties();
        this.properties.putAll(DEFAULT_PROPERTIES);
        this.properties.putAll(properties);
    }

    void addProperty(String name, Object value) {
        properties.put(name, value);
    }

    public void addBootstrapServersSupplier(Supplier<String> bootstrapServers) {
        this.bootstrapServersSupplier = bootstrapServers;
    }

    @SneakyThrows
    SchemaRegistryConfig schemaRegistryConfig() {
        Optional.ofNullable(bootstrapServersSupplier).ifPresent(s ->
                addProperty(SchemaRegistryConfig.KAFKASTORE_BOOTSTRAP_SERVERS_CONFIG, s.get()));

        return new SchemaRegistryConfig(properties);
    }

    String url() {
        return properties.get(SchemaRegistryConfig.LISTENERS_CONFIG).toString().split(",")[0];
    }

    static String singleListenerString(int port) {
        return "http://" + DEFAULT_HOST + ":" + port;
    }
}
