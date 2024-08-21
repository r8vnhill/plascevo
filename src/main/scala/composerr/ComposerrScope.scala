package cl.ravenhill.plascevo
package composerr

import composerr.constraints.Constraint

import cl.ravenhill.composerr.config.ComposerrConfiguration
import cl.ravenhill.composerr.exceptions.ConstraintException

import scala.annotation.targetName
import scala.util.Try

/**
 * A scope for managing and aggregating validation results.
 *
 * The `ComposerrScope` class provides a framework for executing and collecting the results of various validation
 * checks. It stores the results of these validations, allowing users to access both the overall results and any
 * failures that occurred. This class is particularly useful in scenarios where multiple validations are performed, and
 * the outcome needs to be aggregated.
 *
 * @constructor Creates a new `ComposerrScope` with a specified configuration.
 * @param configuration The configuration settings that control the behavior of the scope. If not provided, a default
 *                      configuration is used.
 * @example
 * {{{
 * val scope = new ComposerrScope()
 * // Assuming `StringScope` is used within this context to perform some validations
 *
 * // Access all results
 * val allResults = scope.results
 *
 * // Access only the failures
 * val failedValidations = scope.failures
 * }}}
 */
class ComposerrScope(val configuration: ComposerrConfiguration = new ComposerrConfiguration {}) {

    /**
     * A private buffer that accumulates the results of validation checks performed within this scope.
     *
     * The `_results` buffer stores `Try` objects representing the outcome of each validation.
     */
    private val _results = collection.mutable.ListBuffer.empty[Try[?]]

    /**
     * Returns a list of all validation results accumulated within this scope.
     *
     * The `results` method provides access to all the `Try` objects stored in the scope, representing the outcome
     * (success or failure) of each validation.
     *
     * @return A list of `Try` objects representing the validation results.
     */
    def results: List[Try[?]] = _results.toList

    /**
     * Returns a list of all exceptions corresponding to failed validations.
     *
     * The `failures` method filters the results to include only those that represent failures, and extracts the
     * associated exceptions. This allows users to easily access and handle all validation failures.
     *
     * @return A list of `Throwable` objects representing the exceptions from failed validations.
     */
    def failures: List[Throwable] = _results.collect { case t if t.isFailure => t.failed.get }.toList

    extension (value: String) {
        /** Defines a clause of a contract for string validation.
         *
         * This inline method simplifies the creation of a validation clause for a string value. It is designed to be used
         * within a DSL context, providing a declarative approach to define validation rules for strings. The method creates
         * a [[StringScope]] instance, allowing the caller to specify validation logic in a concise and readable manner.
         *
         * <h2>Usage:</h2>
         * The method is typically utilized to establish validation rules within a custom DSL. It allows for defining
         * various constraints and rules that a string should comply with.
         *
         * <h3>Example: Setting up a simple validation rule</h3>
         * {{{
         * "Username must not be empty" | { 
         *     username mustNot BeEmpty
         * }
         * }}}
         *
         * In this example, a validation rule is defined for the username. The rule specifies that the username must not be
         * empty. If the validation fails, a [[ConstraintException]] is thrown. The actual kind of exception thrown is
         * determined by the [[Constraint]] used in the validation rule. For example, if the validation rule uses the
         * [[BeEmpty]] constraint on a string, a [[StringConstraintException]] is thrown.
         *
         * @param predicate A lambda expression with a receiver of type [[StringScope]], where the validation logic is
         *                  defined.
         * @return A [[StringScope]] instance, providing the context and functionalities to define constraints and
         *         validation logic.
         */
        @targetName("in")
        inline def |(predicate: StringScope ?=> Unit): StringScope = {
            val scope = new StringScope(value)
            predicate(using scope)
            scope
        }

        /** Defines an infix operation to create a validation clause within a specified context.
         *
         * This inline infix method is used to create a validation clause for a string within a custom DSL. It allows the
         * caller to define validation logic and specify a custom exception generator to handle validation failures. The
         * method creates a [[StringScope]] instance, sets the provided exception generator, and applies the validation
         * logic within the scope.
         *
         * <h2>Usage:</h2>
         * This method is typically used in a DSL context to define validation rules with custom exception handling.
         *
         * <h3>Example:</h3>
         * {{{
         * "Username must not be empty" | (
         *    exceptionGenerator = (message: String) => new CustomException(message)
         * ) {
         *   username mustNot BeEmpty
         * }
         * }}}
         *
         * In this example, a validation rule is defined for the username, with a custom exception being thrown if
         * the validation fails. The rule specifies that the username must not be empty, and if it is, a custom
         * exception is generated using the provided `exceptionGenerator`.
         *
         * @param predicate          A lambda expression with a receiver of type [[StringScope]], where the validation logic is
         *                           defined. This is the core logic that specifies the constraints for the string.
         * @param exceptionGenerator A function that generates a custom [[ConstraintException]] when a validation
         *                           rule fails. This allows for custom exception handling based on the validation result.
         * @return A [[StringScope]] instance, with the specified validation logic and exception handling.
         */
        @targetName("in")
        inline infix def in(
            exceptionGenerator: String => ConstraintException,
        )(predicate: StringScope ?=> Unit): StringScope = {
            val scope = new StringScope(value)
            scope.exceptionGenerator = Some(exceptionGenerator)
            predicate(using scope)
            scope
        }

    }

    /**
     * A scope for validating string-related constraints within a specific context.
     *
     * The `StringScope` class provides a framework for applying and handling string-based constraints in a controlled
     * scope. It allows for the validation of strings against defined constraints, and provides mechanisms for generating
     * exceptions if validations fail. The scope is configurable, supporting both short-circuiting of validation failures
     * and custom exception generation.
     *
     * @constructor Creates a new `StringScope` with a specific validation context and message.
     * @param message A message that describes the context or the validation being performed.
     * @example
     * {{{
     * // Example of using `StringScope` to validate a string against a constraint:
     * val scope = new StringScope("Validation failed")
     * scope.exceptionGenerator = Some(msg => new ConstraintException(msg))
     *
     * import scope.must
     * import scope.mustNot
     * val input = "example"
     * input must new LengthConstraint(5)  // Assuming LengthConstraint is a predefined constraint
     * input mustNot new ContainsConstraint("!")  // Assuming ContainsConstraint is a predefined constraint
     * }}}
     */
    class StringScope(message: String) {

        /**
         * The outer scope of the `StringScope`, referring to the parent scope or context in which this `StringScope` is
         * defined.
         */
        val outerScope: ComposerrScope = ComposerrScope.this

        /**
         * An optional function that generates a custom exception based on a string message.
         * If provided, this function will be used to create exceptions when validations fail.
         */
        var exceptionGenerator: Option[String => ConstraintException] = None

        /**
         * Handles the validation logic by evaluating a predicate and managing the result based on the configuration.
         *
         * If the predicate evaluates to `false`, an exception is thrown or wrapped in a `Try` based on the scope's
         * configuration.
         *
         * @param predicate A condition that determines whether the validation passes or fails.
         * @return A `Try` wrapping the result of the validation. If the validation fails, the `Try` will contain the
         *         exception.
         */
        private def handleValidation[T](predicate: => Boolean)
            (using constraint: Constraint[T] = new Constraint[T] {
                override val validator: T => Boolean = _ => predicate
            }): Try[?] = {
            if (!predicate) {
                if (configuration.shortCircuit) {
                    throw exceptionGenerator.map(_.apply(message)).getOrElse(constraint.generateException(message))
                }
                Try(throw exceptionGenerator.map(_.apply(message)).getOrElse(constraint.generateException(message)))
            } else {
                Try(())
            }
        }

        /**
         * Extension methods for applying constraints to values within the `StringScope`.
         */
        extension [T](value: T) {

            /**
             * Validates that the value satisfies the provided constraint.
             *
             * @param constraint The constraint to be validated against the value.
             */
            infix def must[C <: Constraint[T]](constraint: C): Unit = {
                given Constraint[T] = constraint

                _results += handleValidation(constraint.validator(value)).map(_ => value)
            }

            /**
             * Validates that the value does not satisfy the provided constraint.
             *
             * @param constraint The constraint to be negated and validated against the value.
             */
            infix def mustNot[C <: Constraint[T]](constraint: C): Unit = {
                given Constraint[T] = constraint
    
                _results += handleValidation(!constraint.validator(value)).map(_ => value)
            }
        }

        /**
         * Validates a custom predicate within the `StringScope`.
         *
         * @param predicate A condition to be evaluated as part of the validation.
         */
        def constraint(predicate: => Boolean): Unit = {
            _results += handleValidation(predicate)
        }

        /**
         * Returns a string representation of the `StringScope`, including the message provided.
         *
         * @return A string representation of this `StringScope`.
         */
        override def toString: String = s"StringScope($message)"
    }
}
