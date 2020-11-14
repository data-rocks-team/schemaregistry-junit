package com.datarocks.schemaregistry.test.junit4;

import com.datarocks.schemaregistry.test.SchemaRegistryTestResource;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.Properties;

/**
 * Creates and stands up an internal test SchemaRegistry to be shared across test cases within the
 * same test class.
 *
 * <p>Example within your Test class.
 *{@code
 * &#064;ClassRule
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
    implements TestRule {

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
