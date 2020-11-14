package com.datarocks.schemaregistry.test;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import lombok.RequiredArgsConstructor;

/**
 * A collection of helper function to interact with the running SchemaRegistry server.
 */
@RequiredArgsConstructor
public class SchemaRegistryTestUtils {

  private static final int DEFAULT_CACHE_REST_CLIENT_SIZE = 1000;

  private final SchemaRegistryProperties properties;

  /**
   * Return a {@link SchemaRegistryClient} ready to be used against the running SchemaRegistry.
   *
   * @return {@link SchemaRegistryClient} ready to be used
   */
  public SchemaRegistryClient schemaRegistryClient() {
    return schemaRegistryClient(DEFAULT_CACHE_REST_CLIENT_SIZE);
  }

  /**
   * Return a {@link SchemaRegistryClient} ready to be used against the running SchemaRegistry.
   *
   * @param cacheSizePerSubject size of internal client cache
   * @return {@link SchemaRegistryClient} ready to be used
   */
  public SchemaRegistryClient schemaRegistryClient(int cacheSizePerSubject) {
    return new CachedSchemaRegistryClient(properties.url(), cacheSizePerSubject);
  }
}
