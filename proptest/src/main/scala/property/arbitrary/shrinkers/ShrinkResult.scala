/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.arbitrary.shrinkers

/**
 * Represents the result of a shrinking operation in property-based testing.
 *
 * The `ShrinkResult` case class encapsulates the outcome of attempting to shrink a test case value in
 * property-based testing. Shrinking is a process where a failing test case is reduced to its simplest form to help
 * identify the minimal input that causes the test to fail.
 *
 * @param initial The original value that was being tested before shrinking.
 * @param shrink The shrunken value that resulted from the shrinking operation.
 * @param cause An optional `Throwable` that provides information about why the shrinking occurred. This is typically
 *              the exception or failure that triggered the shrink. If no specific cause is available, this field may be
 *              `None`.
 *
 * @tparam T The type of the value being tested and shrunk.
 */
case class ShrinkResult[+T](initial: T, shrink: T, cause: Option[Throwable])
