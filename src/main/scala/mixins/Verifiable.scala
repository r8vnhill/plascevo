package cl.ravenhill.plascevo
package mixins

/** A trait representing an entity that can be verified.
 *
 * The `Verifiable` trait provides a mechanism for implementing objects that require verification. By default,
 * the `verify` method returns `true`, indicating that the entity is considered valid unless overridden.
 *
 * <h2>Usage:</h2>
 * Implement this trait in classes or objects that require a verification step. Override the `verify` method to 
 * provide custom verification logic.
 *
 * <h3>Example:</h3>
 * @example
 * {{{
 * trait MyVerifiable extends Verifiable {
 *   override def verify(): Boolean = {
 *     // Custom verification logic
 *     true
 *   }
 * }
 * }}}
 *
 * In this example, `MyVerifiable` extends the `Verifiable` trait and overrides the `verify` method with custom logic.
 *
 * @return A Boolean value indicating whether the verification was successful. Defaults to `true`.
 */
trait Verifiable:
    def verify(): Boolean = true
