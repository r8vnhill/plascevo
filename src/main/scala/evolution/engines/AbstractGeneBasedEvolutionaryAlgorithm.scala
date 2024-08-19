package cl.ravenhill.plascevo
package evolution.engines

import evolution.config.*
import evolution.states.GeneticEvolutionState
import genetics.genes.Gene
import genetics.{Genotype, GenotypeBuilder}
import listeners.EvolutionListener
import operators.selection.Selector
import ranking.IndividualRanker

abstract class AbstractGeneBasedEvolutionaryAlgorithm[
    T,
    G <: Gene[T, G],
    L <: EvolutionListener[T, G, Genotype[T, G], GeneticEvolutionState[T, G]]
](
    populationConfiguration: GeneticPopulationConfiguration[T, G],
    selectionConfiguration: SelectionConfiguration[T, G, Genotype[T, G]],
    alterationConfiguration: AlterationConfiguration[T, G, Genotype[T, G]],
    evolutionConfiguration: EvolutionConfiguration[T, G, Genotype[T, G], GeneticEvolutionState[T, G], L]
) extends Evolver[T, G, Genotype[T, G], GeneticEvolutionState[T, G], L](evolutionConfiguration) {

    /** The selector used to choose parents for reproduction in the evolutionary process.
     *
     * This selector is derived from the selection configuration and is responsible for selecting individuals from the
     * current population to act as parents for generating the next generation.
     */
    val parentSelector: Selector[T, G, Genotype[T, G]] = selectionConfiguration.parentSelector

    /** The selector used to choose offspring for inclusion in the next generation.
     *
     * This selector is derived from the selection configuration and is responsible for selecting the best offspring to
     * include in the next generation, ensuring the continuation of the evolutionary process.
     */
    val offspringSelector: Selector[T, G, Genotype[T, G]] = selectionConfiguration.offspringSelector
}
