package com.datarocks.schemaregistry.test.junit4;

import com.datarocks.schemaregistry.test.SchemaRegistryTestResource;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.Properties;

public class SharedSchemaRegistryTestResource
        extends SchemaRegistryTestResource<SharedSchemaRegistryTestResource>
        implements TestRule {

    public SharedSchemaRegistryTestResource() {
        super();
    }

    public SharedSchemaRegistryTestResource(Properties properties) {
        super(properties);
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                start();
                try {
                    base.evaluate();
                } finally {
                    shutdown();
                }
            }
        };
    }
}
