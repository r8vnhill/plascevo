package cl.ravenhill.plascevo
package evolution.states

import genetics.Genotype

import cl.ravenhill.plascevo.genetics.genes.Gene
import cl.ravenhill.plascevo.ranking.IndividualRanker

/** A case class that represents the state of a genetic algorithm's evolution at a specific generation.
 *
 * The `GeneticEvolutionState` class extends the `EvolutionState` trait, encapsulating the current generation number,
 * the ranker used to evaluate individuals, and the population of individuals within the genetic algorithm. This state
 * is used to track the progress of the evolutionary process.
 *
 * @param generation The current generation number in the evolutionary process. Must be non-negative.
 * @param ranker     The `IndividualRanker` used to evaluate and rank individuals within the population.
 * @param population The current population of individuals in the genetic algorithm.
 * @tparam T The type of value stored by the gene.
 * @tparam G The type of gene that the individuals' genotypes hold, which must extend [[Gene]].
 */
case class GeneticEvolutionState[T, G <: Gene[T, G]](
    override val generation: Int,
    override val ranker: IndividualRanker[T, G, Genotype[T, G]],
    override val population: Population[T, G, Genotype[T, G]]
) extends EvolutionState[T, G, Genotype[T, G], GeneticEvolutionState[T, G]] {

    require(generation >= 0, "Generation number must be non-negative.")

    override def withPopulation(
        newPopulation: Population[T, G, Genotype[T, G]]
    ): GeneticEvolutionState[T, G] = copy(population = newPopulation)
}

object GeneticEvolutionState {
    def empty[T, G <: Gene[T, G]](ranker: IndividualRanker[T, G, Genotype[T, G]]): GeneticEvolutionState[T, G] =
        GeneticEvolutionState(generation = 0, ranker, Seq.empty[Individual[T, G, Genotype[T, G]]])
}
