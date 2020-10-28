package com.datarocks.schemaregistry.test;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SchemaRegistryTestUtils {

    private static final int DEFAULT_CACHE_REST_CLIENT_SIZE = 1000;

    private final SchemaRegistryProperties properties;

    public SchemaRegistryClient schemaRegistryClient() {
        return schemaRegistryClient(DEFAULT_CACHE_REST_CLIENT_SIZE);
    }

    public SchemaRegistryClient schemaRegistryClient(int cacheSizePerSubject) {
        return new CachedSchemaRegistryClient(properties.url(), cacheSizePerSubject);
    }
}
