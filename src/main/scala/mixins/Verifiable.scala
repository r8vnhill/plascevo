package cl.ravenhill.plascevo
package mixins

/** Represents an entity that can be verified for correctness or validity.
 *
 * Implementing classes or traits can override the `verify` method to provide custom validation logic specific to the
 * entity they represent. This allows for flexible and consistent verification across different types of entities.
 */
trait Verifiable {

    /** Verifies the entity for correctness or validity.
     *
     * The `verify` method checks the validity or correctness of the entity. By default, it returns `true`, indicating
     * that the entity is considered valid. Implementing classes or traits can override this method to provide custom
     * verification logic.
     *
     * @return `true` if the entity is valid, `false` otherwise.
     */
    def verify(): Boolean = true
}
