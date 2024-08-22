package cl.ravenhill.plascevo
package evolution.engines

import evolution.EvolutionInterceptor
import evolution.config.*
import evolution.executors.evaluation.EvaluationExecutor
import evolution.states.GeneticEvolutionState
import genetics.genes.Gene
import genetics.{Genotype, GenotypeBuilder}
import listeners.EvolutionListener
import operators.alteration.Alterer
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

    val interceptor: EvolutionInterceptor[T, G, Genotype[T, G], GeneticEvolutionState[T, G]] =
        evolutionConfiguration.interceptor

    val genotypeBuilder: GenotypeBuilder[T, G] = populationConfiguration.genotypeBuilder

    val evaluator: EvaluationExecutor[T, G, Genotype[T, G], GeneticEvolutionState[T, G]] =
        evolutionConfiguration.evaluator

    val populationSize: Int = populationConfiguration.populationSize
    
    val survivalRate: Double = selectionConfiguration.survivalRate

    val parentSelector: Selector[T, G, Genotype[T, G]] = selectionConfiguration.parentSelector

    val offspringSelector: Selector[T, G, Genotype[T, G]] = selectionConfiguration.offspringSelector

    val alterers: Seq[Alterer[T, G, Genotype[T, G]]] = alterationConfiguration.alterers
}
