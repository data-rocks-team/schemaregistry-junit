package com.datarocks.schemaregistry.test;

import io.confluent.kafka.schemaregistry.rest.SchemaRegistryConfig;
import io.confluent.kafka.schemaregistry.rest.SchemaRegistryRestApplication;
import lombok.extern.java.Log;
import org.apache.curator.test.InstanceSpec;
import org.eclipse.jetty.server.Server;

import java.util.Properties;
import java.util.function.Supplier;

/**
 * {@link SchemaRegistryRestApplication} wrapper with dsl to configure any property supported by
 * the current version of Confluent SchemaRegistry.
 *
 * @param <T> An implementation of this class, to allow for method chaining.
 */
@Log
public class SchemaRegistryTestResource<T extends SchemaRegistryTestResource> {

  private final SchemaRegistryProperties schemaRegistryProperties;

  private Server server;

  /**
   * Default constructor.
   */
  public SchemaRegistryTestResource() {
    this(new Properties());
  }

  /**
   * Constructor allowing passing additional SchemaRegistry properties.
   *
   * @param properties properties for SchemaRegistry server.
   */
  public SchemaRegistryTestResource(Properties properties) {
    this.schemaRegistryProperties = new SchemaRegistryProperties(properties);
  }

  /**
   * Set the address to establish a connection to the Kafka cluster.
   *
   * @param bootstrapServers Kafka connection string
   * @return {@link SchemaRegistryTestResource} for method chaining.
   */
  public T withBootstrapServers(String bootstrapServers) {
    return withProperty(SchemaRegistryConfig.KAFKASTORE_BOOTSTRAP_SERVERS_CONFIG,
        bootstrapServers);
  }

  /**
   * Set a supplier to delay the provision of the address to establish a connection to the Kafka
   * cluster. The supplier will be invoked only before starting the server
   *
   * @param bootstrapServers Kafka connection string supplier
   * @return {@link SchemaRegistryTestResource} for method chaining.
   * @throws IllegalStateException if method called after service has started.
   */
  @SuppressWarnings("unchecked")
  public T withBootstrapServers(Supplier<String> bootstrapServers) {
    validateServerExist("Cannot add bootstrap servers after service has started.");

    this.schemaRegistryProperties.addBootstrapServersSupplier(bootstrapServers);
    return (T) this;
  }

  /**
   * Set the port on which SchemaRegistry will listen for requests.
   *
   * @param port The port to listen on to.
   * @return {@link SchemaRegistryTestResource} for method chaining.
   */
  public T withPort(int port) {
    return withProperty(SchemaRegistryConfig.LISTENERS_CONFIG,
        SchemaRegistryProperties.singleListenerString(port));
  }

  /**
   * Set a random port on which SchemaRegistry will listen for requests.
   *
   * @return {@link SchemaRegistryTestResource} for method chaining.
   */
  public T withRandomPort() {
    return withPort(InstanceSpec.getRandomPort());
  }

  /**
   * Helper to allow overriding SchemaRegistry properties. Can only be called prior to
   * the service being started.
   *
   * @param name  SchemaRegistry configuration property name.
   * @param value Value to set for the configuration property.
   * @return {@link SchemaRegistryTestResource} instance for method chaining.
   * @throws IllegalStateException if method called after service has started.
   */
  @SuppressWarnings("unchecked")
  public T withProperty(String name, Object value) {
    validateServerExist("Cannot add properties after service has started.");

    schemaRegistryProperties.addProperty(name, value);
    return (T) this;
  }

  /**
   * Create and start SchemaRegistry instances.
   *
   * @throws Exception on startup errors.
   */
  public void start() throws Exception {
    log.info("Starting schema-registry test server");

    validateServerExist("Schema-registry test server already exists!");

    SchemaRegistryConfig config = schemaRegistryProperties.schemaRegistryConfig();
    SchemaRegistryRestApplication app = new SchemaRegistryRestApplication(config);

    server = app.createServer();
    server.start();
  }

  /**
   * Closes the internal server. Failing to call this at the end of your tests will likely
   * result in leaking instances.
   *
   * @throws Exception on stopping errors.
   */
  public void shutdown() throws Exception {
    log.info("Shutting down schema-registry test server");

    validateServerDoesNotExist("Schema-registry test server does not exist yet!");

    server.stop();
  }

  /**
   * String to configure {@link io.confluent.kafka.schemaregistry.client.SchemaRegistryClient}.
   *
   * @return Connect string to use for
   * {@link io.confluent.kafka.schemaregistry.client.SchemaRegistryClient}.
   */
  public String schemaRegistryUrl() {
    return schemaRegistryProperties.url();
  }

  /**
   * {@link SchemaRegistryTestUtils} is an helper class for interacting with the running
   * SchemaRegistry.
   *
   * @return {@link SchemaRegistryTestUtils} configured to operate with the running SchemaRegistry.
   */
  public SchemaRegistryTestUtils schemaRegistryTestUtils() {
    validateServerDoesNotExist("Cannot access SchemaRegistryTestUtils before "
        + "Schema-registry has been started.");
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
