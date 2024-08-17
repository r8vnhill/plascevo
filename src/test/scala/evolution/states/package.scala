package cl.ravenhill.plascevo
package evolution

import ranking.IndividualRanker
import repr.{Feature, Representation}

package object states:
    /** Represents the state of an evolutionary algorithm at a given generation.
     *
     * The `SimpleState` case class encapsulates the current population of individuals, the ranker used to evaluate and 
     * compare individuals, and the current generation number within an evolutionary algorithm. It extends
     * `EvolutionState`, providing a concrete implementation for managing the state of the algorithm as it evolves.
     *
     * @param population The current population of individuals in the evolutionary algorithm.
     * @param ranker     The ranker used to evaluate and compare the fitness of individuals within the population.
     * @param generation The current generation number in the evolutionary process.
     * @tparam T The type of value held by the features in the individuals.
     * @tparam F The type of the feature, which must extend `Feature[T, F]`.
     * @tparam R The type of the representation, which must extend `Representation[T, F]`.
     */
    case class SimpleState[T, F <: Feature[T, F], R <: Representation[T, F]](
        population: Population[T, F, R],
        ranker: IndividualRanker[T, F, R],
        generation: Int
    ) extends EvolutionState[T, F, R]:

        /** Creates a new `SimpleState` instance with an updated population.
         *
         * This method returns a copy of the current state with the population replaced by the provided list of
         * individuals. The other attributes, such as the ranker and generation number, remain unchanged.
         *
         * @param population The new population of individuals.
         * @return A new `SimpleState` instance with the updated population.
         */
        def withPopulation(population: Population[T, F, R]): SimpleState[T, F, R] = copy(population = population)

