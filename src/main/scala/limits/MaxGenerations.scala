package cl.ravenhill.plascevo
package limits

import evolution.states.EvolutionState
import genetics.genes.Gene
import listeners.EvolutionListener
import repr.{Feature, Representation}

/** A limit condition based on the maximum number of generations in an evolutionary algorithm.
 *
 * The `MaxGenerations` case class defines a limit for an evolutionary algorithm based on the number of generations.
 * Once the specified number of generations is reached, the evolutionary process will stop. This limit is enforced by
 * a `MaxGenerationListener`, which tracks the number of completed generations.
 *
 * @param generations The maximum number of generations to run the evolutionary algorithm.
 * @tparam T The type of value stored by the feature.
 * @tparam G The kind of feature stored in the representation, which must implement [[Feature]].
 * @tparam R The type of representation used by the individual, which must implement [[Representation]].
 * @tparam S The type of evolutionary state being managed, which must implement [[EvolutionState]].
 */
case class MaxGenerations[T, G <: Gene[T, G], R <: Representation[T, G], S <: EvolutionState[T, G, R, S]](
    generations: Int
) extends Limit[T, G, R, S, EvolutionListener[T, G, R, S]](
    new MaxGenerationListener[T, G, R, S](),
    { (*, state) => state.generation >= generations }
)

/** A private listener that tracks the number of generations completed in the evolutionary algorithm.
 *
 * The `MaxGenerationListener` class is a private class that listens to the evolutionary process and increments its
 * generation count after each generation. This listener is used by the `MaxGenerations` limit to determine when the
 * maximum number of generations has been reached.
 *
 * @tparam T The type of value stored by the feature.
 * @tparam F The kind of feature stored in the representation, which must implement [[Feature]].
 * @tparam R The type of representation used by the individual, which must implement [[Representation]].
 * @tparam S The type of evolutionary state being managed, which must implement [[EvolutionState]].
 */
private class MaxGenerationListener[T, F <: Feature[T, F], R <: Representation[T, F], S <: EvolutionState[T, F, R, S]]
    extends EvolutionListener[T, F, R, S]
