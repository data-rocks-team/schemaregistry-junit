package com.datarocks.schemaregistry.test;

import io.confluent.kafka.schemaregistry.rest.SchemaRegistryConfig;
import io.confluent.kafka.schemaregistry.rest.SchemaRegistryRestApplication;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.curator.test.InstanceSpec;
import org.eclipse.jetty.server.Server;

import java.util.Properties;
import java.util.function.Supplier;

@Log
public class SchemaRegistryTestResource<T extends SchemaRegistryTestResource> {

    private final SchemaRegistryProperties schemaRegistryProperties;

    private Server server;

    public SchemaRegistryTestResource() {
        this(new Properties());
    }

    public SchemaRegistryTestResource(Properties properties) {
        this.schemaRegistryProperties = new SchemaRegistryProperties(properties);
    }

    public T withBootstrapServers(String bootstrapServers) {
        return withProperty(SchemaRegistryConfig.KAFKASTORE_BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    }

    @SuppressWarnings("unchecked")
    public T withBootstrapServers(Supplier<String> bootstrapServers) {
        this.schemaRegistryProperties.addBootstrapServersSupplier(bootstrapServers);
        return (T) this;
    }

    public T withPort(int port) {
        return withProperty(SchemaRegistryConfig.LISTENERS_CONFIG, SchemaRegistryProperties.singleListenerString(port));
    }

    public T withRandomPort() {
        return withPort(InstanceSpec.getRandomPort());
    }

    @SuppressWarnings("unchecked")
    public T withProperty(String name, Object value) {
        schemaRegistryProperties.addProperty(name, value);
        return (T) this;
    }

    @SneakyThrows
    public void start() {
        log.info("Starting schema-registry test server");

        validateServerExist("Schema-registry test server already exists!");

        SchemaRegistryConfig config = schemaRegistryProperties.schemaRegistryConfig();
        SchemaRegistryRestApplication app = new SchemaRegistryRestApplication(config);

        server = app.createServer();
        server.start();
    }

    @SneakyThrows
    public void shutdown() {
        log.info("Shutting down schema-registry test server");

        validateServerDoesNotExist("Schema-registry test server does not exist yet!");

        server.stop();
    }

    public String schemaRegistryUrl() {
        return schemaRegistryProperties.url();
    }

    public SchemaRegistryTestUtils schemaRegistryTestUtils() {
        validateServerDoesNotExist("Cannot access SchemaRegistryTestUtils before Schema-registry has been started.");
        return new SchemaRegistryTestUtils(schemaRegistryProperties);
    }

    private void validateServerDoesNotExist(final String errorMessage) throws IllegalStateException {
        if (server == null) {
            throw new IllegalStateException(errorMessage);
        }
    }

    private void validateServerExist(final String errorMessage) throws IllegalStateException {
        if (server != null) {
            throw new IllegalStateException(errorMessage);
        }
    }
}
