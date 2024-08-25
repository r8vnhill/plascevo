/*
 * Copyright (c) 2024, Ignacio Slater M.
 * 2-Clause BSD License.
 */

package cl.ravenhill.plascevo
package property

/**
 * Represents a source of randomness, encapsulating a random number generator and its seed.
 *
 * The `RandomSource` case class combines a random number generator (`scala.util.Random`) with its corresponding seed.
 * This is useful in contexts where deterministic reproducibility is important, such as in property-based testing or
 * simulations. By storing both the `Random` instance and the seed, you can recreate the exact same sequence of random
 * numbers later by reinitializing the generator with the same seed.
 *
 * @param random The instance of `scala.util.Random` used for generating random numbers.
 * @param seed   The seed value used to initialize the `random` generator. This seed ensures that the random number
 *               sequence generated by `random` can be reproduced.
 */
case class RandomSource(random: scala.util.Random, seed: Long)

object RandomSource {
    def seeded(seed: Long): RandomSource = ???
}
