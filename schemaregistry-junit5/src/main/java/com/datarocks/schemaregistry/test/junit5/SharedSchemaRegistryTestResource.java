package com.datarocks.schemaregistry.test.junit5;

import com.datarocks.schemaregistry.test.SchemaRegistryTestResource;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Properties;

public class SharedSchemaRegistryTestResource
        extends SchemaRegistryTestResource<SharedSchemaRegistryTestResource>
        implements BeforeAllCallback, AfterAllCallback {

    public SharedSchemaRegistryTestResource() {
        super();
    }

    public SharedSchemaRegistryTestResource(Properties properties) {
        super(properties);
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        start();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        shutdown();
    }
}
