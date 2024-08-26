/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property

/**
 * A case class representing the result of a property-based test.
 *
 * `PropertyResult` encapsulates the key details of a property-based test, including the inputs used, the number
 * of attempts, successes, and failures, as well as the classifications (labels) applied during the test.
 *
 * @param inputs    A sequence of strings representing the inputs used in the property test. Each string typically
 *                  corresponds to an identifier for a specific input case.
 * @param seed      The seed value used for random number generation during the property test. This seed allows the test
 *                  to be reproduced exactly.
 * @param attempts  The total number of attempts made during the test. This includes all executions of the test case,
 *                  whether they resulted in success or failure.
 * @param successes The number of successful test executions, where the property was satisfied.
 * @param failures  The number of failed test executions, where the property was violated.
 * @param labels    A map representing the classifications (labels) applied to the test results. The outer map's keys
 *                  are label names, each associated with another map that contains the specific label values and their
 *                  counts across the test cases.
 */
case class PropertyResult(
    inputs: Seq[String],
    seed: Long,
    attempts: Int,
    successes: Int,
    failures: Int,
    labels: Map[String, Map[String, Int]]
)
