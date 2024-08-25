package cl.ravenhill.composerr

import exceptions.CompositeException

object Constrained {
    
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
    
    extension [T](value: T) {
        /**
         * Extension method that applies a set of constraints to a value within a `ComposerrScope`.
         *
         * The `constrainedTo` extension method allows you to define and apply constraints to a value of type `T` within
         * a `ComposerrScope`. If the constraints are satisfied, the original value is returned. If any constraints are
         * violated, a `CompositeException` is thrown, containing all the errors that occurred during the validation.
         *
         * @param builder A block that defines the constraints within the context of a `ComposerrScope`. The scope is
         *                implicitly passed to the builder, allowing you to use constraint-related operations within it.
         * @return The original value if all constraints are satisfied.
         * @throws CompositeException if any constraints are violated. The exception will contain all the errors that
         *                            occurred during the validation.
         * @example
         * {{{
         * val value = 5
         * val constrainedValue = value.constrainedTo {
         *     "Value must be positive" ~ (
         *         value must BePositive,
         *         IntConstraintException(_)
         *     )
         *     "Value must be less than 10" ~ (
         *         value must BeInRange(0, 10),
         *         IntConstraintException(_)
         *     )
         * }
         * // `constrainedValue` will be equal to `value` if both constraints are satisfied.
         * // If either constraint fails, a `CompositeException` is thrown.
         * }}}
         */
        def constrainedTo(builder: ComposerrScope ?=> Unit): T = {
            val scope = new ComposerrScope()
            builder(using scope)
            scope.failures match {
                case Nil => value
                case errors => throw CompositeException(errors)
            }
        }
    }
}
