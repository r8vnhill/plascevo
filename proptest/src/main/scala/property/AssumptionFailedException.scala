/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property

/**
 * An exception that indicates a failed assumption in property-based testing.
 *
 * The `AssumptionFailedException` class is a specialized exception used in the context of property-based testing to
 * signal that an assumption made during the test has failed. This exception is typically used to skip the current test
 * case without marking it as a failure. It allows the test framework to distinguish between actual test failures and
 * cases where the test logic has determined that the current input does not meet the assumptions necessary for the test
 * to be valid.
 * 
 * @note This exception is intended to be caught and handled by the property testing framework, which will skip the
 *       current test case without treating it as a failure.
 */
final class AssumptionFailedException extends Exception()
