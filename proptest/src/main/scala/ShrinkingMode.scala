/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall

/**
 * Represents the different modes of shrinking in property-based testing.
 *
 * `ShrinkingMode` is a sealed trait that defines the behavior of how shrinking should be applied during property-based
 * testing. Shrinking is the process of simplifying failing test cases to find the minimal failing input.
 *
 * The trait has three possible implementations:
 * - `NoShrinking`: Disables shrinking.
 * - `UnboundedShrinking`: Allows unlimited shrinking attempts.
 * - `BoundedShrinking`: Limits the number of shrinking attempts to a specified maximum.
 */
sealed trait ShrinkingMode {

    /**
     * Determines whether shrinking should continue based on the current count of shrinking attempts.
     *
     * @param count The current number of shrinking attempts.
     * @return `true` if shrinking should continue, `false` otherwise.
     */
    def isShrinking(count: Int): Boolean = this match
        case NoShrinking => false
        case UnboundedShrinking => true
        case BoundedShrinking(maxSize) => count < maxSize
}

/**
 * Disables shrinking.
 *
 * `NoShrinking` is a singleton object that represents a mode where shrinking is completely disabled. When this mode is
 * used, no shrinking will be attempted, regardless of the number of failing test cases.
 */
case object NoShrinking extends ShrinkingMode

/**
 * Allows unlimited shrinking attempts.
 *
 * `UnboundedShrinking` is a singleton object that represents a mode where an unlimited number of shrinking attempts
 * are allowed. This mode will continue shrinking as long as the test case continues to fail.
 */
case object UnboundedShrinking extends ShrinkingMode

/**
 * Limits the number of shrinking attempts to a specified maximum.
 *
 * `BoundedShrinking` is a case class that represents a mode where shrinking is limited to a specified maximum number of
 * attempts. Once the `maxSize` limit is reached, no further shrinking will be attempted.
 *
 * @param maxSize The maximum number of shrinking attempts allowed.
 */
case class BoundedShrinking(maxSize: Int) extends ShrinkingMode
