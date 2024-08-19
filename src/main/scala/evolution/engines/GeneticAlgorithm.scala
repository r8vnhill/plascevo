package cl.ravenhill.plascevo
package evolution.engines

import evolution.config.*
import evolution.states.GeneticEvolutionState
import genetics.{Genotype, GenotypeBuilder}
import genetics.genes.Gene

case class GeneticAlgorithm[T, G <: Gene[T, G]](
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

object GeneticAlgorithm {
    def of[T, G <: Gene[T, G]](
        fitnessFunction: Genotype[T, G] => Double,
        genotypeBuilder: GenotypeBuilder[T, G]
    ): GeneticAlgorithmBuilder[T, G] = GeneticAlgorithmBuilder(
        fitnessFunction,
        genotypeBuilder
    )
}
