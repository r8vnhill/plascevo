package cl.ravenhill.composerr
package config

/**
 * Provides a context for managing and applying configuration settings within the Composerr library.
 *
 * The `ConfigurationContext` encapsulates an instance of [[ComposerrConfiguration]], offering methods to get and set the
 * `skipChecks` and `shortCircuit` properties. This allows users to fluently manage configuration settings and control
 * the behavior of validation processes.
 *
 * <h2>Usage:</h2>
 *
 * <h3>Example 1: Accessing Configuration Values</h3>
 *
 * @example
 * {{{
 * val ctx = ConfigurationContext(new Configuration {})
 * println(ctx.skipChecks)   // false
 * println(ctx.shortCircuit) // false
 * }}}
 *
 * <h3>Example 2: Modifying Configuration Values</h3>
 * @example
 * {{{
 * val ctx = ConfigurationContext(new Configuration {})
 * ctx.setSkipChecks(true)
 * ctx.setShortCircuit(true)
 * println(ctx.skipChecks)   // true
 * println(ctx.shortCircuit) // true
 * }}}
 */
case class ConfigurationContext(configuration: ComposerrConfiguration) {

  /**
   * Retrieves the current value of the `skipChecks` property from the configuration.
   *
   * @return `true` if checks are skipped, `false` otherwise.
   */
  def skipChecks: Boolean = configuration.skipChecks

  /**
   * Retrieves the current value of the `shortCircuit` property from the configuration.
   *
   * @return `true` if short-circuiting is enabled, `false` otherwise.
   */
  def shortCircuit: Boolean = configuration.shortCircuit

  /**
   * Sets the `skipChecks` property in the configuration and returns the updated context.
   *
   * @param value The new value for `skipChecks`.
   * @return The updated [[ConfigurationContext]] with the new setting.
   */
  def setSkipChecks(value: Boolean): ConfigurationContext = {
    configuration.skipChecks = value
    this
  }

  /**
   * Sets the `shortCircuit` property in the configuration and returns the updated context.
   *
   * @param value The new value for `shortCircuit`.
   * @return The updated [[ConfigurationContext]] with the new setting.
   */
  def setShortCircuit(value: Boolean): ConfigurationContext = {
    configuration.shortCircuit = value
    this
  }
}

/**
 * Companion object for [[ConfigurationContext]], providing a default implicit instance.
 *
 * This implicit [[ConfigurationContext]] can be used to access and modify the configuration
 * without needing to explicitly pass the context around.
 *
 * @example
 * {{{
 * import cl.ravenhill.composerr.config.ConfigurationContext.given
 *
 * println(summon[ConfigurationContext].skipChecks)   // Accessing skipChecks via the implicit context
 * }}}
 */
object ConfigurationContext {
  given ConfigurationContext = ConfigurationContext(new ComposerrConfiguration {})
}
