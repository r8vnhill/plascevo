package cl.ravenhill.plascevo
package operators.alteration

import operators.Operator
import repr.{Feature, Representation}

/** Represents an alterer in an evolutionary algorithm.
 *
 * An `Alterer` is a specialized operator used in evolutionary algorithms to modify or "alter" individuals within a
 * population. This process typically involves operations such as mutation or crossover, which introduce variations
 * in the population to explore the search space more effectively. The `Alterer` trait extends the [[Operator]] trait
 * and provides the foundation for implementing these variation mechanisms.
 *
 * @tparam T The type of value stored by the features within the individual.
 * @tparam F The type of feature contained in the representation, which must extend [[Feature]].
 * @tparam R The type of representation used by the individual, which must extend [[Representation]].
 *
 * @example
 * {{{
 * // Example implementation of a mutation alterer that flips bits in a binary string
 * class BitFlipMutator extends Alterer[Boolean, SimpleGene, SimpleRepresentation] {
 *   override def apply[S <: EvolutionState[Boolean, SimpleGene, SimpleRepresentation, S]](
 *     state: S,
 *     outputSize: Int,
 *     buildState: Seq[Individual[Boolean, SimpleGene, SimpleRepresentation]] => S
 *   )(using random: Random, equalityThreshold: Double): S = {
 *     // Mutation logic here
 *   }
 * }
 * }}}
 */
trait Alterer[T, F <: Feature[T, F], R <: Representation[T, F]] extends Operator[T, F, R]
