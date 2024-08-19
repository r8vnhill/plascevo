package cl.ravenhill.plascevo
package evolution.engines

import evolution.config.*
import evolution.states.GeneticEvolutionState
import genetics.genes.Gene
import genetics.{Genotype, GenotypeBuilder}
import operators.selection.Selector
import ranking.{FitnessMaxRanker, IndividualRanker}

case class GeneticAlgorithm[T, G <: Gene[T, G]](
    private val fitnessFunction: Genotype[T, G] => Double,
    override protected val genotypeBuilder: GenotypeBuilder[T, G],
    override protected val ranker: IndividualRanker[T, G, Genotype[T, G]] = FitnessMaxRanker[T, G, Genotype[T, G]](),
    private val parentSelector: Selector[T, G, Genotype[T, G]],
    private val survivorSelector: Selector[T, G, Genotype[T, G]]
)(
    using override protected val populationSize: PopulationSize,
    override protected val survivalRate: SurvivalRate,
    initialState: GeneticEvolutionState[T, G] = GeneticEvolutionState.empty(ranker)
) extends AbstractGeneBasedEvolutionaryAlgorithm(ranker, genotypeBuilder) {
    override protected def iterateGeneration(
        state: GeneticEvolutionState[T, G]
    ): GeneticEvolutionState[T, G] = ???

    override def toString: String = s"GeneticAlgorithm(" +
        s"$genotypeBuilder, " +
        s"$ranker, " +
        s"$populationSize, " +
        s"$survivalRate, " +
        s"ParentSelector($parentSelector), " +
        s"SurvivorSelector($survivorSelector)" +
        ")"
}

object GeneticAlgorithm {
    def of[T, G <: Gene[T, G]](
        fitnessFunction: Genotype[T, G] => Double,
        genotypeBuilder: GenotypeBuilder[T, G]
    )(
        using populationSize: PopulationSize,
        survivalRate: SurvivalRate
    ): GeneticAlgorithmBuilder[T, G] = GeneticAlgorithmBuilder(fitnessFunction, genotypeBuilder)
}