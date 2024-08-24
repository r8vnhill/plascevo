package cl.ravenhill.composerr

import exceptions.CompositeException

/** Enforces the contract defined within the provided builder.
 *
 * This method applies the specified contract to enforce validation rules. If any of the validation rules fail, a
 * [[CompositeException]] containing all the failures is thrown.
 *
 * <h2>Example: Enforcing a contract</h2>
 * {{{
 *  case class Person(name: String, age: Int) {
 *      constrained {
 *          "Name must not be empty" | { name mustNot BeEmpty }
 *          "Age must be greater than 0" | { age must BePositive }
 *      }
 *  }
 * }}}
 *
 * In this example, the `Person` case class defines a contract within its constructor using the
 * `constrained` method. It enforces that the `name` must not be empty and the `age` must be greater than 0.
 * If these conditions are not met, a [[CompositeException]] is thrown, encapsulating all the validation errors.
 *
 * @param builder The builder that contains the contract logic to be enforced.
 * @throws CompositeException If the contract is not fulfilled and there are validation failures.
 */
inline def constrained(builder: ComposerrScope ?=> Unit): Unit = {
    val scope = new ComposerrScope()
    builder(using scope)
    scope.failures match {
        case Nil => // No failures, do nothing
        case errors => throw CompositeException(errors)
    }
}

extension[T] (value: T) {
    def constrainedTo(builder: ComposerrScope ?=> Unit): T = {
        val scope = new ComposerrScope()
        builder(using scope)
        scope.failures match {
            case Nil => value
            case errors => throw CompositeException(errors)
        }
    }
}
