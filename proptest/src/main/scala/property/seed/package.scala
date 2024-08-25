/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property

import scala.util.Random

package object seed {
    /**
     * Creates a `RandomSource` based on the provided property test configuration.
     *
     * The `createRandom` method generates a `RandomSource` using the seed from the given `PropTestConfig`. If a seed is
     * specified in the configuration, a new `RandomSource` is created using that seed. If no seed is provided, a
     * default `RandomSource` is returned. This allows for both deterministic (seeded) and non-deterministic
     * (non-seeded) random number generation, depending on the configuration.
     *
     * @param config The implicit `PropTestConfig` instance that contains configuration details for property testing, 
     *               including the optional seed value for random number generation.
     * @return A `RandomSource` initialized with the specified seed from the configuration, or a default `RandomSource`
     *         if no seed is provided.
     */
    def createRandom(using config: PropTestConfig): RandomSource = config.seed
        .map(seed => new RandomSource(Random(seed), seed))
        .getOrElse(RandomSource.default)
}
