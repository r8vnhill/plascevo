package cl.ravenhill.composerr
package config

/**
 * A DSL (Domain-Specific Language) for configuring validation settings within the Composerr library.
 *
 * The `ConfigurationDsl` object provides a set of methods to interact with the [[ConfigurationContext]], allowing users
 * to fluently set configuration properties such as `skipChecks` and `shortCircuit`.
 *
 * <h2>Usage:</h2>
 *
 * <h3>Example 1: Accessing Configuration</h3>
 *
 * This example demonstrates how to access the current configuration using the DSL:
 *
 * @example
 * {{{
 * import cl.ravenhill.config.{ConfigurationContext, ConfigurationDsl}
 *
 * given ctx: ConfigurationContext = ConfigurationContext(new Configuration {})
 * val config = ConfigurationDsl.config
 * println(config.skipChecks)   // false
 * println(config.shortCircuit) // false
 * }}}
 *
 * <h3>Example 2: Modifying Configuration</h3>
 *
 * This example shows how to modify the configuration using the DSL:
 *
 * @example
 * {{{
 * import cl.ravenhill.config.{ConfigurationContext, ConfigurationDsl}
 *
 * given ctx: ConfigurationContext = ConfigurationContext(new Configuration {})
 * ConfigurationDsl.skipChecks(true)
 * ConfigurationDsl.shortCircuit(true)
 * println(ctx.skipChecks)   // true
 * println(ctx.shortCircuit) // true
 * }}}
 */
object ConfigurationDsl {

  /**
   * Retrieves the current configuration from the given [[ConfigurationContext]].
   *
   * @param ctx The implicit [[ConfigurationContext]] used to access the configuration.
   * @return The current [[ComposerrConfiguration]] instance.
   */
  def configuration(init: ConfigurationContext ?=> ConfigurationContext)(using ctx: ConfigurationContext): ComposerrConfiguration = {
    init
    ctx.configuration
  }
    

  /**
   * Sets the `skipChecks` property in the given [[ConfigurationContext]] and returns the updated context.
   *
   * @param value The new value for `skipChecks`.
   * @param ctx The implicit [[ConfigurationContext]] used to set the configuration property.
   * @return The updated [[ConfigurationContext]] with the new setting.
   */
  def skipChecks(value: Boolean)(using ctx: ConfigurationContext): ConfigurationContext =
    ctx.setSkipChecks(value)

  /**
   * Sets the `shortCircuit` property in the given [[ConfigurationContext]] and returns the updated context.
   *
   * @param value The new value for `shortCircuit`.
   * @param ctx The implicit [[ConfigurationContext]] used to set the configuration property.
   * @return The updated [[ConfigurationContext]] with the new setting.
   */
  def shortCircuit(value: Boolean)(using ctx: ConfigurationContext): ConfigurationContext =
    ctx.setShortCircuit(value)
}
