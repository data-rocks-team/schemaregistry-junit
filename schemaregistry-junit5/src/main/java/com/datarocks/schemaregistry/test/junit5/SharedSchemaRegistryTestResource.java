package com.datarocks.schemaregistry.test.junit5;

import com.datarocks.schemaregistry.test.SchemaRegistryTestResource;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Properties;

/**
 * Creates and stands up an internal test SchemaRegistry to be shared across test cases within the
 * same test class.
 *
 * <p>Example within your Test class.
 * {@code
 * &#064;RegisterExtension
 * public static final SharedSchemaRegistryTestResource sharedSchemaRegistryTestResource =
 * new SharedSchemaRegistryTestResource()
 * .withBootstrapServers("localhost:9092");
 * }
 *
 * <p>Within your test case method:
 * {@code schemaRegistry.schemaRegistryTestUtils().schemaRegistryClient()...}
 */
public class SharedSchemaRegistryTestResource
    extends SchemaRegistryTestResource<SharedSchemaRegistryTestResource>
    implements BeforeAllCallback, AfterAllCallback {

  /**
   * Default constructor.
   */
  public SharedSchemaRegistryTestResource() {
    super();
  }

  /**
   * Constructor allowing passing additional SchemaRegistry properties.
   *
   * @param properties properties for SchemaRegistry server.
   */
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
