package cl.ravenhill.plascevo
package evolution.engines

import evolution.config.{EvolutionConfiguration, GeneticPopulationConfiguration, PopulationSize, SelectionConfiguration, SurvivalRate}
import evolution.states.{EvolutionState, GeneticEvolutionState}
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
    override protected val ranker: IndividualRanker[T, G, Genotype[T, G]],
    protected val genotypeBuilder: GenotypeBuilder[T, G]
)(
    using initialState: GeneticEvolutionState[T, G] = GeneticEvolutionState.empty(ranker),
    protected val populationSize: PopulationSize = PopulationSize(100),
    protected val survivalRate: SurvivalRate = SurvivalRate(0.5),
    
) extends Evolver[T, G, Genotype[T, G], GeneticEvolutionState[T, G], L] {
//
//    /** The selector used to choose parents for reproduction in the evolutionary process.
//     *
//     * This selector is derived from the selection configuration and is responsible for selecting individuals from the
//     * current population to act as parents for generating the next generation.
//     */
//    val parentSelector: Selector[T, G, Genotype[T, G]] = selectionConfiguration.parentSelector
//
//    /** The selector used to choose offspring for inclusion in the next generation.
//     *
//     * This selector is derived from the selection configuration and is responsible for selecting the best offspring to
//     * include in the next generation, ensuring the continuation of the evolutionary process.
//     */
//    val offspringSelector: Selector[T, G, Genotype[T, G]] = selectionConfiguration.offspringSelector
}
