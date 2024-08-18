package cl.ravenhill.plascevo
package evolution.engines

import evolution.config.*
import evolution.states.GeneticEvolutionState
import genetics.Genotype
import genetics.genes.Gene

class GeneticAlgorithm[T, G <: Gene[T, G]](
    populationConfiguration: GeneticPopulationConfiguration[T, G],
    selectionConfiguration: SelectionConfiguration[T, G, Genotype[T, G]],
    alterationConfiguration: AlterationConfiguration[T, G, Genotype[T, G]],
    evolutionConfiguration: EvolutionConfiguration[T, G, Genotype[T, G], GeneticEvolutionState[T, G]]
) extends AbstractGeneBasedEvolutionaryAlgorithm(
    populationConfiguration,
    selectionConfiguration,
    evolutionConfiguration
) {
    override protected def iterateGeneration(
        state: GeneticEvolutionState[T, G]
    ): GeneticEvolutionState[T, G] = ???
}
