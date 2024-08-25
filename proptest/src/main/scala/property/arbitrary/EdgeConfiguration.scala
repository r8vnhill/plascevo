/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property.arbitrary

import property.PropertyTesting

import cl.ravenhill.composerr.Constrained.constrained
import cl.ravenhill.composerr.constraints.doubles.BeInRange

/**
 * Configuration for generating edge cases during property-based testing.
 *
 * The `EdgeConfig` case class provides settings for controlling the probability of generating edge cases during
 * property-based testing. Edge cases are extreme or boundary values that are often critical for identifying potential
 * bugs in code. This configuration allows users to adjust how frequently these edge cases are generated.
 *
 * @param edgeCasesGenerationProbability The probability that an edge case will be generated during a test run. This
 *                                       value must be between `0` and `1`, where `0` means no edge cases will be
 *                                       generated, and `1` means that edge cases will be generated every time.
 *                                       Defaults to the value specified by
 *                                       [[PropertyTesting.defaultEdgeCasesGenerationProbability]]. This ensures a
 *                                       balance between regular cases and edge cases during testing.
 *
 * @throws CompositeException If the probability is not within the valid range of 0 to 1. This is enforced by the
 *                            constraint within the case class, ensuring that the configuration is always valid.
 * @throws DoubleConstraintException If the edge case generation probability is outside the range [0, 1]; wrapped in a
 *                                   `CompositeException`.
 */
case class EdgeConfiguration(
    edgeCasesGenerationProbability: Double = PropertyTesting.defaultEdgeCasesGenerationProbability
) {
    constrained {
        "Edge case generation probability should be between 0 and 1" | {
            edgeCasesGenerationProbability must BeInRange(0, 1)
        }
    }
}

/**
 * The EdgeConfig class represents the configuration for edges in a graph.
 *
 * The EdgeConfig object provides a convenient method to create a default edge configuration with the default edge case generation probability.
 */
object EdgeConfiguration {
    /**
     * Creates a default edge configuration with the default edge case generation probability.
     *
     * @return A new `EdgeConfig` instance with the default edge case generation probability.
     */
    def default: EdgeConfiguration = EdgeConfiguration()
}