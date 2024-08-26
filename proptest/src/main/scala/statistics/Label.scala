/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package munit.checkall
package statistics

/**
 * A case class representing a label used to categorize or classify different scenarios within a property-based test.
 *
 * The `Label` class is used to assign a specific identifier or name to a set of test cases or outcomes, allowing them
 * to be grouped and analyzed based on common characteristics. Labels help in organizing and understanding the results
 * of property-based testing by providing meaningful tags to different test scenarios.
 *
 * @param value The string value of the label, representing the name or identifier used to categorize a specific set
 *              of test cases or outcomes.
 */
case class Label(value: String)
