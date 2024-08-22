package cl.ravenhill.composerr
package config

/** Trait representing the configuration options for a system that supports skipping checks and enabling
  * short-circuiting behavior.
  *
  * Implementing classes or instances can modify these properties to influence the behavior of the system.
  *
  * <h2>Example 1: Default Configuration</h2>
  *
  * By default, both `skipChecks` and `shortCircuit` are set to `false`.
  *
  * @example
  * {{{
  * val config = new Configuration {}
  * println(config.skipChecks)   // false
  * println(config.shortCircuit) // false
  * }}}
  *
  * <h2>Example 2: Custom Configuration</h2>
  *
  * You can create a custom configuration by setting these properties:
  * @example
  * {{{
  * val config = new Configuration {
  *   skipChecks = true
  *   shortCircuit = true
  * }
  * println(config.skipChecks)   // true
  * println(config.shortCircuit) // true
  * }}}
  */
trait ComposerrConfiguration {
  /** A flag indicating whether checks should be skipped. */
  var skipChecks: Boolean = false

  /** A flag to stop checking constraints after the first failure. */
  var shortCircuit: Boolean = false
}
